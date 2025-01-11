package co.crystaldev.client.gui.screens;

import co.crystaldev.client.Client;
import co.crystaldev.client.Resources;
import co.crystaldev.client.feature.base.Dropdown;
import co.crystaldev.client.font.FontRenderer;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.buttons.*;
import co.crystaldev.client.gui.buttons.Label;
import co.crystaldev.client.gui.buttons.settings.DropdownButton;
import co.crystaldev.client.network.Packet;
import co.crystaldev.client.network.socket.client.group.PacketHighlightChunk;
import co.crystaldev.client.network.socket.client.group.PacketRemoveChunkHighlight;
import co.crystaldev.client.util.RenderUtils;
import mapwriter.MapWriterMod;
import mapwriter.MwKeyHandler;
import mapwriter.api.IMwDataProvider;
import mapwriter.api.MwAPI;
import mapwriter.config.Config;
import mapwriter.map.MapRenderer;
import mapwriter.map.MapView;
import mapwriter.map.mapmode.FullscreenMapMode;
import mapwriter.map.mapmode.MapMode;
import mapwriter.tasks.MergeTask;
import mapwriter.tasks.RebuildRegionsTask;
import mapwriter.tasks.Task;
import mapwriter.util.Utils;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;

public class ScreenMapWriter extends Screen {
  private static final double PAN_FACTOR = 0.3D;

  private static Color currentColor = Color.RED;

  private String colorMode = "highlight";

  public String getColorMode() {
    return this.colorMode;
  }

  private static String chunkLabel = "";

  private final MapWriterMod mapWriterMod;

  private final FullscreenMapMode mapMode;

  private final MapView mapView;

  private final MapRenderer map;

  private SimpleColorPicker colorPicker;

  private TextInputField field;

  private int mouseLeftHeld = 0;

  private int mouseLeftDragStartX = 0;

  private int mouseLeftDragStartY = 0;

  private double viewXStart;

  private double viewZStart;

  private int mouseBlockX = 0;

  private int mouseBlockY = 0;

  private int mouseBlockZ = 0;

  private int lastdwheel = 0;

  public ScreenMapWriter(MapWriterMod mapWriterMod) {
    this.mapWriterMod = mapWriterMod;
    this.mapMode = new FullscreenMapMode();
    this.mapView = new MapView(this.mapWriterMod, true);
    this.map = new MapRenderer(this.mapWriterMod, (MapMode)this.mapMode, this.mapView);
    this.mapView.setDimension(this.mapWriterMod.miniMap.view.getDimension());
    this.mapView.setViewCentreScaled(this.mapWriterMod.playerX, this.mapWriterMod.playerZ, this.mapWriterMod.playerDimension);
    this.mapView.setZoomLevel(Config.fullScreenZoomLevel);
  }

  public void init() {
    this.pane = new Pane(10, 10, 121, 180);
    int x = this.pane.x + 4;
    int y = this.pane.y + 58;
    int w = this.pane.width - 8;
    int h = 18;
    addButton((Button)new MenuButton(0, x, y, w, h, "Settings") {

        });
    y += h + 4;
    addButton((Button)new DropdownButton<String>(-1, x, y, w, h, new Dropdown(
            MwAPI.getRegisteredProviders().stream().map(IMwDataProvider::getName).toArray(String[]::new), (Object[])new String[0], true)) {

        });
    y += h + 4;
    addButton((Button)new Label(x + w / 2, y + Fonts.NUNITO_SEMI_BOLD_16.getStringHeight() / 2, "Chunk Highlighting", 16777215, Fonts.NUNITO_SEMI_BOLD_16));
    y += Fonts.NUNITO_SEMI_BOLD_16.getStringHeight() + 4;
    addButton((Button)(this.colorPicker = new SimpleColorPicker(x + 1, y + 1, w - 2, 10, currentColor)));
    y += 16;
    addButton((Button)new MenuButton(2, x, y, w, h, "Clear Chunks") {

        });
    y += h + 4;
    addButton((Button)new DropdownButton<String>(-1, x, y, w, h, new Dropdown((Object[])new String[] { "Highlight", "Text" }, (Object[])new String[] { "Highlight" })) {

        });
    y += h + 4;
    addButton((Button)(this.field = new TextInputField(-1, x, y, w, h, "Chunk Label") {

        }));
    y += h + 4;
    this.pane.height = y - this.pane.y;
  }

  public void draw(int mouseX, int mouseY, float partialTicks) {
    if (this.mouseLeftHeld > 2) {
      double xOffset = (this.mouseLeftDragStartX - mouseX) * this.mapView.getWidth() / this.mapMode.w;
      double yOffset = (this.mouseLeftDragStartY - mouseY) * this.mapView.getHeight() / this.mapMode.h;
      this.mapView.setViewCentre(this.viewXStart + xOffset, this.viewZStart + yOffset);
    }
    if (this.mouseLeftHeld > 0)
      this.mouseLeftHeld++;
    this.map.draw();
    Point p = this.mapMode.screenXYtoBlockXZ(this.mapView, mouseX, mouseY);
    this.mouseBlockX = p.x;
    this.mouseBlockZ = p.y;
    this.mouseBlockY = getHeightAtBlockPos(this.mouseBlockX, this.mouseBlockZ);
    this.mapMode.setCoordinates(this.mouseBlockX, this.mouseBlockY, this.mouseBlockZ);
    renderPane();
    renderCursorInfo(this.mouseBlockX, this.mouseBlockY, this.mouseBlockZ);
    int dwheel = Mouse.getDWheel();
    if (dwheel != 0) {
      for (Button button : this.buttons) {
        if (button.onScroll(null, mouseX, mouseY, dwheel))
          return;
      }
      this.lastdwheel = dwheel;
    }
  }

  private void renderCursorInfo(int blockX, int blockY, int blockZ) {
    int chunkX = blockX >> 4, chunkZ = blockZ >> 4;
    FontRenderer fr = Fonts.NUNITO_SEMI_BOLD_18;
    String title = "Cursor Information";
    String blockCoords = String.format("Block: X: %d, Y: %d, Z: %d", new Object[] { Integer.valueOf(blockX), Integer.valueOf(blockY), Integer.valueOf(blockZ) });
    String chunkCoords = String.format("Chunk: X: %d, Z: %d", new Object[] { Integer.valueOf(chunkX), Integer.valueOf(chunkZ) });
    int strHeight = fr.getStringHeight();
    int width = fr.getMaxWidth(new String[] { title, blockCoords, chunkCoords }) + 12;
    int height = fr.getStringHeight() * 3 + 4;
    int x = this.mc.displayWidth / 4 - width / 2;
    int y = this.mc.displayHeight / 2 - 4 - height;
    RenderUtils.drawRoundedRect(x, y, (x + width), (y + height), 20.0D, this.opts.backgroundColor.getRGB());
    RenderUtils.drawRoundedRect(x, y, (x + width), (y + height), 20.0D, this.opts.backgroundColor1.getRGB());
    x = this.mc.displayWidth / 4;
    y += 2 + strHeight / 2;
    fr.drawCenteredString(title, x, y, 16777215);
    fr.drawCenteredString(blockCoords, x, y += strHeight, 13421772);
    fr.drawCenteredString(chunkCoords, x, y + strHeight, 13421772);
  }

  private void renderPane() {
    RenderUtils.drawRoundedRect(this.pane.x, this.pane.y, (this.pane.x + this.pane.width), (this.pane.y + this.pane.height), 20.0D, this.opts.backgroundColor
        .getRGB());
    RenderUtils.drawRoundedRect(this.pane.x, this.pane.y, (this.pane.x + this.pane.width), (this.pane.y + this.pane.height), 20.0D, this.opts.backgroundColor1
        .getRGB());
    RenderUtils.setGlColor(this.opts.secondaryColor);
    int fontSize = Fonts.NUNITO_SEMI_BOLD_24.getStringHeight();
    int fontWidth = Fonts.NUNITO_SEMI_BOLD_24.getStringWidth("CRYSTAL");
    int combinedWidth = fontSize * 2 + fontWidth + 3;
    int x = this.pane.x + this.pane.width / 2 - combinedWidth / 2;
    int y = this.pane.y + 6;
    RenderUtils.drawCustomSizedResource(Resources.LOGO_WHITE, x, y, fontSize * 2, fontSize * 2);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    x += fontSize * 2 + 3 + fontWidth / 2;
    y += fontSize / 2;
    Fonts.NUNITO_SEMI_BOLD_24.drawCenteredString("CRYSTAL", x, y, -1);
    y += fontSize;
    Fonts.NUNITO_SEMI_BOLD_24.drawCenteredString("CLIENT", x, y, -1);
    y += fontSize;
    RenderUtils.setGlColor(this.opts.mainColor);
    RenderUtils.drawLine(1.0F, (this.pane.x + 30), y, (this.pane.x + this.pane.width - 30), y);
    RenderUtils.resetColor();
  }

  public void handleMouseInput() throws IOException {
    if (this.lastdwheel != 0) {
      mouseDWheelScrolled(this.lastdwheel);
      this.lastdwheel = 0;
    }
    super.handleMouseInput();
  }

  public void onButtonInteract(Button button, int mouseX, int mouseY, int mouseButton) {
    System.out.println(button.id);
    if(button.id == 0 && !(this.mc.currentScreen instanceof ScreenClientOptions)) {
      this.mc.displayGuiScreen(new ScreenClientOptions(this));
    } else if (button.id == 2) {
    //TODO implement chunk clearing functionality
//      mapWriterMod.chunkManager.close();
//      mapWriterMod.regionManager.regionFileCache.close();
//      mapWriterMod.regionManager.close();
//      mapWriterMod.close();
    }
  }

  public void keyTyped(char character, int key) {
    super.keyTyped(character, key);
    if (this.mc.currentScreen == null || this.field.isTyping())
      return;
    switch (key) {
      case 199:
        this.mapView.setViewCentreScaled(this.mapWriterMod.playerX, this.mapWriterMod.playerZ, this.mapWriterMod.playerDimension);
        return;
      case 25:
        mergeMapViewToImage();
        exitGui();
        return;
      case 203:
        this.mapView.panView(-0.3D, 0.0D);
        return;
      case 205:
        this.mapView.panView(0.3D, 0.0D);
        return;
      case 200:
        this.mapView.panView(0.0D, -0.3D);
        return;
      case 208:
        this.mapView.panView(0.0D, 0.3D);
        return;
      case 19:
        regenerateView();
        exitGui();
        return;
    }
    if (key == MwKeyHandler.keyZoomIn.getKeyCode()) {
      this.mapView.adjustZoomLevel(-1);
    } else if (key == MwKeyHandler.keyZoomOut.getKeyCode()) {
      this.mapView.adjustZoomLevel(1);
    }
  }

  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    float scaledScreen = getScaledScreen();
    mouseX = (int)(mouseX / scaledScreen);
    mouseY = (int)(mouseY / scaledScreen);
    if (mouseButton == 0) {
      boolean flag = true;
      for (Button button : this.buttons) {
        if (button.isHovered(mouseX, mouseY))
          flag = false;
      }
      if (this.pane.isHovered(mouseX, mouseY, false))
        flag = false;
      if (flag) {
        this.mouseLeftHeld = 1;
        this.mouseLeftDragStartX = mouseX;
        this.mouseLeftDragStartY = mouseY;
      }
    } else if (mouseButton == 2) {
      Point blockPoint = this.mapMode.screenXYtoBlockXZ(this.mapView, mouseX, mouseY);
      MwAPI.getEnabledDataProviders().forEach(provider -> provider.onMiddleClick(this.mapView.getDimension(), blockPoint.x, blockPoint.y, this.mapView));
    }
    this.viewXStart = this.mapView.getX();
    this.viewZStart = this.mapView.getZ();
  }

  public void mouseReleased(int x, int y, int button) {
    super.mouseReleased(x, y, button);
    float scaledScreen = getScaledScreen();
    int mouseXScaled = (int)(x / scaledScreen);
    int mouseYScaled = (int)(y / scaledScreen);
    if (!this.pane.isHovered(x, y, false) && mouseXScaled == this.lastMouseClickPosX && mouseYScaled == this.lastMouseClickPosY && button == this.lastMouseClickButton)
      if (button == 0) {
        PacketHighlightChunk packet = null;
        if (this.colorMode.equalsIgnoreCase("text")) {
          if (chunkLabel != null && !chunkLabel.isEmpty()) {
            packet = new PacketHighlightChunk(this.mouseBlockX >> 4, this.mouseBlockZ >> 4, chunkLabel);
            this.field.setText("");
          }
        } else {
          int color = (new Color(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), 150)).getRGB();
          packet = new PacketHighlightChunk(this.mouseBlockX >> 4, this.mouseBlockZ >> 4, Integer.toString(color));
        }
        if (packet != null)
          Client.sendPacket((Packet)packet);
      } else {
        PacketRemoveChunkHighlight packet = new PacketRemoveChunkHighlight(this.mapMode.getChunkX(), this.mapMode.getChunkZ());
        Client.sendPacket((Packet)packet);
      }
    if (button == 0)
      this.mouseLeftHeld = 0;
    currentColor = this.colorPicker.getCurrentColor();
  }

  public int getHeightAtBlockPos(int bX, int bZ) {
    int bY = 0;
    int worldDimension = this.mapWriterMod.mc.theWorld.provider.getDimensionId();
    if (worldDimension == this.mapView.getDimension() && worldDimension != -1)
      bY = this.mapWriterMod.mc.theWorld.getHeight(new BlockPos(bX, 0, bZ)).getY();
    return bY;
  }

  public void mergeMapViewToImage() {
    this.mapWriterMod.chunkManager.saveChunks();
    this.mapWriterMod.executor.addTask((Task)new MergeTask(this.mapWriterMod.regionManager, (int)this.mapView.getX(), (int)this.mapView.getZ(), (int)this.mapView.getWidth(), (int)this.mapView.getHeight(), this.mapView.getDimension(), this.mapWriterMod.worldDir, this.mapWriterMod.worldDir.getName()));
    Utils.printBoth(I18n.format("mw.gui.mwgui.chatmsg.merge", this.mapWriterMod.worldDir.getAbsolutePath()));
  }

  public void regenerateView() {
    Utils.printBoth(I18n.format("mw.gui.mwgui.chatmsg.regenmap", (int) this.mapView.getWidth(),
            (int) this.mapView.getHeight(),
            (int) this.mapView.getMinX(),
            (int) this.mapView.getMinZ()));
    this.mapWriterMod.executor.addTask((Task)new RebuildRegionsTask(this.mapWriterMod, (int)this.mapView.getMinX(), (int)this.mapView.getMinZ(), (int)this.mapView.getWidth(), (int)this.mapView.getHeight(), this.mapView.getDimension()));
  }

  public void mouseDWheelScrolled(int direction) {
    int zF = (direction > 0) ? -1 : 1;
    this.mapView.zoomToPoint(this.mapView.getZoomLevel() + zF, this.mouseBlockX, this.mouseBlockZ);
    Config.fullScreenZoomLevel = this.mapView.getZoomLevel();
  }

  public void exitGui() {
    this.mc.displayGuiScreen(null);
  }

  public void onGuiClosed() {
    Keyboard.enableRepeatEvents(false);
    this.mapWriterMod.miniMap.view.setDimension(this.mapView.getDimension());
    Keyboard.enableRepeatEvents(false);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\ScreenMapWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */