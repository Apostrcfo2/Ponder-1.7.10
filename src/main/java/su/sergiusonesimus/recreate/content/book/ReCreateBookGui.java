package su.sergiusonesimus.recreate.content.book;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import su.sergiusonesimus.recreate.ReCreate;

@SideOnly(Side.CLIENT)
public class ReCreateBookGui extends GuiScreen {

    private static final ResourceLocation BOOK_TEXTURE =
        new ResourceLocation(ReCreate.ID, "textures/gui/book.png");

    private static final int BOOK_WIDTH = 272;
    private static final int BOOK_HEIGHT = 180;

    private int bookX;
    private int bookY;
    private int currentPage = 0;
    private List<BookPage> pages = new ArrayList<>();

    public ReCreateBookGui() {
        buildPages();
    }

    private void buildPages() {
        pages.add(new IndexPage());
        pages.add(new BookPage("ReCreate", "recreate.book.cover.desc", null));
        pages.add(new BookPage("recreate.book.kinetic.title", "recreate.book.kinetic.desc", null));
        pages.add(new BookPage("recreate.book.shaft.title", "recreate.book.shaft.desc", "textures/gui/book/shaft.png"));
        pages.add(new BookPage("recreate.book.cogwheel.title", "recreate.book.cogwheel.desc", "textures/gui/book/cogwheel.png"));
        pages.add(new BookPage("recreate.book.creative_motor.title", "recreate.book.creative_motor.desc", "textures/gui/book/creative_motor.png"));
        pages.add(new BookPage("recreate.book.water_wheel.title", "recreate.book.water_wheel.desc", "textures/gui/book/water_wheel.png"));
        pages.add(new BookPage("recreate.book.clutch.title", "recreate.book.clutch.desc", "textures/gui/book/clutch.png"));
        pages.add(new BookPage("recreate.book.gearshift.title", "recreate.book.gearshift.desc", "textures/gui/book/gearshift.png"));
        pages.add(new BookPage("recreate.book.gearbox.title", "recreate.book.gearbox.desc", "textures/gui/book/gearbox.png"));
        pages.add(new BookPage("recreate.book.mechanical_bearing.title", "recreate.book.mechanical_bearing.desc", "textures/gui/book/mechanical_bearing.png"));
        pages.add(new BookPage("recreate.book.windmill_bearing.title", "recreate.book.windmill_bearing.desc", "textures/gui/book/windmill_bearing.png"));
        pages.add(new BookPage("recreate.book.sail.title", "recreate.book.sail.desc", "textures/gui/book/sail.png"));
        pages.add(new BookPage("recreate.book.mechanical_piston.title", "recreate.book.mechanical_piston.desc", "textures/gui/book/mechanical_piston.png"));
    }

    @Override
    public void initGui() {
        bookX = (width - BOOK_WIDTH) / 2;
        bookY = (height - BOOK_HEIGHT) / 2;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        Minecraft.getMinecraft().getTextureManager().bindTexture(BOOK_TEXTURE);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        drawTexturedModalRect(bookX, bookY, 0, 0, BOOK_WIDTH, BOOK_HEIGHT);

        BookPage page = pages.get(currentPage);
        page.draw(this, fontRendererObj, bookX, bookY);

        String pageNum = (currentPage + 1) + "/" + pages.size();
        fontRendererObj.drawString(pageNum, bookX + BOOK_WIDTH / 2 - fontRendererObj.getStringWidth(pageNum) / 2,
            bookY + BOOK_HEIGHT - 14, 0x555555);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (currentPage == 0) {
            IndexPage index = (IndexPage) pages.get(0);
            int clicked = index.getClickedTopic(mouseX, mouseY, bookX, bookY);
            if (clicked != -1) {
                currentPage = clicked;
                return;
            }
        }
        if (mouseX > bookX + BOOK_WIDTH / 2 && mouseX < bookX + BOOK_WIDTH
            && mouseY > bookY + BOOK_HEIGHT - 20 && mouseY < bookY + BOOK_HEIGHT) {
            if (currentPage < pages.size() - 1) currentPage++;
        }
        if (mouseX > bookX && mouseX < bookX + BOOK_WIDTH / 2
            && mouseY > bookY + BOOK_HEIGHT - 20 && mouseY < bookY + BOOK_HEIGHT) {
            if (currentPage > 0) currentPage--;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public static void open() {
        Minecraft.getMinecraft().displayGuiScreen(new ReCreateBookGui());
    }
}
