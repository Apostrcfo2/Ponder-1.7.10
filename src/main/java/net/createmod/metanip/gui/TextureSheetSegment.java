package net.createmod.metanip.gui;

import net.createmod.metanip.render.BindableTexture;

public interface TextureSheetSegment extends BindableTexture {

    int getStartX();
    int getStartY();
    int getWidth();
    int getHeight();
}
