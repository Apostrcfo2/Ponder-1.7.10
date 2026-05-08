package net.createmod.ponder1710.foundation.instruction;

import net.createmod.ponder1710.api.element.AnimatedSceneElement;
import net.createmod.ponder1710.api.element.ElementLink;
import net.createmod.ponder1710.foundation.PonderScene;
import net.createmod.ponder1710.foundation.element.ElementLinkImpl;

// import net.minecraft.core.Direction; // ForgeDirection in 1.7.10
// import net.minecraft.world.phys.Vec3; // net.minecraft.util.Vec3 in 1.7.10
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class FadeIntoSceneInstruction<T extends AnimatedSceneElement> extends TickingInstruction {

    protected ForgeDirection fadeInFrom;
    protected T element;
    private ElementLink<T> elementLink;

    public FadeIntoSceneInstruction(int fadeInTicks, ForgeDirection fadeInFrom, T element) {
        super(false, fadeInTicks);
        this.fadeInFrom = fadeInFrom;
        this.element = element;
    }

    @Override
    protected void firstTick(PonderScene scene) {
        super.firstTick(scene);
        scene.addElement(element);
        element.setVisible(true);
        element.setFade(0);
        if (fadeInFrom == null) {
            element.setFadeVec(Vec3.createVectorHelper(0, 0, 0));
        } else {
            // Direction.getNormal() -> ForgeDirection offset in 1.7.10
            element.setFadeVec(Vec3.createVectorHelper(
                fadeInFrom.offsetX * 0.5,
                fadeInFrom.offsetY * 0.5,
                fadeInFrom.offsetZ * 0.5
            ));
        }
        if (elementLink != null)
            scene.linkElement(element, elementLink);
    }

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        float fade = totalTicks == 0 ? 1 : (remainingTicks / (float) totalTicks);
        element.setFade(1 - fade * fade);
        if (remainingTicks == 0) {
            if (totalTicks == 0)
                element.setFade(1);
            element.setFade(1);
        }
    }

    public ElementLink<T> createLink(PonderScene scene) {
        elementLink = new ElementLinkImpl<>(getElementClass());
        scene.linkElement(element, elementLink);
        return elementLink;
    }

    protected abstract Class<T> getElementClass();
}
