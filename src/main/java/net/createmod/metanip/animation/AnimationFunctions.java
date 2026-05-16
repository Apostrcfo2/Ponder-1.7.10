package net.createmod.metanip.animation;

import net.minecraft.util.MathHelper;

public class AnimationFunctions {
	// Approximations of some Web animation functions

	public static float easeOut(float t) {
		return MathHelper.sin((float)(Math.PI / 2) * t);
	}

	public static float easeInOut(float t) {
		return (float) Math.pow(MathHelper.sin((float)(Math.PI / 2) * t), 2);
	}

	public static float easeIn(float t) {
		return (float) Math.pow(t, 1.7);
	}
}
