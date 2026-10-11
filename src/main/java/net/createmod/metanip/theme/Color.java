package net.createmod.metanip.theme;

import java.util.function.UnaryOperator;

import javax.annotation.Nonnull;

import org.joml.Vector3f;

import com.google.common.hash.Hashing;

import net.createmod.metanip.data.Couple;

// import net.minecraft.network.chat.Style; // not available in 1.7.10
// import net.minecraft.util.Mth; // MathHelper in 1.7.10
// import net.minecraft.world.phys.Vec3; // net.minecraft.util.Vec3 in 1.7.10
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

@SuppressWarnings("PointlessBitwiseExpression")
public class Color {
    public final static Color TRANSPARENT_BLACK = new Color(0, 0, 0, 0).setImmutable();
    public final static Color BLACK = new Color(0, 0, 0).setImmutable();
    public final static Color WHITE = new Color(255, 255, 255).setImmutable();
    public final static Color RED = new Color(255, 0, 0).setImmutable();
    public final static Color GREEN = new Color(0, 255, 0).setImmutable();
    public final static Color PURPLE = new Color(128, 0, 128).setImmutable();
    public final static Color SPRING_GREEN = new Color(0, 255, 187).setImmutable();

    protected boolean mutable = true;
    protected int value;

    public Color(int r, int g, int b) {
        this(r, g, b, 0xff);
    }

    public Color(int r, int g, int b, int a) {
        value = ((a & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
    }

    public Color(float r, float g, float b, float a) {
        this(
            (int) (0.5 + 0xff * MathHelper.clamp_float(r, 0, 1)),
            (int) (0.5 + 0xff * MathHelper.clamp_float(g, 0, 1)),
            (int) (0.5 + 0xff * MathHelper.clamp_float(b, 0, 1)),
            (int) (0.5 + 0xff * MathHelper.clamp_float(a, 0, 1))
        );
    }

    public Color(int argb) { value = argb; }

    public Color(int argb, boolean hasAlpha) {
        value = hasAlpha ? argb : argb | 0xff_000000;
    }

    public Color copy() { return copy(true); }

    public Color copy(boolean mutable) {
        return mutable ? new Color(value) : new Color(value).setImmutable();
    }

    public Color setImmutable() {
        this.mutable = false;
        return this;
    }

    public int getRed()   { return (getRGB() >> 16) & 0xff; }
    public int getGreen() { return (getRGB() >> 8) & 0xff; }
    public int getBlue()  { return getRGB() & 0xff; }
    public int getAlpha() { return (getRGB() >> 24) & 0xff; }

    public float getRedAsFloat()   { return getRed() / 255f; }
    public float getGreenAsFloat() { return getGreen() / 255f; }
    public float getBlueAsFloat()  { return getBlue() / 255f; }
    public float getAlphaAsFloat() { return getAlpha() / 255f; }

    public int getRGB() { return value; }

    public Vec3 asVector() {
        return Vec3.createVectorHelper(getRedAsFloat(), getGreenAsFloat(), getBlueAsFloat());
    }

    public Vector3f asVectorF() {
        return new Vector3f(getRedAsFloat(), getGreenAsFloat(), getBlueAsFloat());
    }

    // Style not available in 1.7.10
    // public Style asStyle() { return Style.EMPTY.withColor(this.value); }

    public Color setRed(int r)   { return ensureMutable().setRedUnchecked(r); }
    public Color setGreen(int g) { return ensureMutable().setGreenUnchecked(g); }
    public Color setBlue(int b)  { return ensureMutable().setBlueUnchecked(b); }
    public Color setAlpha(int a) { return ensureMutable().setAlphaUnchecked(a); }

    public Color setRed(float r)   { return ensureMutable().setRedUnchecked((int)(0xff * MathHelper.clamp_float(r, 0, 1))); }
    public Color setGreen(float g) { return ensureMutable().setGreenUnchecked((int)(0xff * MathHelper.clamp_float(g, 0, 1))); }
    public Color setBlue(float b)  { return ensureMutable().setBlueUnchecked((int)(0xff * MathHelper.clamp_float(b, 0, 1))); }
    public Color setAlpha(float a) { return ensureMutable().setAlphaUnchecked((int)(0xff * MathHelper.clamp_float(a, 0, 1))); }

    public Color scaleAlpha(float factor) {
        return ensureMutable().setAlphaUnchecked((int)(getAlpha() * MathHelper.clamp_float(factor, 0, 1)));
    }

    public Color scaleAlphaForText(float factor) {
        return ensureMutable().setAlphaUnchecked(Math.max(0x05, (int)(getAlpha() * MathHelper.clamp_float(factor, 0, 1))));
    }

    public Color mixWith(Color other, float weight) {
        return ensureMutable()
            .setRedUnchecked((int)(getRed() + (other.getRed() - getRed()) * weight))
            .setGreenUnchecked((int)(getGreen() + (other.getGreen() - getGreen()) * weight))
            .setBlueUnchecked((int)(getBlue() + (other.getBlue() - getBlue()) * weight))
            .setAlphaUnchecked((int)(getAlpha() + (other.getAlpha() - getAlpha()) * weight));
    }

    public Color darker() {
        int a = getAlpha();
        return ensureMutable().mixWith(BLACK, .25f).setAlphaUnchecked(a);
    }

    public Color brighter() {
        int a = getAlpha();
        return ensureMutable().mixWith(WHITE, .25f).setAlphaUnchecked(a);
    }

    public Color setValue(int value) { return ensureMutable().setValueUnchecked(value); }

    public Color modifyValue(UnaryOperator<Integer> function) {
        int newValue = function.apply(value);
        if (newValue == value) return this;
        return ensureMutable().setValueUnchecked(newValue);
    }

    public Color ensureMutable() {
        return this.mutable ? this : new Color(this.value);
    }

    protected Color setRedUnchecked(int r)   { value = (value & 0xff_00ffff) | ((r & 0xff) << 16); return this; }
    protected Color setGreenUnchecked(int g) { value = (value & 0xff_ff00ff) | ((g & 0xff) << 8);  return this; }
    protected Color setBlueUnchecked(int b)  { value = (value & 0xff_ffff00) | (b & 0xff);          return this; }
    protected Color setAlphaUnchecked(int a) { value = (value & 0x00_ffffff) | ((a & 0xff) << 24);  return this; }
    protected Color setValueUnchecked(int v) { value = v; return this; }

    public static Color mixColors(@Nonnull Color c1, @Nonnull Color c2, float w) {
        return new Color(
            (int)(c1.getRed()   + (c2.getRed()   - c1.getRed())   * w),
            (int)(c1.getGreen() + (c2.getGreen() - c1.getGreen()) * w),
            (int)(c1.getBlue()  + (c2.getBlue()  - c1.getBlue())  * w),
            (int)(c1.getAlpha() + (c2.getAlpha() - c1.getAlpha()) * w)
        );
    }

    public static Color mixColors(@Nonnull Couple<Color> colors, float w) {
        return mixColors(colors.getFirst(), colors.getSecond(), w);
    }

    public static int mixColors(int c1, int c2, float w) {
        int a1 = (c1 >> 24), r1 = (c1 >> 16) & 0xFF, g1 = (c1 >> 8) & 0xFF, b1 = c1 & 0xFF;
        int a2 = (c2 >> 24), r2 = (c2 >> 16) & 0xFF, g2 = (c2 >> 8) & 0xFF, b2 = c2 & 0xFF;
        return ((int)(a1 + (a2-a1)*w) << 24) | ((int)(r1 + (r2-r1)*w) << 16)
             | ((int)(g1 + (g2-g1)*w) << 8)  |  (int)(b1 + (b2-b1)*w);
    }

    public static Color rainbowColor(int timeStep) {
        int local = Math.abs(timeStep) % 1536;
        int inPhase = local % 256;
        int phase = local / 256;
        return new Color(colorInPhase(phase+4, inPhase), colorInPhase(phase+2, inPhase), colorInPhase(phase, inPhase));
    }

    private static int colorInPhase(int phase, int progress) {
        phase = phase % 6;
        if (phase <= 1) return 0;
        if (phase == 2) return progress;
        if (phase <= 4) return 255;
        return 255 - progress;
    }

    public static Color generateFromLong(long l) {
        return rainbowColor(Hashing.crc32().hashLong(l).asInt()).mixWith(WHITE, 0.5f);
    }
}
