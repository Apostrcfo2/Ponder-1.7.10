package net.createmod.ponder1710.foundation.instruction;

import net.createmod.ponder1710.foundation.PonderScene;
import net.createmod.ponder1710.foundation.element.AnimatedOverlayElementBase;
import net.createmod.ponder1710.foundation.element.AnimatedSceneElementBase;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public class HideAllInstruction extends TickingInstruction {

    private final ForgeDirection fadeOutTo;

    public HideAllInstruction(int fadeOutTicks, ForgeDirection fadeOutTo) {
        super(false, fadeOutTicks);
        this.fadeOutTo = fadeOutTo;
    }

    @Override
    protected void firstTick(PonderScene scene) {
        super.firstTick(scene);
        scene.getElements().forEach(element -> {
            if (element instanceof AnimatedSceneElementBase animatedSceneElement) {
                animatedSceneElement.setFade(1);
                if (fadeOutTo != null) {
                    Vec3 fadeVec = Vec3.createVectorHelper(
                        fadeOutTo.offsetX * 0.5,
                        fadeOutTo.offsetY * 0.5,
                        fadeOutTo.offsetZ * 0.5
                    );
                    animatedSceneElement.setFadeVec(fadeVec);
                } else {
                    animatedSceneElement.setFadeVec(null);
                }
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
        float fade = remainingTicks / (float) totalTicks;

        scene.forEach(AnimatedSceneElementBase.class, ase -> {
            ase.setFade(fade * fade);
            if (remainingTicks == 0) ase.setFade(0);
        });

        scene.forEach(AnimatedOverlayElementBase.class, aoe -> {
            aoe.setFade(fade * fade);
            if (remainingTicks == 0) aoe.setFade(0);
        });
    }
}
