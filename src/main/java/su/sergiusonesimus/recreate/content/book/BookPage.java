package su.sergiusonesimus.recreate.content.book;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import su.sergiusonesimus.recreate.ReCreate;

@SideOnly(Side.CLIENT)
public class BookPage {

    private final String titleKey;
    private final String descKey;
    private final String imagePath;

    public BookPage(String titleKey, String descKey, String imagePath) {
        this.titleKey = titleKey;
        this.descKey = descKey;
        this.imagePath = imagePath;
    }

    public void draw(GuiScreen gui, FontRenderer font, int bookX, int bookY) {
        int contentX = bookX + 16;
        int contentY = bookY + 16;
        int pageWidth = 240;

        String title = I18n.format(titleKey);
        font.drawString("\u00a7l" + title, contentX, contentY, 0x3b2a1a);
        contentY += 14;

        gui.drawHorizontalLine(contentX, contentX + pageWidth - 32, contentY, 0x3b2a1a);
        contentY += 6;

        if (imagePath != null) {
            ResourceLocation image = new ResourceLocation(ReCreate.ID, imagePath);
            try {
                Minecraft.getMinecraft().getTextureManager().bindTexture(image);
                GL11.glColor4f(1f, 1f, 1f, 1f);
                gui.drawTexturedModalRect(contentX + pageWidth / 2 - 40, contentY, 0, 0, 80, 60);
                contentY += 68;
            } catch (Exception e) {
                // Image not found, skip
            }
        }

        String desc = I18n.format(descKey);
        font.drawSplitString(desc, contentX, contentY, pageWidth - 32, 0x3b2a1a);
    }
}
