package net.senti.createspaghetti.client.renderer;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.senti.createspaghetti.Block.entity.SpaghettiKineticBlockEntity;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.Direction;

import java.util.function.Consumer;

public class SpaghettiKineticVisual extends KineticBlockEntityVisual<SpaghettiKineticBlockEntity>
        implements SimpleDynamicVisual {

    protected final RotatingInstance rotatingModel;

    public SpaghettiKineticVisual(VisualizationContext context, SpaghettiKineticBlockEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);

        rotatingModel = instancerProvider()
                .instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF))
                .createInstance()
                .rotateToFace(Direction.NORTH, Direction.Axis.Y)
                .setup(blockEntity)
                .setPosition(getVisualPosition());



        rotatingModel.setChanged();
    }

    @Override
    public void beginFrame(DynamicVisual.Context context) {
        rotatingModel.setup(blockEntity).setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        relight(rotatingModel);
    }

    @Override
    protected void _delete() {
        rotatingModel.delete();
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {

    }
}