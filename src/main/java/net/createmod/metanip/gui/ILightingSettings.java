package net.createmod.metanip.gui;

import net.minecraft.client.renderer.RenderHelper;

public interface ILightingSettings {
    void applyLighting();

    ILightingSettings DEFAULT_3D   = RenderHelper::enableStandardItemLighting;
    ILightingSettings DEFAULT_FLAT = RenderHelper::enableGUIStandardItemLighting;
}
