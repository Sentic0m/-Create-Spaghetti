package net.senti.createspaghetti.client.animation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Laufzeit für Modelle aus dem Blockbench-Plugin "Block Animator" (wird bei jedem Export überschrieben).
 * Liest assets/<ns>/animations/block/<name>.json und zeichnet jeden Knochen als eigenes Blockmodell.
 */
public final class AnimatedBlockModel {
    private static final Map<ResourceLocation, AnimatedBlockModel> CACHE = new ConcurrentHashMap<>();
    private static final int STRIDE = 9;

    private final ResourceLocation[] models;
    private final float[][] origins;
    private final int[][] children;
    private final int[] roots;
    private final float[] rest;
    private final Map<String, Clip> clips = new HashMap<>();

    private record Clip(String loop, float length, float[][] frames) {
    }

    public static AnimatedBlockModel get(ResourceLocation id) {
        return CACHE.computeIfAbsent(id, AnimatedBlockModel::load);
    }

    /** Wird beim Neuladen der Ressourcen aufgerufen (siehe registerModels der generierten Klassen). */
    public static void clearCache() {
        CACHE.clear();
    }

    private static AnimatedBlockModel load(ResourceLocation id) {
        ResourceLocation file = new ResourceLocation(id.getNamespace(), "animations/block/" + id.getPath() + ".json");
        try (Reader reader = Minecraft.getInstance().getResourceManager().openAsReader(file)) {
            return new AnimatedBlockModel(JsonParser.parseReader(reader).getAsJsonObject());
        } catch (IOException e) {
            throw new IllegalStateException("Block-Animation nicht gefunden: " + file, e);
        }
    }

    private AnimatedBlockModel(JsonObject json) {
        JsonArray bones = json.getAsJsonArray("bones");
        int count = bones.size();
        models = new ResourceLocation[count];
        origins = new float[count][];
        List<List<Integer>> childLists = new ArrayList<>();
        List<Integer> rootList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            childLists.add(new ArrayList<>());
        }
        for (int i = 0; i < count; i++) {
            JsonObject bone = bones.get(i).getAsJsonObject();
            origins[i] = floats(bone.getAsJsonArray("origin"));
            if (bone.has("model")) {
                models[i] = new ResourceLocation(bone.get("model").getAsString());
            }
            int parent = bone.get("parent").getAsInt();
            (parent < 0 ? rootList : childLists.get(parent)).add(i);
        }
        children = new int[count][];
        for (int i = 0; i < count; i++) {
            children[i] = childLists.get(i).stream().mapToInt(Integer::intValue).toArray();
        }
        roots = rootList.stream().mapToInt(Integer::intValue).toArray();
        rest = floats(json.getAsJsonArray("rest"));

        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("animations").entrySet()) {
            JsonObject clip = entry.getValue().getAsJsonObject();
            JsonArray frameArray = clip.getAsJsonArray("frames");
            float[][] frames = new float[frameArray.size()][];
            for (int i = 0; i < frames.length; i++) {
                frames[i] = floats(frameArray.get(i).getAsJsonArray());
            }
            clips.put(entry.getKey(), new Clip(clip.get("loop").getAsString(), clip.get("length").getAsFloat(), frames));
        }
    }

    private static float[] floats(JsonArray array) {
        float[] out = new float[array.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = array.get(i).getAsFloat();
        }
        return out;
    }

    public boolean hasAnimation(String animation) {
        return clips.containsKey(animation);
    }

    /** Länge der Animation in Ticks (0, wenn es sie nicht gibt). */
    public float getLength(String animation) {
        Clip clip = clips.get(animation);
        return clip == null ? 0 : clip.length();
    }

    /** true, wenn eine nicht wiederholte Animation ("once"/"hold") zu Ende ist. */
    public boolean isFinished(String animation, float ticks) {
        Clip clip = clips.get(animation);
        return clip == null || !clip.loop().equals("loop") && ticks >= clip.length();
    }

    /**
     * Zeichnet das Modell. Das Modell ist wie in Blockbench nach Norden ausgerichtet und wird um die
     * Blockmitte in {@code facing} gedreht.
     *
     * @param animation Name der Animation oder null für die Grundpose
     * @param ticks     Zeit seit Animationsstart in Ticks, inklusive partialTicks
     */
    public void render(PoseStack ms, VertexConsumer consumer, @Nullable String animation, float ticks,
                       Direction facing, int light, int overlay) {
        float[] pose = pose(animation, ticks);
        float yRot = 180 - facing.toYRot();

        ms.pushPose();
        ms.translate(.5, 0, .5);
        ms.mulPose(new Quaternionf().rotateY(yRot * Mth.DEG_TO_RAD));
        ms.translate(-.5, 0, -.5);
        // Drehung ohne Kamera, nur für die Schattierung der Flächen
        Quaternionf world = new Quaternionf().rotateY(yRot * Mth.DEG_TO_RAD);
        RandomSource random = RandomSource.create();
        for (int root : roots) {
            renderBone(root, pose, ms, world, consumer, random, light, overlay);
        }
        ms.popPose();
    }

    private float[] pose(@Nullable String animation, float ticks) {
        Clip clip = animation == null ? null : clips.get(animation);
        if (clip == null) {
            return rest;
        }
        float t = Math.max(0, ticks);
        switch (clip.loop()) {
            case "loop" -> t = clip.length() > 0 ? t % clip.length() : 0;
            case "hold" -> t = Math.min(t, clip.length());
            default -> {
                if (t > clip.length()) {
                    return rest;
                }
            }
        }
        // Ein Bild pro Tick
        int last = clip.frames().length - 1;
        int i0 = Math.min((int) t, last);
        int i1 = Math.min(i0 + 1, last);
        float alpha = Math.min(t - i0, 1);
        float[] a = clip.frames()[i0];
        float[] b = clip.frames()[i1];
        float[] out = new float[a.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = Mth.lerp(alpha, a[i], b[i]);
        }
        return out;
    }

    private void renderBone(int bone, float[] pose, PoseStack ms, Quaternionf parentWorld, VertexConsumer consumer,
                            RandomSource random, int light, int overlay) {
        int o = bone * STRIDE;
        Quaternionf rotation = new Quaternionf()
                .rotateZ(pose[o + 5] * Mth.DEG_TO_RAD)
                .rotateY(pose[o + 4] * Mth.DEG_TO_RAD)
                .rotateX(pose[o + 3] * Mth.DEG_TO_RAD);
        Quaternionf world = new Quaternionf(parentWorld).mul(rotation);

        ms.pushPose();
        ms.translate(pose[o] / 16f, pose[o + 1] / 16f, pose[o + 2] / 16f);
        ms.mulPose(rotation);
        ms.scale(pose[o + 6], pose[o + 7], pose[o + 8]);

        for (int child : children[bone]) {
            renderBone(child, pose, ms, world, consumer, random, light, overlay);
        }

        if (models[bone] != null) {
            ms.pushPose();
            ms.translate(-origins[bone][0] / 16f, -origins[bone][1] / 16f, -origins[bone][2] / 16f);
            BakedModel model = Minecraft.getInstance().getModelManager().getModel(models[bone]);
            random.setSeed(42L);
            renderQuads(model.getQuads(null, null, random, ModelData.EMPTY, RenderType.cutout()), ms, world, consumer, light, overlay);
            for (Direction side : Direction.values()) {
                random.setSeed(42L);
                renderQuads(model.getQuads(null, side, random, ModelData.EMPTY, RenderType.cutout()), ms, world, consumer, light, overlay);
            }
            ms.popPose();
        }
        ms.popPose();
    }

    // Schattierung wie bei normalen Blöcken (oben 100 %, N/S 80 %, O/W 60 %, unten 50 %), auch für schräge Flächen
    private static void renderQuads(List<BakedQuad> quads, PoseStack ms, Quaternionf world, VertexConsumer consumer,
                                    int light, int overlay) {
        for (BakedQuad quad : quads) {
            float shade = 1;
            if (quad.isShade()) {
                Direction dir = quad.getDirection();
                Vector3f n = world.transform(new Vector3f(dir.getStepX(), dir.getStepY(), dir.getStepZ()));
                shade = Math.min(n.x * n.x * .6f + n.y * n.y * ((3 + n.y) / 4) + n.z * n.z * .8f, 1);
            }
            consumer.putBulkData(ms.last(), quad, shade, shade, shade, light, overlay);
        }
    }
}
