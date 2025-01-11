package co.crystaldev.client.gui;

import co.crystaldev.client.Reference;
//import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class Screen extends GuiScreen {
  public final Queue<Button> buttons = new ConcurrentLinkedQueue<>();
  
  public final List<Button> buttonsToRemove = new ArrayList<>();
  
  private final List<Screen> screenOverlays = new LinkedList<>();
  
  private final List<Screen> overlaysToRemove = new ArrayList<>();
  
  public GuiScreen parent = null;
  
  public boolean constructed = false;
  
  public boolean overlay = false;
  
  public boolean exited = false;
  
  public boolean ignoreFirstKeyPress = true;
  
  public int lastMouseClickPosX = -1;
  
  public int lastMouseClickPosY = -1;
  
  public int lastMouseClickButton = -1;
  
  public boolean _keyPressed = false;
  
  public Pane pane;
  
  public ScaledResolution scaledResolution;
  
  protected GuiOptions opts = GuiOptions.getInstance();
  
  public Screen() {
    this.mc = Minecraft.getMinecraft();
    ScaledResolution scaledResolution = new ScaledResolution(this.mc);
    //TODO fix guis changing size when changing scale in videosettings
//    System.out.println(scaledResolution.getScaledHeight());
//    System.out.println(scaledResolution.getScaledWidth());
//    System.out.println(scaledResolution.getScaleFactor());
    setWorldAndResolution(this.mc, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
//    setWorldAndResolution(this.mc, 1920, 1080);
    this.scaledResolution = scaledResolution;
//    this.scaledResolution = new ScaledResolution()

  }
  
  public Screen(GuiScreen parent) {
    this();
    this.parent = parent;
  }
  
  public void onButtonInteract(Button button, int mouseX, int mouseY, int mouseButton) {}
  
  public void addButton(Button button) {
    this.buttons.add(button);
  }
  
  public <T extends Button> void addButton(T button, Consumer<T> consumer) {
    this.buttons.add(button);
    consumer.accept(button);
  }
  
  public void removeButton(Button button) {
    this.buttonsToRemove.add(button);
    button.removed = true;
  }
  
  public void removeButton(Predicate<Button> predicate) {
    for (Button button : this.buttons) {
      if (predicate.test(button) && !button.removed) {
        this.buttonsToRemove.add(button);
        button.removed = true;
      } 
    } 
  }
  
  public void init() {
    this.buttons.clear();
    this.buttonsToRemove.clear();
  }
  
  public abstract void draw(int paramInt1, int paramInt2, float paramFloat);
  
  public void drawOverlay(int mouseX, int mouseY, int scaledMouseX, int scaledMouseY, float scale, float partialTicks) {
    if (hasOverlay()) {
      Screen cur = getCurrentOverlay();
      cur.drawScreen(mouseX, mouseY, partialTicks);
      if (cur.exited)
        removeOverlay(cur); 
    } 
  }
  
  public float getScaledScreen() {
    int s = ((this.scaledResolution == null) ? (this.scaledResolution = new ScaledResolution(this.mc)) : this.scaledResolution).getScaleFactor();
//    s = 0.5;
    return 1.0F / 0.5F * s;
//    return 1;
  }
  
  public boolean hasOverlay() {
    return (this.screenOverlays.size() > 0);
  }
  
  public Screen getCurrentOverlay() {
    if (hasOverlay())
      return this.screenOverlays.get(0); 
    return null;
  }
  
  public void addOverlay(Screen overlay) {
    overlay.overlay = true;
    this.screenOverlays.add(overlay);
    overlay.init();
  }
  
  public void removeOverlay(Screen overlay) {
    System.out.println("removeoverlay in screen");
    overlay.onGuiClosed();
    overlay.exited = true;
    this.overlaysToRemove.add(overlay);
  }
  
  public void removeOverlay(int index) {
    synchronized (this.screenOverlays) {
      if (this.screenOverlays.size() - 1 >= index) {
        Screen overlay = this.screenOverlays.get(index);
        overlay.onGuiClosed();
        overlay.exited = true;
        this.overlaysToRemove.add(overlay);
      } 
    } 
  }
  
  public void removeOverlays() {
//    System.out.println("removeoverlays");
    if (!this.overlaysToRemove.isEmpty()) {
      this.screenOverlays.removeAll(this.overlaysToRemove);
      this.overlaysToRemove.clear();
    } 
  }
  
  public void removeButtons() {
//    System.out.println("removebuttons");
    if (!this.buttonsToRemove.isEmpty()) {
      this.buttons.removeAll(this.buttonsToRemove);
      this.buttonsToRemove.clear();
    } 
    for (Screen overlay : this.screenOverlays)
      overlay.removeButtons(); 
  }

  public static void scissorStart(Pane pane) {
    if (pane != null) {
      int scale = (new ScaledResolution(Minecraft.getMinecraft())).getScaleFactor();
      int x = pane.x;
      int y = pane.y;
      int x1 = pane.x + pane.width;
      int y1 = pane.y + pane.height;

      GL11.glScissor(x * scale, (Minecraft.getMinecraft()).displayHeight - y1 * scale, (x1 - x) * scale, (y1 - y) * scale);

      GL11.glEnable(3089);
    }
  }
  
  public static void scissorEnd(Pane pane) {
    if (pane != null) {
      GL11.glDisable(3089);
    }
  }
  
  public void setWorldAndResolution(Minecraft mc, int width, int height) {
    this.mc = mc;
    this.scaledResolution = new ScaledResolution(mc);
    this.itemRender = mc.getRenderItem();
    this.fontRendererObj = mc.fontRendererObj;
    this.width = width;
    this.height = height;
    this.buttonList.clear();
    initGui();
  }
  
  public void drawDefaultBackground() {
    if (ClientOptions.getInstance().canBlur())
      ClientOptions.getInstance().blurScreen();
    float scale = getScaledScreen();
    int color = ClientOptions.getInstance().canBlur() ? ClientOptions.getInstance().getBackgroundColor() : 1712328720;
    if (this.mc.theWorld != null) {
      RenderUtils.drawGradientRect(0, 0, (int)(this.width / scale), (int)(this.height / scale), color, color);
    } else {
      drawBackground(0);
    } 
  }
  
  public void onResize(Minecraft mc, int w, int h) {
    super.onResize(mc, w, h);
    init();
    if (hasOverlay())
      getCurrentOverlay().onResize(mc, w, h); 
  }
  
  public void keyTyped(char keyTyped, int keyCode) {
    if (hasOverlay()) {
      Screen overlay = getCurrentOverlay();
      boolean bool = true;
      for (Button button : overlay.buttons) {
        if (!button.onKeyTyped(keyTyped, keyCode))
          bool = false; 
      } 
      if (bool && keyCode == 1) {
        overlay.onGuiClosed();
        removeOverlay(overlay);
        return;
      } 
      return;
    } 
    boolean flag = true;
    for (Button button : this.buttons) {
      if (!button.onKeyTyped(keyTyped, keyCode))
        flag = false; 
    } 
    if (flag && keyCode == 1 && 
      this.parent != null) {
      this.mc.displayGuiScreen(this.parent);
      return;
    } 
    if (flag)
      try {
        super.keyTyped(keyTyped, keyCode);
      } catch (IOException ex) {
        Reference.LOGGER.error("Exception raised while calling keyTyped", ex);
      }  
  }
  
  public boolean doesGuiPauseGame() {
    return false;
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    float scaledScreen = getScaledScreen();
    GL11.glPushMatrix();
    GL11.glScalef(scaledScreen, scaledScreen, scaledScreen);
    if (!this.overlay)
      drawDefaultBackground(); 
    boolean hasOverlay = hasOverlay();
    int mouseXScaled = (int)(mouseX / scaledScreen), buttonX = hasOverlay ? -200 : mouseXScaled;
    int mouseYScaled = (int)(mouseY / scaledScreen), buttonY = hasOverlay ? -200 : mouseYScaled;
    draw(hasOverlay ? -200 : mouseXScaled, hasOverlay ? -200 : mouseYScaled, partialTicks);
    Button hoveredButton = null;
    boolean wasHovered = false;
    for (Button button : this.buttons) {
      if (!button.visible || button.removed || button.renderLast || 
        button.shouldBeCulled())
        continue; 
      boolean hovered = button.isHovered(buttonX, buttonY);
      button.drawButton(buttonX, buttonY, (!wasHovered && hovered));
      if (!wasHovered && hovered) {
        wasHovered = true;
        hoveredButton = button;
      } 
    } 
    for (Button button : this.buttons) {
      if (!button.visible || button.removed || !button.renderLast || 
        button.shouldBeCulled())
        continue; 
      boolean hovered = button.isHovered(buttonX, buttonY);
      button.drawButton(buttonX, buttonY, (!wasHovered && hovered));
      if (!wasHovered && hovered) {
        wasHovered = true;
        hoveredButton = button;
      } 
    } 
    if (hoveredButton != null)
      hoveredButton.drawHoverOverlay(buttonX, buttonY); 
    GL11.glPopMatrix();
    drawOverlay(mouseX, mouseY, mouseXScaled, mouseYScaled, scaledScreen, partialTicks);
  }
  
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    boolean hasOverlay = hasOverlay();
    float scaledScreen = getScaledScreen();
    int mouseXScaled = (int)(mouseX / scaledScreen);
    int mouseYScaled = (int)(mouseY / scaledScreen);
    this.lastMouseClickPosX = mouseXScaled;
    this.lastMouseClickPosY = mouseYScaled;
    this.lastMouseClickButton = mouseButton;
    if (hasOverlay && (getCurrentOverlay()).pane != null && !(getCurrentOverlay()).pane.isHovered(mouseXScaled, mouseYScaled, false))
      removeOverlay(0); 
    if (!hasOverlay) {
      for (Button button : this.buttons) {
        if (!button.removed)
          button.mouseDown(this, mouseXScaled, mouseYScaled, mouseButton); 
      } 
      for (Button button : this.buttons) {
        if (button.visible && !button.removed && button.isHovered(mouseXScaled, mouseYScaled)) {
          button.onInteract(mouseXScaled, mouseYScaled, mouseButton);
          onButtonInteract(button, mouseXScaled, mouseYScaled, mouseButton);
          break;
        } 
      } 
    } else if (hasOverlay()) {
      getCurrentOverlay().mouseClicked(mouseX, mouseY, mouseButton);
    } 
  }
  
  public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    boolean hasOverlay = hasOverlay();
    if (!hasOverlay) {
      float scaledScreen = getScaledScreen();
      int mouseXScaled = (int)(mouseX / scaledScreen);
      int mouseYScaled = (int)(mouseY / scaledScreen);
      super.mouseReleased(mouseXScaled, mouseYScaled, mouseButton);
      for (Button button : this.buttons) {
        if (button.visible && !button.removed)
          button.mouseReleased(mouseXScaled, mouseYScaled, mouseButton); 
      } 
    } else {
      getCurrentOverlay().mouseReleased(mouseX, mouseY, mouseButton);
    } 
  }
  
  public void handleKeyboardInput() {
    if (Keyboard.getEventKeyState())
      if (this.ignoreFirstKeyPress && !this._keyPressed) {
        this._keyPressed = true;
      } else {
        keyTyped(Keyboard.getEventCharacter(), Keyboard.getEventKey());
      }  
    this.mc.dispatchKeypresses();
  }
  
  public void onGuiClosed() {
    if (!this.exited) {
      this.exited = true;
      for (Button button : this.buttons)
        button.onClose(); 
    } 
  }
}
