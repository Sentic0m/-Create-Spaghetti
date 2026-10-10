package net.senti.createspaghetti.client.animation;

import org.jetbrains.annotations.Nullable;

/**
 * Für Block-Entities, die mit einem vom Blockbench-Plugin "Block Animator" erzeugten Renderer
 * gezeichnet werden (wird bei jedem Export überschrieben).
 */
public interface AnimatedBlockEntity {
    /** Name der laufenden Animation (Konstanten der generierten Klasse) oder null für die Grundpose. */
    @Nullable
    String getAnimation();

    /**
     * Zeit seit Start der Animation in Ticks. Typisch:
     * {@code level.getGameTime() - animationStartTick + partialTicks}
     */
    float getAnimationTicks(float partialTicks);
}
