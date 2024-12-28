package co.crystaldev.client.gui.buttons;

import co.crystaldev.client.Reference;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.ease.Animation;
import co.crystaldev.client.gui.ease.Easing;
import co.crystaldev.client.gui.ease.IEasingFunction;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NavigationButton<V extends Enum<?>> extends Button {
  private static final IEasingFunction ANIMATION_FUNCTION = Easing.IN_OUT_QUART;

  private static final long ANIMATION_DURATION = 250L;

  private static List<NavigationButton<?>> PERSISTENT;

  private final List<NavigationOptionButton> buttons;

  private V selected;

  private boolean updated = false;

  private Animation positionAnimation;

  private Animation widthAnimation;

  public NavigationButton(V selected, int x, int y) {
    this(selected, x, y, true);
  }

  public NavigationButton(V selected, int x, int y, boolean persistent) {
    super(-1, x, y - 2, 0, 0);
    this.fontRenderer = Fonts.NUNITO_SEMI_BOLD_20;
    this.height = this.fontRenderer.getStringHeight() + 4;
    this.y -= Fonts.NUNITO_SEMI_BOLD_24.getStringHeight() / 2;
    this.buttons = new ArrayList<>();
    this.selected = selected;
    if (persistent)
      if (PERSISTENT == null) {
        PERSISTENT = new ArrayList<>();
        PERSISTENT.add(this);
      } else {
        boolean found = false;
        for (NavigationButton<?> button : PERSISTENT) {
          if (button.selected.getClass().equals(this.selected.getClass())) {
            NavigationButton<V> nav = (NavigationButton)button;
            this.selected = nav.selected;
            PERSISTENT.remove(button);
            PERSISTENT.add(this);
            found = true;
            break;
          }
        }
        if (!found) {
          PERSISTENT = new ArrayList<>();
          PERSISTENT.add(this);
        }
      }
    V[] values = getValues();
    if (values != null) {
      int bx = this.x;
      int by = this.y;
      int index = 0;
      for (V value : values) {
        if (value.toString() != null) {
          NavigationOptionButton nav = new NavigationOptionButton(index, bx, by, value);
          this.buttons.add(nav);
          bx += this.fontRenderer.getStringWidth(value.toString()) + 12;
          if (value == this.selected) {
            this.widthAnimation = new Animation(1L, nav.width, nav.width);
            this.positionAnimation = new Animation(1L, nav.x, nav.x);
          }
          index++;
        }
      }
      this.width = bx - this.x;
      this.x -= this.width / 2;
      this.positionAnimation = new Animation(1L, this.positionAnimation.getValue() - this.width / 2.0F, this.positionAnimation.getValue() - this.width / 2.0F);
      this.buttons.forEach(b -> b.x -= this.width / 2);
    }
  }

  public void drawButton(int mouseX, int mouseY, boolean hovered) {
    for (NavigationOptionButton button : this.buttons)
      button.drawButton(mouseX, mouseY, button.isHovered(mouseX, mouseY));
    float currentX = 5;
    float currentWidth = 5;
    if(this.positionAnimation != null) {
       currentX = this.positionAnimation.getValue();
    }

    if(this.widthAnimation != null) {
       currentWidth = this.widthAnimation.getValue();

    }
    int y = this.y + this.height;
    RenderUtils.glColor(this.opts.mainColor.getRGB());
    RenderUtils.drawLine(2.5F, currentX, y, currentX + currentWidth, y);
    RenderUtils.resetColor();
  }

  public void onInteract(int mouseX, int mouseY, int mouseButton) {
    for (NavigationOptionButton button : this.buttons) {
      if (button.isHovered(mouseX, mouseY) &&
        this.selected != button.value) {
        this.selected = button.value;
        this.updated = true;
        this.positionAnimation = new Animation(250L, this.positionAnimation.getValue(), button.x, ANIMATION_FUNCTION);
        this.widthAnimation = new Animation(250L, this.widthAnimation.getValue(), button.width, ANIMATION_FUNCTION);
      }
    }
  }

  public boolean onKeyTyped(char key, int keycode) {
    if (keycode == 15) {
      int index = 0;
      for (NavigationOptionButton button : this.buttons) {
        if (button.value == this.selected) {
          index = button.id + 1;
          break;
        }
      }
      NavigationOptionButton nav = this.buttons.get((index >= this.buttons.size()) ? 0 : index);
      this.selected = nav.value;
      this.updated = true;
      this.positionAnimation = new Animation(250L, this.positionAnimation.getValue(), nav.x, ANIMATION_FUNCTION);
      this.widthAnimation = new Animation(250L, this.widthAnimation.getValue(), nav.width, ANIMATION_FUNCTION);
    }
    return true;
  }

  public void setType(V type) {
    for (NavigationOptionButton button : this.buttons) {
      if (button.value == type &&
        this.selected != button.value) {
        this.selected = button.value;
        this.updated = true;
        this.positionAnimation = new Animation(250L, this.positionAnimation.getValue(), button.x, ANIMATION_FUNCTION);
        this.widthAnimation = new Animation(250L, this.widthAnimation.getValue(), button.width, ANIMATION_FUNCTION);
        break;
      }
    }
  }

  public void forceSetType(V type) {
    for (NavigationOptionButton button : this.buttons) {
      if (button.value == type &&
        this.selected != button.value) {
        this.selected = button.value;
        this.updated = true;
        this.positionAnimation = new Animation(250L, this.positionAnimation.getValue(), button.x, ANIMATION_FUNCTION);
        this.widthAnimation = new Animation(250L, this.widthAnimation.getValue(), button.width, ANIMATION_FUNCTION);
        break;
      }
    }
  }

  public NavigationButton<V> copy(int x, int y) {
    if (this.selected == null)
      return null;
    return copy(this.selected, x, y);
  }

  public NavigationButton<V> copy(V selected, int x, int y) {
    if (selected == null)
      return copy(x, y);
    NavigationButton<V> navigationButton = new NavigationButton(selected, x, y, false);
    navigationButton.positionAnimation = this.positionAnimation.clone();
    navigationButton.widthAnimation = this.widthAnimation.clone();
    return navigationButton;
  }

  private V[] getValues() {
    V[] values;
    try {
      values = (V[]) this.selected.getClass().getMethod("values", new Class[0]).invoke(null, new Object[0]);
    } catch (Exception ex) {
      values = null;
      Reference.LOGGER.error("Unable to invoke values method of enum");
    }
    return values;
  }

  public boolean wasUpdated() {
    if (this.updated) {
      this.updated = false;
      return true;
    }
    return false;
  }

  public V getCurrent() {
    return this.selected;
  }

  private class NavigationOptionButton extends Button {
    private final V value;

    private final FadingColor fadingColor;

    public NavigationOptionButton(int index, int x, int y, V value) {
      super(index, x - 2, y - 2, NavigationButton.this.fontRenderer.getStringWidth(value.toString() + '\004'), NavigationButton.this.height, value
          .toString());
      this.value = value;
      this.fadingColor = new FadingColor(this.opts.unselectedTextColor, this.opts.hoveredTextColor);
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
      this.fadingColor.fade((hovered || NavigationButton.this.selected == this.value));
      NavigationButton.this.fontRenderer.drawCenteredString(this.displayText, this.x + this.width / 2, this.y + this.height / 2, this.fadingColor
          .getCurrentColor().getRGB());
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\NavigationButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */