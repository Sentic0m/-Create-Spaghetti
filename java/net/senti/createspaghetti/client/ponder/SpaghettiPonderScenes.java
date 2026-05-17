package net.senti.createspaghetti.client.ponder;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class SpaghettiPonderScenes {

    public static void spaghettiKineticBlock(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("spaghetti_kinetic_block", "Processing items with the Spaghetti Kinetic Block");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(5);


        scene.world().showSection(util.select().fromTo(0, 1, 0, 4, 2, 4), Direction.DOWN);
        scene.idle(10);

        BlockPos blockPos = util.grid().at(1, 1, 3);

        scene.world().modifyBlockEntity(blockPos, KineticBlockEntity.class, be -> be.setSpeed(64f));
        scene.idle(10);

        scene.overlay().showText(45)
                .text("")
                .pointAt(util.vector().blockSurface(blockPos.east(), Direction.NORTH))
                .attachKeyFrame();
        scene.idle(55);

        scene.overlay().showText(55)
                .text("")
                .pointAt(util.vector().blockSurface(blockPos.east(), Direction.NORTH))
                .attachKeyFrame();
        scene.idle(65);

        scene.rotateCameraY(164);

        scene.idle(47);

        BlockPos ziel1 = blockPos.east().below();
        BlockPos ziel2 = blockPos.west();

        scene.overlay().showText(60)
                .text("")
                .pointAt(util.vector().blockSurface(ziel2.above(2).east(2), Direction.NORTH))
                .attachKeyFrame();

        scene.idle(64);

        scene.overlay().showText(60)
                .text("")
                .pointAt(util.vector().blockSurface(ziel1.above(3).east(), Direction.NORTH))
                .attachKeyFrame();

        scene.idle(30);

        scene.rotateCameraY(-153);

        scene.idle(30);

        scene.overlay().showText(55)
                .text("")
                .pointAt(util.vector().blockSurface(ziel1.west().above(), Direction.NORTH))
                .attachKeyFrame();

        scene.idle(70);


    }
}
    //public static void spaghettiKineticBlockFluid(SceneBuilder scene, SceneBuildingUtil util) {
       // scene.title("spaghetti_kinetic_block", "Processing items with the Spaghetti Kinetic Block");
    //scene.configureBasePlate(0, 0, 5);
    //scene.idle(5);

    //scene.world().showSection(util.select().everywhere(), Direction.DOWN);
    //scene.idle(5);

    //scene.showBasePlate();
    //scene.idle(10);

    //BlockPos blockPos = util.grid().at(1, 1, 3);

    //scene.world().modifyBlockEntity(blockPos, KineticBlockEntity.class, be -> be.setSpeed(64f));
    //scene.idle(10);

    //scene.overlay().showText(45)
    //.text("")
    //.pointAt(util.vector().blockSurface(blockPos, Direction.NORTH))
    //.attachKeyFrame();
    //scene.idle(55);

    //scene.overlay().showText(55)
    //.text("")
    //.pointAt(util.vector().blockSurface(blockPos, Direction.NORTH))
    //.attachKeyFrame();
    //scene.idle(65);

    //BlockPos ziel1 = blockPos.east().below();
    //BlockPos ziel2 = blockPos.west();

    //scene.overlay().showText(60)
    //.text("")
    //.pointAt(util.vector().blockSurface(ziel2, Direction.NORTH))
    //.attachKeyFrame();

    //scene.overlay().showText(60)
    //.text("")
    //.pointAt(util.vector().blockSurface(ziel1, Direction.NORTH))
    //.attachKeyFrame();

    //scene.idle(70);
    //}
//
