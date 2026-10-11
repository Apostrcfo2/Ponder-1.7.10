package net.createmod.metanip.animation;

import javax.annotation.Nullable;

import net.createmod.metanip.math.AngleHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;

public class LerpedFloat {

    protected Interpolator interpolator;
    protected float previousValue;
    protected float value;

    @Nullable
    protected Chaser chaseFunction;
    protected float chaseTarget;
    protected float chaseSpeed;
    protected boolean angularChase;
    protected boolean forcedSync;

    public LerpedFloat(Interpolator interpolator) {
        this.interpolator = interpolator;
        startWithValue(0);
        forcedSync = true;
    }

    public static LerpedFloat linear() {
        return new LerpedFloat((p, c, t) -> (float) (c + (t - c) * p));
    }

    public static LerpedFloat angular() {
        LerpedFloat lerpedFloat = new LerpedFloat(AngleHelper::angleLerp);
        lerpedFloat.angularChase = true;
        return lerpedFloat;
    }

    public LerpedFloat startWithValue(double value) {
        float f = (float) value;
        this.previousValue = f;
        this.chaseTarget = f;
        this.value = f;
        return this;
    }

    public LerpedFloat chase(double value, double speed, Chaser chaseFunction) {
        updateChaseTarget((float) value);
        this.chaseSpeed = (float) speed;
        this.chaseFunction = chaseFunction;
        return this;
    }

    public LerpedFloat chaseTimed(double value, int ticks) {
        double diff = value - this.value;
        return chase(value, Math.abs(diff / ticks), Chaser.LINEAR);
    }

    public LerpedFloat disableSmartAngleChasing() {
        angularChase = false;
        return this;
    }

    public void updateChaseTarget(float target) {
        if (angularChase)
            target = value + AngleHelper.getShortestAngleDiff(value, target);
        this.chaseTarget = target;
    }

    public boolean updateChaseSpeed(double speed) {
        float prevSpeed = this.chaseSpeed;
        this.chaseSpeed = (float) speed;
        return Math.abs(prevSpeed - speed) > 1e-5;
    }

    public void tickChaser() {
        previousValue = value;
        if (chaseFunction == null)
            return;
        if (Math.abs(value - chaseTarget) < 1e-5) {
            value = chaseTarget;
            return;
        }
        value = chaseFunction.chase(value, chaseSpeed, chaseTarget);
    }

    public void setValueNoUpdate(double value) {
        this.value = (float) value;
    }

    public void setValue(double value) {
        this.previousValue = this.value;
        this.value = (float) value;
    }

    public float getValue() {
        return getValue(1);
    }

    public float getValue(float partialTicks) {
        return interpolator.interpolate(partialTicks, previousValue, value);
    }

    public boolean settled() {
        return Math.abs(previousValue - value) < 1e-5
            && (chaseFunction == null || Math.abs(value - chaseTarget) < 1e-5);
    }

    public float getChaseTarget() {
        return chaseTarget;
    }

    public void forceNextSync() {
        forcedSync = true;
    }

    public NBTTagCompound writeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setFloat("Speed", chaseSpeed);
        nbt.setFloat("Target", chaseTarget);
        nbt.setFloat("Value", value);
        if (forcedSync)
            nbt.setBoolean("Force", true);
        forcedSync = false;
        return nbt;
    }

    public void readNBT(NBTTagCompound nbt, boolean clientPacket) {
        if (!clientPacket || nbt.hasKey("Force"))
            startWithValue(nbt.getFloat("Value"));
        readChaser(nbt);
    }

    protected void readChaser(NBTTagCompound nbt) {
        chaseSpeed = nbt.getFloat("Speed");
        chaseTarget = nbt.getFloat("Target");
    }

    @FunctionalInterface
    public interface Interpolator {
        float interpolate(double progress, double current, double target);
    }

    @FunctionalInterface
    public interface Chaser {
        Chaser IDLE = (c, s, t) -> (float) c;
        Chaser EXP = exp(Double.MAX_VALUE);
        Chaser LINEAR = (c, s, t) -> {
            double diff = t - c;
            return (float) (c + Math.max(-s, Math.min(s, diff)));
        };

        static Chaser exp(double maxEffectiveSpeed) {
            return (c, s, t) -> {
                double diff = (t - c) * s;
                return (float) (c + Math.max(-maxEffectiveSpeed, Math.min(maxEffectiveSpeed, diff)));
            };
        }

        float chase(double current, double speed, double target);
    }
}
