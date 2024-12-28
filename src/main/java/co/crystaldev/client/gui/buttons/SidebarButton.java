package co.crystaldev.client.gui.buttons;

import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.ease.Animation;
import co.crystaldev.client.gui.ease.Easing;
import co.crystaldev.client.gui.screens.ScreenBase;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.GuiType;
import co.crystaldev.client.util.objects.FadingColor;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class SidebarButton extends Button {
  private static Map<GuiType, SidebarButton> PERSISTENT;

  private final GuiType type;

  private final int expandedWidth;

  public GuiType getType() {
    return this.type;
  }

  private long hoverTime = 0L;

  private final FadingColor text;

  private boolean wasSelected;

  private boolean wasHovered;

  private int currentWidth = 1;

  private int targetWidth;

  private Animation animation;

  public SidebarButton(int x, int y, int width, int expandedWidth, int height, GuiType type) {
    super(-1, x, y, width, height, type.getDisplayText());
    this.expandedWidth = expandedWidth;
    this.type = type;
    this.text = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor, 200L);
    if (PERSISTENT == null)
      PERSISTENT = new HashMap<>();
    if (PERSISTENT.containsKey(type)) {
      SidebarButton b = PERSISTENT.get(type);
      this.wasHovered = b.wasHovered;
      this.wasSelected = b.wasSelected;
      this.targetWidth = b.targetWidth;
      this.currentWidth = b.currentWidth;
      this.animation = b.animation;
      PERSISTENT.remove(type);
    } else {
      this.wasSelected = (ScreenBase.getType() == this.type);
      if (this.wasSelected)
        this.currentWidth = this.targetWidth = this.expandedWidth;
    }
  }

  public void saveState() {
    PERSISTENT.remove(this.type);
    PERSISTENT.put(this.type, this);
  }

  public void drawButton(int mouseX, int mouseY, boolean hovered) {
    GL11.glPushMatrix();
    boolean selected = (ScreenBase.getType() == this.type);
    this.text.fade((selected || hovered));
    animate(selected, hovered);
    RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height), 3.3D, this.opts.neutralButtonBackground
        .getRGB());
    if (this.currentWidth < 8) {
      RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.currentWidth), (this.y + this.height), 3.3D, this.opts.mainColor.getRGB(), true, true, false, false);
    } else {
      RenderUtils.drawRoundedHorizontalGradientRect(this.x, this.y, (this.x + this.currentWidth), (this.y + this.height), 3.3D, this.opts.mainColor.getRGB(), this.opts.secondaryColor
          .getRGB());
    }
    Fonts.NUNITO_SEMI_BOLD_20.drawString(this.displayText, this.x + 8, this.y + this.height / 2 - Fonts.NUNITO_SEMI_BOLD_20.getStringHeight() / 2, this.text
        .getCurrentColor().getRGB());
    GL11.glPopMatrix();
  }

  public void animate(boolean selected, boolean hovered) {
    if (hovered && (this.hoverTime == 0L || System.currentTimeMillis() - this.hoverTime < 50L)) {
      if (this.hoverTime == 0L)
        this.hoverTime = System.currentTimeMillis();
      hovered = false;
    } else if (!hovered) {
      this.hoverTime = 0L;
    }
    if (selected != this.wasSelected || hovered != this.wasHovered || this.animation == null) {
      this.wasSelected = selected;
      this.wasHovered = hovered;
      this.targetWidth = selected ? this.expandedWidth : (hovered ? this.width : 1);
      this.animation = new Animation(175L, this.currentWidth, this.targetWidth, Easing.IN_OUT_CUBIC);
    }
    this.currentWidth = (int)this.animation.getValue();
  }
}
