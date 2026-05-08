package net.createmod.ponder1710.foundation.instruction;

import net.createmod.ponder1710.api.element.AnimatedSceneElement;
import net.createmod.ponder1710.api.element.ElementLink;
import net.createmod.ponder1710.foundation.PonderScene;

// import net.minecraft.core.Direction; // ForgeDirection in 1.7.10
// import net.minecraft.world.phys.Vec3; // net.minecraft.util.Vec3 in 1.7.10
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public class FadeOutOfSceneInstruction<T extends AnimatedSceneElement> extends TickingInstruction {

    private final ForgeDirection fadeOutTo;
    private final ElementLink<T> link;
    private T element;

    public FadeOutOfSceneInstruction(int fadeOutTicks, ForgeDirection fadeOutTo, ElementLink<T> link) {
        super(false, fadeOutTicks);
        this.fadeOutTo = fadeOutTo == null ? null : ForgeDirection.values()[fadeOutTo.getOpposite().ordinal()];
        this.link = link;
    }

    @Override
    protected void firstTick(PonderScene scene) {
        super.firstTick(scene);
        element = scene.resolve(link);
        if (element == null)
            return;
        element.setVisible(true);
        element.setFade(1);
        if (fadeOutTo == null) {
            element.setFadeVec(Vec3.createVectorHelper(0, 0, 0));
        } else {
            element.setFadeVec(Vec3.createVectorHelper(
                fadeOutTo.offsetX * 0.5,
                fadeOutTo.offsetY * 0.5,
                fadeOutTo.offsetZ * 0.5
            ));
        }
    }

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        if (element == null)
            return;
        float fade = (remainingTicks / (float) totalTicks);
        element.setFade(1 - (1 - fade) * (1 - fade));
        if (remainingTicks == 0) {
            element.setVisible(false);
            element.setFade(0);
        }
    }
}
