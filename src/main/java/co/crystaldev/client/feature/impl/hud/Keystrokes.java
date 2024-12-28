package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.player.InputEvent;
import co.crystaldev.client.feature.annotations.properties.Colour;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Slider;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModule;
import co.crystaldev.client.gui.ease.Easing;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.AnchorRegion;
import co.crystaldev.client.util.objects.FadingColor;
import co.crystaldev.client.util.objects.ModulePosition;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Iterator;

@ModuleInfo(name = "Keystrokes", description = "Displays the WASD keys, mouse buttons, and space bar onscreen", category = Category.HUD)
public class Keystrokes extends HudModule implements IRegistrable {
  @Toggle(label = "Show Mouse Buttons")
  public boolean showMouseButtons = true;
  
  @Toggle(label = "Show Mouse CPS")
  public boolean showCps = false;
  
  @Toggle(label = "Draw Background")
  public boolean drawBackground = true;
  
  @Toggle(label = "Show Spacebar")
  public boolean showSpacebar = false;
  
  @Toggle(label = "Use Full Spacebar Height")
  public boolean fullSpacebarHeight = false;
  
  @Slider(label = "Box Size", minimum = 20.0D, maximum = 30.0D, standard = 25.0D, integers = true)
  public int boxSize = 25;
  
  @Colour(label = "Background Color (Pressed)")
  public ColorObject pressedBackgroundColor = new ColorObject(255, 255, 255, 140);
  
  @Colour(label = "Background Color")
  public ColorObject backgroundColor = new ColorObject(0, 0, 0, 97);
  
  @Colour(label = "Text Color (Pressed)", isTextRender = true)
  public ColorObject pressedColor = new ColorObject(60, 60, 60, 255);
  
  @Colour(label = "Text Color", isTextRender = true)
  public ColorObject textColor = new ColorObject(255, 255, 255, 255);
  
  private int oldBoxSize = 0;
  
  private final ArrayDeque<Key> keys = new ArrayDeque<>();
  
  private final ArrayDeque<Long> clickListLeft = new ArrayDeque<>(), clickListRight = new ArrayDeque<>();
  
  public Keystrokes() {
    this.position = new ModulePosition(AnchorRegion.TOP_RIGHT, 5.0F, 19.0F);
  }
  
  public void configPostInit() {
    super.configPostInit();
    setOptionVisibility("Background Color (Pressed)", f -> this.drawBackground);
    setOptionVisibility("Background Color", f -> this.drawBackground);
    setOptionVisibility("Use Full Spacebar Height", f -> this.showSpacebar);
  }
  
  public void onUpdate() {
    updateKeys();
  }
  
  public void draw() {
    if (this.oldBoxSize != this.boxSize) {
      this.oldBoxSize = this.boxSize;
      updateKeys();
    } 
    synchronized (this.keys) {
      Iterator<Key> iterator = this.keys.iterator();
      while (iterator.hasNext())
        ((Key)iterator.next()).draw(); 
    } 
  }
  
  public void disable() {
    this.clickListLeft.clear();
    this.clickListRight.clear();
    super.disable();
  }
  
  private int getLeftCps() {
    this.clickListLeft.removeIf(x -> (x.longValue() < System.currentTimeMillis() - 1000L));
    return this.clickListLeft.size();
  }
  
  private int getRightCps() {
    this.clickListRight.removeIf(x -> (x.longValue() < System.currentTimeMillis() - 1000L));
    return this.clickListRight.size();
  }
  
  private void updateKeys() {
    this.keys.clear();
    this.width = this.boxSize * 3 + (int)(this.boxSize * 0.2D);
    this.height = 0;
    int y = 0, separator = (int)(this.boxSize * 0.1D);
    this.keys.add(new Key(this, this.width / 2 - this.boxSize / 2, 0, this.boxSize, this.boxSize, this.mc.gameSettings.keyBindForward, new FadingColor((Color)this.backgroundColor, (Color)this.pressedBackgroundColor, 170L, Easing.IN_OUT_EXP)));
    y += this.boxSize + separator;
    this.keys.add(new Key(this, 0, y, this.boxSize, this.boxSize, this.mc.gameSettings.keyBindLeft, new FadingColor((Color)this.backgroundColor, (Color)this.pressedBackgroundColor, 170L, Easing.IN_OUT_EXP)));
    this.keys.add(new Key(this, this.width / 2 - this.boxSize / 2, y, this.boxSize, this.boxSize, this.mc.gameSettings.keyBindBack, new FadingColor((Color)this.backgroundColor, (Color)this.pressedBackgroundColor, 170L, Easing.IN_OUT_EXP)));
    this.keys.add(new Key(this, this.width - this.boxSize, y, this.boxSize, this.boxSize, this.mc.gameSettings.keyBindRight, new FadingColor((Color)this.backgroundColor, (Color)this.pressedBackgroundColor, 170L, Easing.IN_OUT_EXP)));
    y += this.boxSize + separator;
    if (this.showMouseButtons) {
      this.keys.add(new Key(this, 0, y, this.width / 2 - separator / 2, this.boxSize, this.mc.gameSettings.keyBindAttack, new FadingColor((Color)this.backgroundColor, (Color)this.pressedBackgroundColor, 20L, Easing.OUT_QUART)));
      this.keys.add(new Key(this, this.width / 2 + separator / 2, y, this.width / 2 - separator / 2, this.boxSize, this.mc.gameSettings.keyBindUseItem, new FadingColor((Color)this.backgroundColor, (Color)this.pressedBackgroundColor, 20L, Easing.OUT_QUART)));
      y += this.boxSize + separator;
    } 
    if (this.showSpacebar)
      this.keys.add(new Key(this, 0, y, this.width, this.fullSpacebarHeight ? this.boxSize : (this.boxSize / 2), this.mc.gameSettings.keyBindJump, new FadingColor((Color)this.backgroundColor, (Color)this.pressedBackgroundColor, 170L, Easing.IN_OUT_EXP))); 
    y = 0;
    for (Key key : this.keys)
      y = Math.max(y, key.y + key.height); 
    this.height = y;
  }
  
  public void registerEvents() {
    EventBus.register(this, InputEvent.Mouse.class, ev -> {
          if (ev.buttonState && ev.button == 0) {
            this.clickListLeft.add(Long.valueOf(System.currentTimeMillis()));
          } else if (ev.buttonState && ev.button == 1) {
            this.clickListRight.add(Long.valueOf(System.currentTimeMillis()));
          } 
        });
  }
  
  private static class Key {
    private final Keystrokes ks;
    
    private final int x;
    
    private final int y;
    
    private final int width;
    
    private final int height;
    
    private final KeyBinding keybind;
    
    private final FadingColor fadingColor;
    
    private final FadingColor textColor;
    
    public Key(Keystrokes ks, int x, int y, int width, int height, KeyBinding keybind, FadingColor color) {
      this.ks = ks;
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.keybind = keybind;
      this.fadingColor = color;
      this.textColor = new FadingColor((Color)ks.textColor, (Color)ks.pressedColor, color.getFadeTimeMs());
    }
    
    public void draw() {
      GL11.glPushMatrix();
      GL11.glTranslated(this.ks.getRenderX(), this.ks.getRenderY(), 0.0D);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.fadingColor.fade(this.keybind.isPressed());//getIsKeyPressed
      this.textColor.fade(this.keybind.isPressed());
      if (this.ks.drawBackground) {
        RenderUtils.drawRect(this.x, this.y, (this.x + this.width), (this.y + this.height), this.fadingColor
            .getCurrentColorObject());
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      } 
      if (this.keybind.equals(this.ks.mc.gameSettings.keyBindAttack) || this.keybind.equals(this.ks.mc.gameSettings.keyBindUseItem)) {
        String display;
        if (this.keybind.equals(this.ks.mc.gameSettings.keyBindAttack)) {
          display = (this.ks.boxSize < 25) ? "L" : "LMB";
        } else {
          display = (this.ks.boxSize < 25) ? "R" : "RMB";
        } 
        if (this.ks.showCps) {
          RenderUtils.drawCenteredString(display, this.x + this.width / 2, this.y + this.height / 3, this.textColor
              .getCurrentColorObject());
          GL11.glScaled(0.62D, 0.62D, 1.0D);
          GL11.glTranslated(this.x * 0.626D, this.y * 0.627D, 0.0D);
          display = (display.startsWith("L") ? this.ks.getLeftCps() : this.ks.getRightCps()) + " CPS";
          RenderUtils.drawCenteredString(display, this.x + this.width * 0.789F, this.y + this.height * 0.733F + 4.0F, this.textColor
              .getCurrentColorObject());
        } else {
          RenderUtils.drawCenteredString(display, this.x + this.width / 2, this.y + this.height / 2, this.textColor
              .getCurrentColorObject());
        } 
      } else if (this.keybind.equals(this.ks.mc.gameSettings.keyBindJump)) {
        RenderUtils.drawCenteredString(Character.toString('-'), this.x + this.width / 2, this.y + this.height / 2, this.textColor
            .getCurrentColorObject());
      } else {
        RenderUtils.drawCenteredString(GameSettings.getKeyDisplayString(this.keybind.getKeyCode()), this.x + this.width / 2, this.y + this.height / 2, this.textColor
            .getCurrentColorObject());
      } 
      GL11.glPopMatrix();
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\hud\Keystrokes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */