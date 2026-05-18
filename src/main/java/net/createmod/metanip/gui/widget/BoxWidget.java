package net.createmod.metanip.gui.widget;

import javax.annotation.Nullable;

import net.createmod.metanip.animation.LerpedFloat;
import net.createmod.metanip.data.Couple;
import net.createmod.metanip.gui.element.BoxElement;
import net.createmod.metanip.theme.Color;

public class BoxWidget extends AbstractSimiWidget {

    protected BoxElement box;
    @Nullable
    protected Couple<Color> customBorder;
    @Nullable
    protected Color customBackground;
    protected Couple<Color> colorIdle = COLOR_IDLE;
    protected Couple<Color> colorHover = COLOR_HOVER;
    protected Couple<Color> colorClick = COLOR_CLICK;
    protected Couple<Color> colorDisabled = COLOR_DISABLED;
    protected boolean animateColors = true;
    protected LerpedFloat colorAnimation = LerpedFloat.linear();
    protected Couple<Color> gradientColor;
    private Couple<Color> previousGradient;
    private Couple<Color> gradientTarget;
    protected LerpedFloat fade = LerpedFloat.linear().startWithValue(1);

    public BoxWidget(int x, int y, int width, int height) {
        super(x, y, width, height);
        box = new BoxElement().at(x, y).withBounds(width, height);
        previousGradient = gradientColor = gradientTarget = getColorIdle();
    }

    public <T extends BoxWidget> T withBounds(int width, int height) {
        this.width = width; this.height = height;
        return (T) this;
    }

    public <T extends BoxWidget> T withBorderColors(Couple<Color> colors) {
        this.customBorder = colors;
        updateGradientFromState();
        return (T) this;
    }

    public <T extends BoxWidget> T withCustomBackground(Color color) {
        this.customBackground = color;
        return (T) this;
    }

    public <T extends BoxWidget> T animateColors(boolean b) {
        this.animateColors = b;
        return (T) this;
    }

    public <T extends BoxWidget> T enableFade(float start, int ticks) {
        fade.startWithValue(start).chase(1, 1f / ticks, LerpedFloat.Chaser.LINEAR);
        return (T) this;
    }

    public <T extends BoxWidget> T fade(float value) {
        fade.startWithValue(value);
        return (T) this;
    }

    public void tick() {
        colorAnimation.tickChaser();
        fade.tickChaser();
    }

    @Override
    protected void doRender(int mouseX, int mouseY, float partialTicks) {
        float fadeValue = fade.getValue(partialTicks);
        if (fadeValue < .1f) return;

        updateGradientFromState();
        box.withAlpha(fadeValue)
            .withBackground(customBackground != null ? customBackground : BoxElement.COLOR_BACKGROUND_TRANSPARENT)
            .gradientBorder(gradientColor)
            .at(xPosition, yPosition, z)
            .withBounds(width, height)
            .render();
    }

    public void updateGradientFromState() {
        gradientTarget = getColorForState();
        gradientColor = gradientTarget;
    }

    public void animateGradientFromState() {
        startGradientAnimation(getColorForState());
    }

    protected void startGradientAnimation(Couple<Color> target, double expSpeed) {
        if (!animateColors) return;
        colorAnimation.startWithValue(1).chase(0, expSpeed, LerpedFloat.Chaser.EXP).tickChaser();
        previousGradient = gradientColor;
        gradientTarget = target;
    }

    protected void startGradientAnimation(Couple<Color> target) {
        startGradientAnimation(target, 0.6);
    }

    protected Couple<Color> getColorForState() {
        if (!enabled) return getColorDisabled();
        if (customBorder != null) return isHovered ? customBorder.map(Color::darker) : customBorder;
        return isHovered ? getColorHover() : getColorIdle();
    }

    public Couple<Color> getColorIdle()     { return colorIdle; }
    public Couple<Color> getColorHover()    { return colorHover; }
    public Couple<Color> getColorClick()    { return colorClick; }
    public Couple<Color> getColorDisabled() { return colorDisabled; }
}
