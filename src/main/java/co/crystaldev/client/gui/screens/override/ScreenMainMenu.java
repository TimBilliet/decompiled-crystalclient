package co.crystaldev.client.gui.screens.override;

import co.crystaldev.client.Resources;
import co.crystaldev.client.account.AltManager;
import co.crystaldev.client.font.FontRenderer;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.GuiOptions;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.ScreenPanorama;
import co.crystaldev.client.gui.buttons.AccountButton;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.gui.buttons.ResourceButton;
import co.crystaldev.client.gui.ease.Animation;
import co.crystaldev.client.gui.ease.Easing;
import co.crystaldev.client.gui.screens.screen_overlay.OverlayOfflineAccount;
import co.crystaldev.client.util.Reflector;
import co.crystaldev.client.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.GuiModList;
import net.minecraftforge.fml.common.Loader;

import java.awt.*;

public class ScreenMainMenu extends ScreenPanorama implements GuiYesNoCallback {
    private static final FontRenderer fontRenderer = Fonts.NUNITO_REGULAR_20;

    private static final int textColor = (new Color(255, 255, 255, 180)).getRGB();

    private static boolean firstTime = true;

    private Animation primaryAnimation;

    private Animation secondaryAnimation;

    private GuiOptions.Theme theme;

    private int topButtonY;

    private static final int lMods = Loader.instance().getModList().size();

    private static final int aMods = Loader.instance().getActiveModList().size();

    private static final String CC_VERSION = String.format("%s v%s", "Timmeke_ Crystal Client", "1.1.1");

    private static final String MC_COPYRIGHT = "Copyright Mojang Studios. Do not distribute!";

    private static final String FORGE_MODS = String.format("%d mod%s loaded, %d mod%s active", lMods, (lMods == 1) ? "" : "s", aMods, (aMods == 1) ? "" : "s");

    private static final long buttonOutlineColor = 0xff1b72f4;

    public ScreenMainMenu() {
        super(Resources.PANORAMA_IMAGES, 256);
    }

    public void initGui() {
        init();
    }

    public void init() {
        super.init();
        this.theme = GuiOptions.Theme.DEFAULT;
        int w = 200;
        int w1 = w / 2 - 3;
        int h = 18;
        int totalHeight = (h + 6) * 4 - 6;
        int x = (int) (this.width / getScaledScreen() / 2.0F - (w / 2));
        int x1 = x + w1 + 6;
        int y = this.topButtonY = (int) (this.height / getScaledScreen() / 2.0F - (totalHeight / 2)) + 10 + 30;
        addButton(new MenuButton(0, x, y, w, h, I18n.format("menu.singleplayer").toUpperCase(), buttonOutlineColor) {

        });
        y += h + 6;
        addButton(new MenuButton(1, x, y, w, h, I18n.format("menu.multiplayer").toUpperCase(), buttonOutlineColor) {

        });
        y += h + 6;
        addButton(new MenuButton(4, x, y, w, h, I18n.format("fml.menu.mods").toUpperCase(), buttonOutlineColor) {

        });
        y += h + 6;
        addButton(new MenuButton(2, x, y, w1, h,
                I18n.format("menu.options").toUpperCase().replaceAll("\\.", ""), buttonOutlineColor) {

        });
        addButton(new MenuButton(3, x1, y, w1, h, I18n.format("menu.quit").toUpperCase(), buttonOutlineColor) {

        });
        x = 5;
        if (Reflector.isReplaymodLoaded()) {
            addButton(new ResourceButton(20, x, 5, h, h, Resources.REPLAY_MOD), b -> b.setOnClick(Reflector::openReplayGui));
            x += h + 2;
        }
        addButton(new AccountButton(21, x, 5, this));
    }

    public void onButtonInteract(Button button, int mouseX, int mouseY, int mouseButton) {
        switch (button.id) {
            case 0:
                checkSession(new GuiSelectWorld(this));
                break;
            case 1:
                checkSession(new GuiMultiplayer(this));
                break;
            case 2:
                this.mc.displayGuiScreen(new net.minecraft.client.gui.GuiOptions(this, this.mc.gameSettings));
                break;
            case 3:
                this.mc.shutdown();
                break;
            case 4:
                this.mc.displayGuiScreen(new GuiModList(this));
                break;
        }
    }

    public void onGuiClosed() {
        this.constructed = false;
        super.onGuiClosed();
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        super.draw(mouseX, mouseY, partialTicks);
        float scale = getScaledScreen();
        int width = (int) (this.width / scale);
        int height = (int) (this.height / scale);
        fontRenderer.drawString(CC_VERSION, 5, height - 5 - fontRenderer.getStringHeight(CC_VERSION), textColor);
        fontRenderer.drawString(FORGE_MODS, 5, height - 5 - fontRenderer.getStringHeight(CC_VERSION) - 5 - fontRenderer.getStringHeight(FORGE_MODS), textColor);
        fontRenderer.drawString(MC_COPYRIGHT, width - fontRenderer.getStringWidth(MC_COPYRIGHT) - 5, height - 5 - fontRenderer.getStringHeight(MC_COPYRIGHT), textColor);
        FontRenderer fr = Fonts.NUNITO_SEMI_BOLD_24;
        int size = 90;
        int y = this.topButtonY - 10 - fr.getStringHeight("CRYSTAL CLIENT");
        fr.drawString("CRYSTAL CLIENT", width / 2 - fr.getStringWidth("CRYSTAL CLIENT") / 2, y, -1);
        y -= size;
        RenderUtils.resetColor();
        RenderUtils.drawCustomSizedResource(Resources.LOGO, width / 2.0D - size / 2.0D, y, size, size);
    }

    public void onResize(Minecraft mc, int width, int height) {
        super.onResize(mc, width, height);
        this.primaryAnimation = this.secondaryAnimation = null;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (firstTime) {
            firstTime = false;
            this.primaryAnimation = new Animation(2.0F, 0.0F, (this.height * 2 + 4), Easing.OUT_CUBIC);
            this.secondaryAnimation = new Animation(2.0F, 0.0F, (this.height + 4), Easing.OUT_CUBIC);
        }
        if (this.primaryAnimation != null && this.secondaryAnimation != null) {
            int primOffset = (int) this.primaryAnimation.getValue();
            int secOffset = (int) this.secondaryAnimation.getValue();
            RenderUtils.resetColor();
            RenderUtils.drawFastRect(0.0F, -secOffset, this.width, (this.height - secOffset), Color.BLACK.getRGB());
            RenderUtils.drawGradientRect(0, this.height - primOffset, this.width, this.height * 2 - primOffset, this.theme.secondaryColor.getRGB(), this.theme.mainColor.getRGB());
            RenderUtils.resetColor();
            if (this.primaryAnimation.isComplete() && this.secondaryAnimation.isComplete())
                this.primaryAnimation = this.secondaryAnimation = null;
        }
    }

    private void checkSession(GuiScreen screenIn) {
//        if (AltManager.isLoggedIn()) {
            this.mc.displayGuiScreen(screenIn);
//        }
    }

    public static boolean isAnimationComplete() {
        GuiScreen screen = (Minecraft.getMinecraft()).currentScreen;
        if (!(screen instanceof ScreenMainMenu))
            return true;
        ScreenMainMenu menu = (ScreenMainMenu) screen;
        return (menu.primaryAnimation == null || menu.primaryAnimation.isComplete() || menu.secondaryAnimation == null || menu.secondaryAnimation.isComplete());
    }
}
