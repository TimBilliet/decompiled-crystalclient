package co.crystaldev.client.gui;

import co.crystaldev.client.Reference;
import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.gui.buttons.ScrollBarButton;
import co.crystaldev.client.util.ScrollUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.function.Predicate;

public class ScrollPane extends Pane {
  private int maxScrollSize = 0;

  public void setMaxScrollSize(int maxScrollSize) {
    this.maxScrollSize = maxScrollSize;
  }

  public int getMaxScrollSize() {
    return this.maxScrollSize;
  }

  private float target = 0.0F;

  private float amountScrolled;

  public float getTarget() {
    return this.target;
  }

  public void setTarget(float target) {
    this.target = target;
  }

  public float getAmountScrolled() {
    return this.amountScrolled;
  }

  public void setAmountScrolled(float amountScrolled) {
    this.amountScrolled = amountScrolled;
  }

  private Predicate<Button> scrollIf = b -> true;

  public Predicate<Button> getScrollIf() {
    return this.scrollIf;
  }

  public void setScrollIf(Predicate<Button> scrollIf) {
    this.scrollIf = scrollIf;
  }

  private int margin = 0;

  public int getMargin() {
    return this.margin;
  }

  private int lastMarginInc = 0;

  public int getLastMarginInc() {
    return this.lastMarginInc;
  }

  private long start = 0L;

  public ScrollPane(int x, int y, int width, int height) {
    super(x, y, width, height);
  }

  public ScrollPane(double x, double y, double width, double height) {
    super(x, y, width, height);
  }

  public ScrollPane(float x, float y, float width, float height) {
    super(x, y, width, height);
  }

  public ScrollPane(Pane pane) {
    super(pane.x, pane.y, pane.width, pane.height);
    if (pane instanceof ScrollPane) {
      ScrollPane scrollPane = (ScrollPane)pane;
      this.scrollIf = scrollPane.scrollIf;
    }
  }

  public void scroll(Screen currentScreen, int mouseX, int mouseY) {
    if (currentScreen.hasOverlay())
      return;
    scroll(currentScreen, mouseX, mouseY, Mouse.getDWheel());
  }

  public void scroll(Screen currentScreen, int mouseX, int mouseY, int wheel) {
    if (currentScreen.hasOverlay())
      return;
    scroll(currentScreen.buttons, mouseX, mouseY, wheel, currentScreen.getScaledScreen());
  }

  public void scroll(Collection<Button> buttons, int mouseX, int mouseY, float scale) {
    scroll(buttons, mouseX, mouseY, Mouse.getDWheel(), scale);
  }

  public void scroll(Collection<Button> buttons, int mouseX, int mouseY, int wheel, float guiScale) {
    mouseX = (int)(mouseX / guiScale);
    mouseY = (int)(mouseY / guiScale);
    for (Button button : buttons) {
      if (button.onScroll(this, mouseX, mouseY, wheel))
        return;
    }
    float[] target = { this.target };
    this.amountScrolled = ScrollUtils.handleScrollingPosition(target, this.amountScrolled, this.maxScrollSize, 20.0F /
        Minecraft.getDebugFPS(), this.start, (ClientOptions.getInstance()).scrollDuration * 0.4D);
    this.amountScrolled = ScrollUtils.clamp(this.amountScrolled, getMaxScrollSize());
    this.target = ScrollUtils.clamp(target[0], this.maxScrollSize);
    if (Mouse.isButtonDown(0) && hasScrollBar()) {
      this.target = this.amountScrolled = ScrollUtils.clamp(this.amountScrolled, getMaxScrollSize(), 0.0F);
    } else if (isHovered(mouseX, mouseY, true) &&
      wheel != 0) {
      int invert = (ClientOptions.getInstance()).invertScrollDirection ? -1 : 1;
      wheel = ((wheel > 0) ? -1 : 1) * invert;
      scrollTo((float)((ClientOptions.getInstance()).scrollStep * 1.649999976158142D * wheel), true);
    }
    updateButtons(buttons);
  }

  public void scrollTo(float value, boolean animated) {
    this.target = ScrollUtils.clamp(this.target + value, getMaxScrollSize());
    if (animated) {
      this.start = System.currentTimeMillis();
    } else {
      this.amountScrolled = this.target;
    }
  }

  public void updateButtons(Collection<Button> buttons) {
    try {
      for (Button button : buttons) {
        if (this.scrollIf.test(button)) {
          button.y = MathHelper.clamp_int((int)(button.initialY - this.amountScrolled), button.initialY - this.maxScrollSize, button.initialY);
          button.onUpdate();
        }
      }
    } catch (ConcurrentModificationException ex) {
      Reference.LOGGER.error("Error while updating button locations", ex);
    }
  }

  public void reset() {
    ScrollPane def = new ScrollPane(this);
    this.target = def.target;
    this.start = def.start;
    this.margin = def.margin;
    this.lastMarginInc = def.lastMarginInc;
    this.amountScrolled = def.amountScrolled;
  }

  public boolean hasScrollBar() {
    return (this.maxScrollSize + this.height > this.height && (float)this.height / (this.maxScrollSize + this.height) < 0.95F);
  }

  public void updateMaxScroll(Collection<Button> buttons, Collection<Button> invalidButtons, int marginInc) {
    this.lastMarginInc = marginInc;
    int minY = 0;
    int maxY = 0;
    int margin = 0;
    for (Button button : buttons) {
      if ((invalidButtons != null && invalidButtons.contains(button)) || !button.visible)
        continue;
      if (this.scrollIf.test(button)) {
        if (minY == 0)
          minY = button.initialY;
        minY = Math.min(minY, button.initialY);
        maxY = Math.max(maxY, button.initialY + button.height);
      //heeft niks te maken met het afsnijden van de scrollpane
//        maxY = 800;
        margin = minY - this.y + marginInc;
      }
    }
    this.margin = margin + marginInc;
    int bottomY = this.y + this.height - margin + marginInc;
    this.maxScrollSize = (maxY < this.y + this.height - margin + marginInc) ? 0 : (maxY - bottomY);
  }

  public void updateMaxScroll(Screen screen, int marginInc) {
    updateMaxScroll(screen.buttons, screen.buttonsToRemove, marginInc);
  }

  public void addScrollbarToScreen(Screen screen, int y) {
    addScrollbarToScreen(screen, (String)null, y);
  }

  public void addScrollbarToScreen(Screen screen, String attribute) {
    addScrollbarToScreen(screen, attribute, this.y);
  }

  public void addScrollbarToScreen(Screen screen) {
    addScrollbarToScreen(screen, (String)null, this.y);
  }

  public void addScrollbarToScreen(Screen screen, String attribute, int y) {
    System.out.println("addscrollbartoscreen");
    screen.removeButton(b -> (b instanceof ScrollBarButton && ((ScrollBarButton)b).getPane().equals(this)));
    ScrollBarButton button = new ScrollBarButton(this, y);
    System.out.println(scale(screen.getScaledScreen()));
    button.setScissorPane(scale(screen.getScaledScreen()));
    if (attribute != null)
      button.addAttribute(attribute);
    if (hasScrollBar())
      for (Button b : screen.buttons) {
        if (this.scrollIf.test(b) && b.x + b.width >= button.x - 4)
          while (b.x + b.width > button.x - 7)
            b.width--;
      }
    screen.addButton(button);
  }
}
