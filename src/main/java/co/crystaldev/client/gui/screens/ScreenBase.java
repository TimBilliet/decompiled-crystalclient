package co.crystaldev.client.gui.screens;

import co.crystaldev.client.Reference;
import co.crystaldev.client.Resources;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.ScrollPane;
import co.crystaldev.client.gui.buttons.Label;
import co.crystaldev.client.gui.buttons.MenuResourceButton;
import co.crystaldev.client.gui.buttons.SidebarButton;
import co.crystaldev.client.gui.screens.groups.ScreenGroups;
import co.crystaldev.client.gui.screens.screen_overlay.ScreenOverlay;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.GuiType;
import co.crystaldev.client.util.objects.FadingColor;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Map;

public class ScreenBase extends Screen {
    protected static final int PANE_WIDTH = 550;

    protected static final int PANE_HEIGHT = 308;

    protected static final int CONTENT_MARGIN = 14;

    protected static final int HEADER_HEIGHT = 35;

    private static final Map<GuiType, Class<? extends Screen>> SCREENS = (Map<GuiType, Class<? extends Screen>>) (new ImmutableMap.Builder())
            .put(GuiType.MODULES, ScreenModules.class)
            .put(GuiType.COSMETICS, ScreenCosmetics.class)
            .put(GuiType.GROUPS, ScreenGroups.class)
            .put(GuiType.PROFILES, ScreenProfiles.class)
            .put(GuiType.AUTO_TEXT, ScreenMacros.class)
            .put(GuiType.WAYPOINTS, ScreenWaypoints.class)
            .build();

    private static GuiType type = GuiType.MODULES;

    public ScrollPane content;

    public Pane sidebar;

    public Pane header;

    public static GuiType getType() {
        return type;
    }

    public static void setType(GuiType type) {
        ScreenBase.type = type;
    }

    private final FadingColor backgroundColor = new FadingColor(new Color(29, 29, 29, 5), new Color(35, 35, 45, 180), 140L);

    public void init() {
        super.init();
        float scale = getScaledScreen();
        double dw = (this.width / scale);
        double dh = (this.height / scale);
        int sidebarWidth = 121;
        this.pane = new Pane(dw / 2.0D - 275.0D, dh / 2.0D - 154.0D, 550.0D, 308.0D);
        this.header = new Pane(this.pane.x + sidebarWidth, this.pane.y, this.pane.width - sidebarWidth, 35);
        this.sidebar = new Pane(this.pane.x, this.pane.y, sidebarWidth, this.pane.height);
        this.content = new ScrollPane(this.pane.x + sidebarWidth, this.sidebar.y + 35, this.pane.width - sidebarWidth, this.pane.height - 35);
        initSidebarButtons();
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        RenderUtils.drawRoundedRect(this.pane.x, this.pane.y, (this.pane.x + this.pane.width), (this.pane.y + this.pane.height), 30.0D, this.opts.backgroundColor
                .getRGB());
        RenderUtils.drawRoundedRect(this.pane.x, this.pane.y, (this.pane.x + this.pane.width), (this.pane.y + this.pane.height), 30.0D, this.opts.backgroundColor1
                .getRGB());
        RenderUtils.drawRoundedRect(this.sidebar.x, this.sidebar.y, (this.sidebar.x + this.sidebar.width), (this.sidebar.y + this.sidebar.height), 30.0D, this.opts.sidebarBackground
                .getRGB(), true, true, false, false);
        RenderUtils.setGlColor(this.opts.secondaryColor);
        int logoSize = (int) (this.sidebar.width * 0.33F);
        RenderUtils.drawCustomSizedResource(Resources.LOGO_WHITE, this.sidebar.x + 6, this.sidebar.y + 6, logoSize, logoSize);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int fontSize = Fonts.NUNITO_SEMI_BOLD_28.getStringHeight("CRYSTAL");
        int x = this.sidebar.x + logoSize + 3 + (this.sidebar.width - logoSize - 6) / 2;
        int y = this.sidebar.y + 6 + logoSize / 2 - fontSize / 2;
        Fonts.NUNITO_SEMI_BOLD_28.drawCenteredString("CRYSTAL", x, y, Color.WHITE.getRGB());
        y += Fonts.NUNITO_SEMI_BOLD_28.getStringHeight();
        Fonts.NUNITO_SEMI_BOLD_28.drawCenteredString("CLIENT", x, y, Color.WHITE.getRGB());
        int half = this.header.height / 2 - Fonts.NUNITO_SEMI_BOLD_24.getStringHeight() / 2;
        Fonts.NUNITO_SEMI_BOLD_24.drawString(type.getDisplayText(), this.header.x + half, this.header.y + half, Color.WHITE.getRGB());
    }

    public void drawOverlay(int mouseX, int mouseY, int scaledX, int scaledY, float scale, float partialTicks) {
        Screen overlay = getCurrentOverlay();
        boolean flag = (overlay instanceof ScreenOverlay && ((ScreenOverlay) overlay).isDimBackground());
        this.backgroundColor.fade(flag);
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, scale);
        RenderUtils.drawRoundedRect(this.pane.x, this.pane.y, (this.pane.x + this.pane.width), (this.pane.y + this.pane.height), 30.0D, this.backgroundColor
                .getCurrentColor().getRGB());
        RenderUtils.resetColor();
        GL11.glPopMatrix();
        super.drawOverlay(mouseX, mouseY, scaledX, scaledY, scale, partialTicks);
    }

    public void onButtonInteract(Button button, int mouseX, int mouseY, int mouseButton) {
        if (button instanceof SidebarButton) {
            type = ((SidebarButton) button).getType();
            ((SidebarButton) button).saveState();
            for (Button b : this.buttons) {
                if (b instanceof SidebarButton && b != button) {
                    ((SidebarButton) b).animate(false, false);
                    ((SidebarButton) b).saveState();
                }
            }
            openGui();
        } else if (button.id == 0 && !(this.mc.currentScreen instanceof ScreenClientOptions)) {
            this.mc.displayGuiScreen(new ScreenClientOptions(this));
        } else if (button.id == 1) {
            this.mc.displayGuiScreen(new ScreenEditLocations(this));
        }
    }

    public void addScreenMessage(String message) {
        Validate.notNull(this.content);
        String[] messages = WordUtils.wrap(message, 65).split("[\n]+");
        int x = this.content.x + this.content.width / 2;
        int y = this.content.y + 65;
        for (String str : messages) {
            addButton(new Label(x, y, str, 16777215, Fonts.NUNITO_REGULAR_20));
            y += Fonts.NUNITO_REGULAR_20.getStringHeight();
        }
    }

    private void initSidebarButtons() {

        int w = (int) (this.sidebar.width * 0.82F);
        int h = 18;
        int x = this.sidebar.x + this.sidebar.width / 2 - w / 2;
        int y = this.sidebar.y + this.sidebar.height / 2 - (h + 6) * (GuiType.values()).length / 2;
        int w1 = w + x - this.sidebar.x + 5;
        for (GuiType type : GuiType.values()) {
            addButton(new SidebarButton(x, y, w, w1, h, type) {

            });
            y += h + 6;
        }
//    y = this.sidebar.y + this.sidebar.height - x - this.sidebar.x - (h + 6) * 2;
        y = this.sidebar.height + h / 2 + (h + 6) * 2;
        addButton(new MenuResourceButton(0, x, y, w, h, "Client Options", Resources.COG, 10) {

        });
        y += h + 6;
        addButton(new MenuResourceButton(1, x, y, w, h, "Edit HUD", Resources.EDIT, 11) {

        });
    }

    public static void openGui() {
        if (SCREENS.containsKey(type)) {
            try {
                Minecraft.getMinecraft().displayGuiScreen((SCREENS.get(type)).newInstance());
            } catch (InstantiationException | IllegalAccessException ex) {
                Reference.LOGGER.error("Unable to construct GUI " + type, ex);
            }
        } else {
            Minecraft.getMinecraft().displayGuiScreen(new ScreenBase());
        }
    }
}
