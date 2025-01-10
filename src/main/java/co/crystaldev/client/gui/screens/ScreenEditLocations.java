package co.crystaldev.client.gui.screens;

import co.crystaldev.client.feature.base.HudModule;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.feature.impl.hud.InfoHud;
import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.font.FontRenderer;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.handler.ModuleHandler;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.ModulePosition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ScreenEditLocations extends Screen {
  private final List<HudModule> modules = new LinkedList<>();

  private HudModule lastDragged = null;

  private boolean dragging = false;

  private int lastMouseX;

  private int lastMouseY;

  private static final int SNAP_RANGE = 2;

  private final List<SnapPosition> snapPositions = new ArrayList<>();

  public ScreenEditLocations(GuiScreen parent) {
    super(parent);
    Keyboard.enableRepeatEvents(true);
    for (Module module : ModuleHandler.getModules()) {
      if (module instanceof HudModule)
        this.modules.add((HudModule)module);
    }
  }

  public void onGuiClosed() {
    Keyboard.enableRepeatEvents(false);
    super.onGuiClosed();
  }

  public void draw(int mouseX, int mouseY, float partialTicks) {
    RenderUtils.drawBorderedRect(0.5F, 0.5F, (MathHelper.floor_float(this.mc.displayWidth / 2.0F) - 1),
        MathHelper.floor_float(this.mc.displayHeight / 2.0F), 1.0F, this.opts.mainColor, new Color(0, 0, 0, 0));
    for (HudModule module : this.modules) {
      if (((!module.displayWhileDisabled || !(ClientOptions.getInstance()).showDisabledModulesInEditHUD) && !module.enabled) || ((InfoHud.getInstance()).enabled && !InfoHud.getInstance().shouldModuleRender((Module)module)))
        continue;
      GL11.glPushMatrix();
      GL11.glScaled(module.scale, module.scale, module.scale);
      int x = module.getRenderX();
      int y = module.getRenderY();
      Color c = (this.dragging && this.lastDragged == module) ? this.opts.mainColor : (!module.enabled ? (module.isHovered(mouseX, mouseY) ? this.opts.mainRed : this.opts.secondaryRed) : (module.isHovered(mouseX, mouseY) ? this.opts.hoveredTextColor : this.opts.neutralTextColor));
      RenderUtils.drawBorderedRect(x, y, (x + module.width), (y + module.height), 1.3F, this.opts
          .getColorObject(c, 225), this.opts.getColorObject(c, 60));
      GL11.glPopMatrix();
      if (module.isHovered(mouseX, mouseY)) {
        FontRenderer fr = Fonts.NUNITO_REGULAR_16;
        int fw = fr.getStringWidth(module.name);
        int fh = fr.getStringHeight(module.name);
        int textX = mouseX + 2;
        int textY = mouseY - fh - 2;
        textX = (textX + fw + 1 >= this.mc.displayWidth / 2) ? (this.mc.displayWidth / 2 - fw - 2) : Math.max(textX, 1);
        textY = (textY + fh + 1 >= this.mc.displayHeight / 2) ? (this.mc.displayHeight / 2 - fh - 2) : Math.max(textY, 1);
        RenderUtils.drawRoundedRectWithBorder((textX - 1), (textY - 1), (textX + 1 + fw), (textY + 1 + fh), 5.0D, 1.0F, this.opts.getColor(c, 255).getRGB(), this.opts.hoveredButtonBackground.getRGB());
        fr.drawString(module.name, textX, textY, c.getRGB());
      }
    }
    RenderUtils.setGlColor(this.opts.mainColor);
    for (SnapPosition snapPosition : this.snapPositions) {
      if (snapPosition.orientation == SnapPosition.Orientation.HORIZONTAL) {
        RenderUtils.drawLine(0.5F, 0.0F, snapPosition.y, this.mc.displayWidth / 2.0F, snapPosition.y);
        continue;
      }
      RenderUtils.drawLine(0.5F, snapPosition.x, 0.0F, snapPosition.x, this.mc.displayHeight / 2.0F);
    }
    RenderUtils.resetColor();
    if (this.dragging && (mouseX != this.lastMouseX || mouseY != this.lastMouseY)) {
      handleModuleMovement(mouseX - this.lastMouseX, mouseY - this.lastMouseY, true);
      this.lastMouseX = mouseX;
      this.lastMouseY = mouseY;
    }
  }

  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    float scaledScreen = getScaledScreen();
    int mouseXScaled = (int)(mouseX / scaledScreen);
    int mouseYScaled = (int)(mouseY / scaledScreen);
    for (HudModule module : this.modules) {
      if (((!module.displayWhileDisabled || !(ClientOptions.getInstance()).showDisabledModulesInEditHUD) && !module.enabled) || ((InfoHud.getInstance()).enabled && !InfoHud.getInstance().shouldModuleRender((Module)module)))
        continue;
      if (module.isHovered(mouseXScaled, mouseYScaled)) {
        if (mouseButton == 0) {
          this.dragging = true;
          this.lastDragged = module;
          this.lastMouseX = mouseXScaled;
          this.lastMouseY = mouseYScaled;
          break;
        }
        if (mouseButton == 1) {
          module.enabled = !module.enabled;
          break;
        }
      }
    }
  }

  public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    super.mouseReleased(mouseX, mouseY, mouseButton);
    if (mouseButton == 0 && this.dragging) {
      this.dragging = false;
      this.snapPositions.clear();
      this.lastDragged.setupAnchorRegion();
    }
  }

  public void onResize(Minecraft mc, int width, int height) {
    super.onResize(mc, width, height);
    if (this.lastDragged != null && this.dragging) {
      this.dragging = false;
      this.snapPositions.clear();
      this.lastDragged.setupAnchorRegion();
    }
  }

  public void keyTyped(char charTyped, int keyCode) {
    super.keyTyped(charTyped, keyCode);
    int offsetX = 0, offsetY = 0;
    switch (keyCode) {
      case 200:
        offsetY--;
        break;
      case 208:
        offsetY++;
        break;
      case 203:
        offsetX--;
        break;
      case 205:
        offsetX++;
        break;
      case 17:
        offsetY -= 5;
        break;
      case 31:
        offsetY += 5;
        break;
      case 30:
        offsetX -= 5;
        break;
      case 32:
        offsetX += 5;
        break;
    }
    this.snapPositions.clear();
    handleModuleMovement(offsetX, offsetY, false);
  }

  private void handleModuleMovement(int offsetX, int offsetY, boolean checkForSnapping) {
    System.out.println("handlemousemovement screenedit");
    if (this.lastDragged != null && ((this.lastDragged.displayWhileDisabled && (ClientOptions.getInstance()).showDisabledModulesInEditHUD) || this.lastDragged.enabled) && (
      !(InfoHud.getInstance()).enabled || InfoHud.getInstance().shouldModuleRender((Module)this.lastDragged))) {
      int snapBackX = 0, snapBackY = 0;
      if (checkForSnapping)
        for (SnapPosition snapPosition : this.snapPositions) {
          if (snapPosition.getOrientation() == SnapPosition.Orientation.VERTICAL) {
            if (!snapPosition.canBreakFreeX()) {
              offsetX = 0;
              continue;
            }
            snapBackX = (int)((Mouse.getX() / 2) - snapPosition.initialMouseX);
            continue;
          }
          if (!snapPosition.canBreakFreeY()) {
            offsetY = 0;
            continue;
          }
          snapBackY = (int)(((this.mc.displayHeight - Mouse.getY()) / 2) - snapPosition.initialMouseY);
        }
      ModulePosition pos = this.lastDragged.position;
      pos.setX(pos.getX() + ((pos.getAnchorRegion().isRightSided() ? -1 : 1) * offsetX));
      pos.setY(pos.getY() + ((pos.getAnchorRegion().isBottomSided() ? -1 : 1) * offsetY));
      this.lastDragged.setupAnchorRegion();
      if (checkForSnapping) {
        refreshSnapPositions();
        handleModuleMovement(snapBackX, snapBackY, false);
      }
    }
  }

  private void refreshSnapPositions() {
    int lastX = this.lastDragged.getX(), lastY = this.lastDragged.getY();
    int lastX1 = lastX + (int)(this.lastDragged.width * this.lastDragged.scale);
    int lastY1 = lastY + (int)(this.lastDragged.height * this.lastDragged.scale);
    int xMid = lastX + (int)(this.lastDragged.width * this.lastDragged.scale) / 2;
    int yMid = lastY + (int)(this.lastDragged.height * this.lastDragged.scale) / 2;
    if (xMid == this.mc.displayWidth / 4) {
      addSnapPosition(new SnapPosition(SnapPosition.Orientation.VERTICAL, this.lastDragged, xMid, yMid));
    } else if (yMid == this.mc.displayHeight / 4) {
      addSnapPosition(new SnapPosition(SnapPosition.Orientation.HORIZONTAL, this.lastDragged, xMid, yMid));
    } else {
      label52: for (HudModule module : this.modules) {
        if ((!module.displayWhileDisabled || !(ClientOptions.getInstance()).showDisabledModulesInEditHUD) && !module.enabled)
          continue;
        if (module == this.lastDragged)
          continue;
        for (SnapPosition pos : this.snapPositions) {
          if (pos.module == module)
            continue label52;
        }
        int x = module.getX(), y = module.getY();
        int x1 = x + (int)(module.width * module.scale), y1 = y + (int)(module.height * module.scale);
        if (x1 == lastX) {
          addSnapPosition(new SnapPosition(SnapPosition.Orientation.VERTICAL, module, x1, y1));
          continue;
        }
        if (x1 == lastX1) {
          addSnapPosition(new SnapPosition(SnapPosition.Orientation.VERTICAL, module, x1, y1));
          continue;
        }
        if (x == lastX1) {
          addSnapPosition(new SnapPosition(SnapPosition.Orientation.VERTICAL, module, x, y));
          continue;
        }
        if (x == lastX) {
          addSnapPosition(new SnapPosition(SnapPosition.Orientation.VERTICAL, module, x, y));
          continue;
        }
        if (y1 == lastY1) {
          addSnapPosition(new SnapPosition(SnapPosition.Orientation.HORIZONTAL, module, x1, y1));
          continue;
        }
        if (y1 == lastY) {
          addSnapPosition(new SnapPosition(SnapPosition.Orientation.HORIZONTAL, module, x1, y1));
          continue;
        }
        if (y == lastY1) {
          addSnapPosition(new SnapPosition(SnapPosition.Orientation.HORIZONTAL, module, x, y));
          continue;
        }
        if (y == lastY)
          addSnapPosition(new SnapPosition(SnapPosition.Orientation.HORIZONTAL, module, x, y));
      }
    }
    this.snapPositions.removeIf(SnapPosition::canBeRemoved);
  }

  private void addSnapPosition(SnapPosition pos) {
    for (SnapPosition snapPosition : this.snapPositions) {
      if (snapPosition.orientation == pos.orientation) {
        if (pos.module == snapPosition.module || pos.equals(snapPosition))
          return;
        if (pos.orientation == SnapPosition.Orientation.VERTICAL) {
          if (Math.abs(pos.x - snapPosition.x) <= 2)
            return;
          continue;
        }
        if (Math.abs(pos.y - snapPosition.y) <= 2)
          return;
      }
    }
    this.snapPositions.add(pos);
  }

  private static class SnapPosition {
    private final Orientation orientation;

    private final HudModule module;

    private final int x;

    private final int y;

    private final double initialMouseX;

    private final double initialMouseY;

    public Orientation getOrientation() {
      return this.orientation;
    }

    public HudModule getModule() {
      return this.module;
    }

    public int getX() {
      return this.x;
    }

    public int getY() {
      return this.y;
    }

    public double getInitialMouseX() {
      return this.initialMouseX;
    }

    public double getInitialMouseY() {
      return this.initialMouseY;
    }

    private boolean brokenFree = false;

    public boolean isBrokenFree() {
      return this.brokenFree;
    }

    public SnapPosition(Orientation orientation, HudModule module, int x, int y) {
      this.orientation = orientation;
      this.module = module;
      this.x = x;
      this.y = y;
      this.initialMouseX = Mouse.getX() / 2.0D;
      this.initialMouseY = ((Minecraft.getMinecraft()).displayHeight - Mouse.getY()) / 2.0D;
    }

    public boolean canBeRemoved() {
      return (this.brokenFree && canBreakFreeX() && canBreakFreeY());
    }

    private boolean canBreakFreeX() {
      if (this.orientation == Orientation.VERTICAL) {
        boolean res = (Math.abs(Mouse.getX() / 2.0D - this.initialMouseX) > 2.0D);
        if (res)
          this.brokenFree = true;
        return (res || this.brokenFree);
      }
      return true;
    }

    private boolean canBreakFreeY() {
      if (this.orientation == Orientation.HORIZONTAL) {
        boolean res = (Math.abs(((Minecraft.getMinecraft()).displayHeight - Mouse.getY()) / 2.0D - this.initialMouseY) > 2.0D);
        if (res)
          this.brokenFree = true;
        return (res || this.brokenFree);
      }
      return true;
    }

    public boolean equals(Object obj) {
      if (!(obj instanceof SnapPosition))
        return false;
      SnapPosition pos = (SnapPosition)obj;
      return (pos.orientation == this.orientation && pos.x == this.x && pos.y == this.y);
    }

    enum Orientation {
      VERTICAL, HORIZONTAL;
    }
  }

  enum Orientation {
    VERTICAL, HORIZONTAL;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\ScreenEditLocations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */