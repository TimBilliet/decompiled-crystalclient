package co.crystaldev.client.gui;

import co.crystaldev.client.font.FontRenderer;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Button {
  public String toString() {
    return "Button(BUTTON_ID=" + this.BUTTON_ID + ", attributes=" + this.attributes + ", mc=" + this.mc + ", opts=" + this.opts + ", scissorPane=" + getScissorPane() + ", hoverOverlay=" + getHoverOverlay() + ", scale=" + this.scale + ", fontRenderer=" + this.fontRenderer + ", id=" + this.id + ", x=" + this.x + ", y=" + this.y + ", initialX=" + this.initialX + ", initialY=" + this.initialY + ", width=" + this.width + ", height=" + this.height + ", visible=" + this.visible + ", displayText=" + this.displayText + ", lastClickedMouseX=" + this.lastClickedMouseX + ", lastClickedMouseY=" + this.lastClickedMouseY + ", lastClickedMouseTime=" + this.lastClickedMouseTime + ", lastClickedMouseButton=" + this.lastClickedMouseButton + ", removed=" + this.removed + ", renderLast=" + this.renderLast + ")";
  }
  
  private static final AtomicInteger COUNTER = new AtomicInteger();
  
  protected final int BUTTON_ID = COUNTER.getAndIncrement();
  
  protected final Set<String> attributes = new HashSet<>();
  
  protected final Minecraft mc;
  
  protected final GuiOptions opts;
  
  protected Pane scissorPane = null;
  
  public Pane getScissorPane() {
    return this.scissorPane;
  }
  
  public void setScissorPane(Pane scissorPane) {
    this.scissorPane = scissorPane;
  }
  
  protected ButtonHoverOverlay hoverOverlay = null;
  
  protected float scale;
  
  public ButtonHoverOverlay getHoverOverlay() {
    return this.hoverOverlay;
  }
  
  public void setHoverOverlay(ButtonHoverOverlay hoverOverlay) {
    this.hoverOverlay = hoverOverlay;
  }
  
  public FontRenderer fontRenderer = Fonts.NUNITO_REGULAR_20;
  
  public int id;
  
  public int x;
  
  public int y;
  
  public int initialX;
  
  public int initialY;
  
  public int width;
  
  public int height;
  
  public void setFontRenderer(FontRenderer fontRenderer) {
    this.fontRenderer = fontRenderer;
  }
  
  public boolean visible = true;
  
  public String displayText;
  
  public int lastClickedMouseX = 0;
  
  public int lastClickedMouseY = 0;
  
  public long lastClickedMouseTime = -1L;
  
  public int lastClickedMouseButton = 0;
  
  public boolean removed = false;
  
  public boolean renderLast = false;
  
  public Button(int id, int x, int y, int width, int height) {
    this(id, x, y, width, height, null);
  }
  
  public Button(int id, int x, int y, int width, int height, String displayText) {
    this.mc = Minecraft.getMinecraft();
    this.opts = GuiOptions.getInstance();
    this.scale = getScaledScreen();
    this.id = id;
    this.x = x;
    this.y = y;
    this.initialX = x;
    this.initialY = y;
    this.width = width;
    this.height = height;
    this.displayText = displayText;
  }
  
  public void drawHoverOverlay(int mouseX, int mouseY) {
    if (!hasHoverOverlay() || !shouldOverlayBeRendered(mouseX, mouseY))
      return; 
    GL11.glPushMatrix();
    boolean wasScissor = GL11.glGetBoolean(3089);
    GL11.glDisable(3089);
    GL11.glTranslated(mouseX + 4.0D, mouseY - Math.min(this.hoverOverlay.getHeight() / 2.0D, this.hoverOverlay.getFontRenderer().getStringHeight()), 1.0D);
    RenderUtils.drawRoundedRect(0.0D, 0.0D, this.hoverOverlay.getWidth(), this.hoverOverlay.getHeight(), 9.0D, this.opts.backgroundColor.getRGB());
    RenderUtils.drawRoundedRect(0.0D, 0.0D, this.hoverOverlay.getWidth(), this.hoverOverlay.getHeight(), 9.0D, this.opts.backgroundColor1.getRGB());
    FontRenderer fr = this.hoverOverlay.getFontRenderer();
    int tX = 3, tY = 3;
    for (String line : this.hoverOverlay.getLines()) {
      fr.drawString(line, tX, tY, 16777215);
      tY += fr.getStringHeight(line);
    } 
    if (wasScissor)
      GL11.glEnable(3089); 
    GL11.glPopMatrix();
  }
  
  public void onInteract(int mouseX, int mouseY, int mouseButton) {
    this.lastClickedMouseX = mouseX;
    this.lastClickedMouseY = mouseY;
    this.lastClickedMouseButton = mouseButton;
    this.lastClickedMouseTime = System.currentTimeMillis();
  }
  
  public void mouseDown(Screen currentScreen, int mouseX, int mouseY, int mouseButton) {}
  
  public void mouseReleased(int mouseX, int mouseY, int mouseButton) {}
  
  public float getScaledScreen() {
    GuiScreen screen = (Minecraft.getMinecraft()).currentScreen;
    if (screen instanceof co.crystaldev.client.gui.screens.override.ScreenMainMenu)
      return 1.0F;
    int s = (new ScaledResolution(Minecraft.getMinecraft())).getScaleFactor();
    return 1.0F / 0.5F * s;
  }
  
  public boolean isHovered(int mouseX, int mouseY) {
    if (this.scissorPane != null && (
      mouseX < this.scissorPane.x / this.scale || mouseX > (this.scissorPane.x + this.scissorPane.width) / this.scale || mouseY < this.scissorPane.y / this.scale || mouseY > (this.scissorPane.y + this.scissorPane.height) / this.scale))
      return false; 
    return (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height);
  }
  
  public boolean shouldBeCulled() {
    if (this.scissorPane == null)
      return false; 
    float px = this.scissorPane.x / this.scale;
    float py = this.scissorPane.y / this.scale;
    float pw = this.scissorPane.width / this.scale;
    float ph = this.scissorPane.height / this.scale;
    return ((this.x + this.width) < px || this.x > px + pw || (this.y + this.height) < py || this.y > py + ph);
  }
  
  public boolean onKeyTyped(char charTyped, int keyCode) {
    return true;
  }
  
  public void onClose() {}
  
  public void onUpdate() {}
  
  public boolean onScroll(ScrollPane pane, int mouseX, int mouseY, int dWheel) {
    return false;
  }
  
  public boolean shouldOverlayBeRendered(int mouseX, int mouseY) {
    return true;
  }
  
  public boolean hasHoverOverlay() {
    return (this.hoverOverlay != null);
  }
  
  public boolean hasAttribute(String attribute) {
    for (String str : this.attributes) {
      if (str.equalsIgnoreCase(attribute))
        return true; 
    } 
    return false;
  }
  
  public void addAttribute(String attribute) {
    this.attributes.add(attribute);
  }
  
  public void removeAttribute(String attribute) {
    this.attributes.removeIf(a -> a.equalsIgnoreCase(attribute));
  }
  
  public boolean equals(Object object) {
    return (object instanceof Button && this.BUTTON_ID == ((Button)object).BUTTON_ID);
  }
  
  public abstract void drawButton(int paramInt1, int paramInt2, boolean paramBoolean);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\Button.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */