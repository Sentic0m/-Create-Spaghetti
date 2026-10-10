package net.senti.createspaghetti.Block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.senti.createspaghetti.client.animation.AnimatedBlockEntity;
import org.jetbrains.annotations.Nullable;

public class AnimatedTestBlockEntity extends BlockEntity implements AnimatedBlockEntity {
    @Nullable
    private String animation;
    // Spielzeit (level.getGameTime()) beim Start der Animation
    private long animationStart;

    public AnimatedTestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ANIMATED_TEST_BE.get(), pos, state);
    }

    /** Startet die Animation von vorn (nur auf dem Server aufrufen, wird an die Clients geschickt). */
    public void playAnimation(String name) {
        animation = name;
        animationStart = level.getGameTime();
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    public @Nullable String getAnimation() {
        return animation;
    }

    @Override
    public float getAnimationTicks(float partialTicks) {
        return level == null ? 0 : level.getGameTime() - animationStart + partialTicks;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        if (animation != null) {
            tag.putString("Animation", animation);
        }
        tag.putLong("AnimationStart", animationStart);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        animation = tag.contains("Animation") ? tag.getString("Animation") : null;
        animationStart = tag.getLong("AnimationStart");
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public AABB getRenderBoundingBox() {
        // Die Animation schwenkt das Modell über den Block hinaus
        return new AABB(worldPosition).inflate(1);
    }
}
