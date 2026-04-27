package su.sergiusonesimus.recreate.content.ponder;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class PonderScene {

    protected float rotation = 0;

    public void tick() {
        rotation += 1f;
        if (rotation >= 360) rotation = 0;
    }

    public abstract void render(float partialTicks);
}
