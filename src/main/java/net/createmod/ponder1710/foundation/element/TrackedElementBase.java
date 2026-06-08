package net.createmod.ponder1710.foundation.element;

import java.lang.ref.WeakReference;
import java.util.function.Consumer;

import net.createmod.ponder1710.api.element.TrackedElement;
import net.createmod.ponder1710.api.level.PonderLevel;

// MultiBufferSource/GuiGraphics/RenderType not available in 1.7.10 - GL11 direct
public abstract class TrackedElementBase<T> extends PonderElementBase implements TrackedElement<T> {

    private final WeakReference<T> reference;

    public TrackedElementBase(T wrapped) {
        this.reference = new WeakReference<>(wrapped);
    }

    @Override
    public void ifPresent(Consumer<T> func) {
        T resolved = reference.get();
        if (resolved == null) return;
        func.accept(resolved);
    }

    @Override public void renderFirst(PonderLevel world, float pt) {}
    @Override public void renderLayer(PonderLevel world, float pt) {}
    @Override public void renderLast(PonderLevel world, float pt)  {}
}
