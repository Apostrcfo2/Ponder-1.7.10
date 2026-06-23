package net.createmod.ponder1710.enums;


import net.createmod.ponder1710.Ponder;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public enum PonderSpecialTextures {

    BLANK("blank.png"),

    ;

    public static final String ASSET_PATH = "textures/special/";
    private final ResourceLocation location;

    PonderSpecialTextures(String filename) {
        location = Ponder.asResource(ASSET_PATH + filename);
    }

    public void bind() {
        // RenderSystem.setShaderTexture not available in 1.7.10
        Minecraft.getMinecraft().getTextureManager().bindTexture(location);
    }

    public ResourceLocation getLocation() {
        return location;
    }
}
