package net.createmod.ponder1710.enums;

import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import net.createmod.catnip.client.ConflictSafeKeyMapping;
import net.createmod.catnip.platform.CatnipClientServices;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

public enum PonderKeybinds {

	PONDER("ponder", GLFW.GLFW_KEY_W);

	public static final String CATEGORY = "key.categories.ponder";

	private final KeyMapping mapping;

	PonderKeybinds(String description, int defaultKey) {
		this.mapping = new ConflictSafeKeyMapping("key.ponder." + description, defaultKey, CATEGORY);
	}

	public static void register(Consumer<KeyMapping> registrationCallback) {
		for (PonderKeybinds key : values()) {
			registrationCallback.accept(key.mapping);
		}
	}

	public boolean isDown() {
		return !this.mapping.isUnbound() && CatnipClientServices.CLIENT_HOOKS.isKeyPressed(this.mapping);
	}

	public Component message() {
		return this.mapping.getTranslatedKeyMessage();
	}

}
