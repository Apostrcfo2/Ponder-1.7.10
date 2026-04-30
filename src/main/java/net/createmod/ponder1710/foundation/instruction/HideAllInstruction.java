package net.createmod.ponder1710.foundation.instruction;

import net.createmod.ponder1710.foundation.PonderScene;
import net.createmod.ponder1710.foundation.element.AnimatedOverlayElementBase;
import net.createmod.ponder1710.foundation.element.AnimatedSceneElementBase;

// TODO: Direction and Vec3 fadeOutTo not available in 1.7.10 form yet
// Original used net.minecraft.core.Direction and net.minecraft.world.phys.Vec3
public class HideAllInstruction extends TickingInstruction {

    // private final Direction fadeOutTo;
    // Replaced with null for now - fade direction not yet implemented

    public HideAllInstruction(int fadeOutTicks, /* Direction fadeOutTo */ Object fadeOutTo) {
        super(false, fadeOutTicks);
        // this.fadeOutTo = fadeOutTo;
    }

    @Override
    protected void firstTick(PonderScene scene) {
        super.firstTick(scene);
        scene.getElements()
            .forEach(element -> {
                if (element instanceof AnimatedSceneElementBase animatedSceneElement) {
                    animatedSceneElement.setFade(1);
                    // TODO: setFadeVec not yet implemented - requires Direction/Vec3 port
                    // animatedSceneElement.setFadeVec(fadeOutTo == null ? null : Vec3.atLowerCornerOf(fadeOutTo.getNormal()).scale(.5f));
                } else if (element instanceof AnimatedOverlayElementBase animatedSceneElement) {
                    animatedSceneElement.setFade(1);
                } else {
                    element.setVisible(false);
                }
            });
    }

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        float fade = (remainingTicks / (float) totalTicks);

        scene.forEach(AnimatedSceneElementBase.class, ase -> {
            ase.setFade(fade * fade);
            if (remainingTicks == 0)
                ase.setFade(0);
        });

        scene.forEach(AnimatedOverlayElementBase.class, aoe -> {
            aoe.setFade(fade * fade);
            if (remainingTicks == 0)
                aoe.setFade(0);
        });
    }
}
