package su.sergiusonesimus.recreate.content.book;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class IndexPage extends BookPage {

    private List<String> topics = new ArrayList<>();
    private ReCreateBookGui gui;

    public IndexPage() {
        super("recreate.book.index.title", "", null);
        topics.add("recreate.book.kinetic.title");
        topics.add("recreate.book.shaft.title");
        topics.add("recreate.book.cogwheel.title");
        topics.add("recreate.book.creative_motor.title");
        topics.add("recreate.book.water_wheel.title");
        topics.add("recreate.book.clutch.title");
        topics.add("recreate.book.gearshift.title");
        topics.add("recreate.book.gearbox.title");
        topics.add("recreate.book.mechanical_bearing.title");
        topics.add("recreate.book.windmill_bearing.title");
        topics.add("recreate.book.sail.title");
        topics.add("recreate.book.mechanical_piston.title");
    }

    public void setGui(ReCreateBookGui gui) {
        this.gui = gui;
    }

    public List<String> getTopics() {
        return topics;
    }

    @Override
    public void draw(GuiScreen screen, FontRenderer font, int bookX, int bookY) {
        int contentX = bookX + 16;
        int contentY = bookY + 16;

        String title = net.minecraft.client.resources.I18n.format("recreate.book.index.title");
        font.drawString("\u00a7l" + title, contentX, contentY, 0x3b2a1a);
        contentY += 14;

        net.minecraft.client.renderer.Tessellator t = net.minecraft.client.renderer.Tessellator.instance;
        t.startDrawingQuads();
        t.setColorOpaque_I(0x3b2a1a);
        t.addVertexWithUV(contentX, contentY + 1, 0, 0, 0);
        t.addVertexWithUV(contentX + 208, contentY + 1, 0, 0, 0);
        t.addVertexWithUV(contentX + 208, contentY, 0, 0, 0);
        t.addVertexWithUV(contentX, contentY, 0, 0, 0);
        t.draw();
        contentY += 8;

        for (int i = 0; i < topics.size(); i++) {
            String label = net.minecraft.client.resources.I18n.format(topics.get(i));
            font.drawString("\u00a72> \u00a70" + label, contentX, contentY, 0x3b2a1a);
            contentY += 11;
        }
    }

    public int getClickedTopic(int mouseX, int mouseY, int bookX, int bookY) {
        int contentX = bookX + 16;
        int contentY = bookY + 38;

        for (int i = 0; i < topics.size(); i++) {
            if (mouseX >= contentX && mouseX <= contentX + 208
                && mouseY >= contentY + i * 11
                && mouseY <= contentY + i * 11 + 10) {
                return i + 1;
            }
        }
        return -1;
    }
}
