package co.crystaldev.client.gui.screens.schematica;

import co.crystaldev.client.Resources;
import co.crystaldev.client.feature.base.Dropdown;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.ScrollPane;
import co.crystaldev.client.gui.buttons.NavigationButton;
import co.crystaldev.client.gui.screens.screen_overlay.ScreenOverlay;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.SchematicaGuiType;
import co.crystaldev.client.util.objects.FadingColor;
import com.github.lunatrius.schematica.Schematica;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.github.lunatrius.schematica.util.LoadedSchematic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ScreenSchematicaBase extends Screen {
  protected static final int PANE_WIDTH = 550;

  protected static final int PANE_HEIGHT = 308;

  protected static final int CONTENT_MARGIN = 14;

  protected static final int HEADER_HEIGHT = 35;

  private static NavigationButton<SchematicaGuiType> navbar;

  private final SchematicaGuiType type;

  private boolean toNewGui = false;

  protected Dropdown<LoadedSchematic> loadedSchematic;

  public ScrollPane content;

  public Pane header;

  private final FadingColor backgroundColor;

  public ScreenSchematicaBase(SchematicaGuiType type) {
    this.type = type;
    this.backgroundColor = new FadingColor(new Color(29, 29, 29, 5), new Color(35, 35, 45, 180), 140L);
  }

  public void init() {
    super.init();
    float scale = getScaledScreen();
    double dw = (this.width / scale);
    double dh = (this.height / scale);
    this.pane = new Pane(dw / 2.0D - 275.0D, dh / 2.0D - 154.0D, 550.0D, 308.0D);
    this.header = new Pane(this.pane.x, this.pane.y, this.pane.width, 35);
    this.content = new ScrollPane(this.pane.x, this.pane.y + 35, this.pane.width, this.pane.height - 35);
    this.loadedSchematic = new Dropdown(ClientProxy.loadedSchematics.toArray((Object[])new LoadedSchematic[0]), new LoadedSchematic[] { ClientProxy.currentSchematic });
    this.loadedSchematic.setOnSelect(this::onSelectSchematic);
    this.loadedSchematic.setDefault();
    if (navbar == null) {
      addButton((navbar = new NavigationButton(this.type, this.header.x + this.header.width / 2, this.header.y + this.header.height / 2, false)));
    } else {
      addButton((navbar = navbar.copy(this.type, this.header.x + this.header.width / 2, this.header.y + this.header.height / 2)));
    }
  }

  public void draw(int mouseX, int mouseY, float partialTicks) {
    RenderUtils.drawRoundedRect(this.pane.x, this.pane.y, (this.pane.x + this.pane.width), (this.pane.y + this.pane.height), 30.0D, this.opts.backgroundColor
        .getRGB());
    RenderUtils.drawRoundedRect(this.pane.x, this.pane.y, (this.pane.x + this.pane.width), (this.pane.y + this.pane.height), 30.0D, this.opts.backgroundColor1
        .getRGB());
    int size = (int)(this.header.height * 0.75F);
    int margin = (this.header.height - size) / 2;
    int lx = this.header.x + margin;
    int ly = this.header.y + margin;
    RenderUtils.setGlColor(this.opts.secondaryColor);
    RenderUtils.drawCustomSizedResource(Resources.LOGO_WHITE, lx, ly, size, size);
    RenderUtils.resetColor();
    Fonts.NUNITO_SEMI_BOLD_24.drawString("Schematica", lx + size + 5, ly + size / 2 - Fonts.NUNITO_SEMI_BOLD_24.getStringHeight() / 2, this.opts.hoveredTextColor.getRGB());
    if (navbar.wasUpdated() && navbar.getCurrent() != this.type)
      openGui(navbar.getCurrent());
  }

  public void drawOverlay(int mouseX, int mouseY, int scaledX, int scaledY, float scale, float partialTicks) {
    Screen overlay = getCurrentOverlay();
    boolean flag = (overlay instanceof ScreenOverlay && ((ScreenOverlay)overlay).isDimBackground());
    this.backgroundColor.fade(flag);
    GL11.glPushMatrix();
    GL11.glScalef(scale, scale, scale);
    RenderUtils.drawRoundedRect(this.pane.x, this.pane.y, (this.pane.x + this.pane.width), (this.pane.y + this.pane.height), 30.0D, this.backgroundColor
        .getCurrentColor().getRGB());
    RenderUtils.resetColor();
    GL11.glPopMatrix();
    super.drawOverlay(mouseX, mouseY, scaledX, scaledY, scale, partialTicks);
  }

  public void onGuiClosed() {
    if (!this.toNewGui)
      navbar = null;
  }

  protected void onSelectSchematic(Dropdown<LoadedSchematic> dropdown, LoadedSchematic schematic) {
    ClientProxy.currentSchematic = schematic;
    if (ClientProxy.currentSchematic.schematic != null) {
      ClientProxy.currentSchematic.schematic.isRendering = true;
      ClientProxy.moveToPlayer = false;
      Schematica.proxy.awaitingChange = true;
    } else {
      init();
    }
  }

  public static void openGui(SchematicaGuiType type) {
    if ((Minecraft.getMinecraft()).currentScreen instanceof ScreenSchematicaBase)
      ((ScreenSchematicaBase)(Minecraft.getMinecraft()).currentScreen).toNewGui = true;
    final boolean flag = ((Minecraft.getMinecraft()).currentScreen != null);
    switch (type) {
      case LOAD_SCHEMATIC:
        Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new ScreenLoadSchematic() {

            });
        return;
      case CONTROL_SCHEMATIC:
        Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new ScreenSchematicControl() {

            });
        return;
      case SAVE_SCHEMATIC:
        Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new ScreenSaveSchematic() {

            });
        return;
    }
    Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new ScreenSchematicaBase(type) {

        });
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\schematica\ScreenSchematicaBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */