package co.crystaldev.client.gui.buttons;

import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.ScrollPane;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

public class ScrollBarButton extends Button {
  private final ScrollPane pane;

  public ScrollPane getPane() {
    return this.pane;
  }

  private int lastMouseY = 0;

  private boolean mouseDown = false;

  private boolean clickedScrollBar = false;

  private final FadingColor unhoveredFadingColor;

  private final FadingColor fadingColor;

  public ScrollBarButton(ScrollPane pane, int y) {
    super(-1, pane.x + pane.width - 3 - 7, y + 3, 4, pane.y - y + pane.height - 6);
    this.pane = pane;
    this.unhoveredFadingColor = new FadingColor(this.opts.unselectedTextColor, 0, 80);
    this.fadingColor = new FadingColor(this.opts.unselectedTextColor, 80, 130);
  }

  public void drawButton(int mouseX, int mouseY, boolean hovered) {
    if (!this.pane.hasScrollBar())
      return;
    float maxScrollSize = this.pane.getMaxScrollSize();
    float amountScrolled = this.pane.getAmountScrolled();
    int top = this.y;
    int bottom = this.y + this.height - this.pane.getMargin();
    int height = (int)(((bottom - top) * (bottom - top)) / maxScrollSize);
    height = MathHelper.clamp_int(height, 32, bottom - top - 8);
    height = (int)(height - Math.min((amountScrolled < 0.0D) ? (int)-amountScrolled : ((amountScrolled > maxScrollSize) ? ((int)amountScrolled - maxScrollSize) : 0.0F), height * 0.75D));
    int y = (int)Math.min(Math.max(amountScrolled * (bottom - top - height) / maxScrollSize + top, top), (bottom - height));
    boolean paneHovered = this.pane.isHovered(mouseX, mouseY, false);
    boolean sidebarHovered = (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= y && mouseY <= y + height);
    if (sidebarHovered && Mouse.isButtonDown(0) && !this.mouseDown && this.clickedScrollBar) {
      this.mouseDown = true;
      this.lastMouseY = mouseY;
    } else if (!Mouse.isButtonDown(0) || !this.clickedScrollBar) {
      this.mouseDown = this.clickedScrollBar = false;
    }
    if (this.mouseDown &&
      this.lastMouseY != mouseY) {
      float percent = maxScrollSize / (this.pane.height - height) + 1.0F;
      this.pane.scrollTo((mouseY - this.lastMouseY) * percent, false);
      this.lastMouseY = mouseY;
    }
    this.unhoveredFadingColor.fade((this.pane.isHovered(mouseX, mouseY, false) || this.mouseDown));
    this.fadingColor.fade((sidebarHovered || this.mouseDown));
    RenderUtils.drawRoundedRect(this.x, y, (this.x + this.width), (y + height), Math.min(6.0F, this.width), (paneHovered || this.mouseDown) ? this.fadingColor
        .getCurrentColor().getRGB() : this.unhoveredFadingColor.getCurrentColor().getRGB());
  }

  public void mouseDown(Screen screen, int mouseX, int mouseY, int mouseButton) {
    super.mouseDown(screen, mouseX, mouseY, mouseButton);
    if (!this.pane.hasScrollBar() || mouseButton != 0)
      return;
    float maxScrollSize = this.pane.getMaxScrollSize();
    float amountScrolled = this.pane.getAmountScrolled();
    int top = this.y;
    int bottom = this.y + this.height - this.pane.getMargin();
    int height = (int)(((bottom - top) * (bottom - top)) / maxScrollSize);
    height = MathHelper.clamp_int(height, 32, bottom - top - 8);
    height = (int)(height - Math.min((amountScrolled < 0.0D) ? (int)-amountScrolled : ((amountScrolled > maxScrollSize) ? ((int)amountScrolled - maxScrollSize) : 0.0F), height * 0.75D));
    int y = (int)Math.min(Math.max(amountScrolled * (bottom - top - height) / maxScrollSize + top, top), (bottom - height));
    boolean paneHovered = this.pane.isHovered(mouseX, mouseY, false);
    boolean sidebarHovered = (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= y && mouseY <= y + height);
    this.clickedScrollBar = (paneHovered && sidebarHovered);
  }
}
