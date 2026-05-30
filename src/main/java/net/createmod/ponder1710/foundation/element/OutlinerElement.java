package net.createmod.ponder1710.foundation.element;

// import net.createmod.metanip.outliner.Outline.OutlineParams; // TODO: catnip not available
// import net.createmod.metanip.outliner.Outliner; // TODO: catnip not available
// Outliner is a catnip utility for drawing outlines in the world
// Will need to be reimplemented using 1.7.10 rendering system

import net.createmod.ponder1710.foundation.PonderScene;

public class OutlinerElement extends AnimatedSceneElementBase {

    // TODO: Function<Outliner, OutlineParams> not available - catnip not ported yet
    // private final Function<Outliner, OutlineParams> outlinerCall;
    private int overrideColor;

    public OutlinerElement(/* Function<Outliner, OutlineParams> outlinerCall */) {
        // this.outlinerCall = outlinerCall;
        this.overrideColor = -1;
    }

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        if (fadeValue < 1 / 16f)
            return;

        // OutlineParams params = outlinerCall.apply(scene.getOutliner());
        // if (overrideColor != -1)
        //     params.colored(overrideColor);
    }

    public void setColor(int overrideColor) {
        this.overrideColor = overrideColor;
    }
}
