package co.crystaldev.client.gui.buttons.settings;

import co.crystaldev.client.Resources;
import co.crystaldev.client.feature.base.Dropdown;
import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.ScrollPane;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.gui.screens.ScreenSettings;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.ChatColor;
import co.crystaldev.client.util.objects.FadingColor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class DropdownButton<T> extends SettingButton<Dropdown<T>> {
  private final List<Button> elements = new ArrayList<>();

  private final LinkedList<T> selected;

  private final String placeholderText;

  private final FadingColor fadingColor;

  private final FadingColor textColor;

  public FadingColor getFadingColor() {
    return this.fadingColor;
  }

  public FadingColor getTextColor() {
    return this.textColor;
  }

  private String displayStringFormat = "%d items selected";

  private String displayStringOverride = null;

  public void setDisplayStringFormat(String displayStringFormat) {
    this.displayStringFormat = displayStringFormat;
  }

  public void setDisplayStringOverride(String displayStringOverride) {
    this.displayStringOverride = displayStringOverride;
  }

  private boolean expanded = false;

  private ScrollPane pane;

  private Pane scaledPane;

  public boolean isExpanded() {
    return this.expanded;
  }

  private boolean updated = false;

  private BiPredicate<Dropdown<T>, T> onSelect = null;

  public BiPredicate<Dropdown<T>, T> getOnSelect() {
    return this.onSelect;
  }

  public void setOnSelect(BiPredicate<Dropdown<T>, T> onSelect) {
    this.onSelect = onSelect;
  }

  public DropdownButton(int id, int x, int y, int width, int height, String placeholderText, Dropdown<T> dropdown) {
    super(id, x, y, width, height, "", dropdown);
    this.fadingColor = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
    this.textColor = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
    this.placeholderText = placeholderText;
    this.renderLast = true;
    this.selected = dropdown.getCurrentlySelected();
    T[] values = (T[])dropdown.getValues();
    int offsetY = 0, index = 0;
    for (T element : values) {
      this.elements.add(new DropdownElement(this.x + 2, this.y + this.height + 2 + offsetY, this.width - 4, this.height, element, (index == 0), (index == values.length - 1)));
      offsetY += this.height;
      index++;
    }
    setDisplayText();
    setScrollPane();
    this.pane.updateMaxScroll(this.elements, null, 0);
  }

  public DropdownButton(int id, int x, int y, int width, int height, Dropdown<T> dropdown) {
    this(id, x, y, width, height, "No item selected", dropdown);
  }

  public void onUpdate() {
    int offsetY = 0;
    for (Button button : this.elements) {
      button.y = button.initialY = this.y + this.height + 2 + offsetY;
      offsetY += this.height;
    }
    setScrollPane();
  }

  public boolean onScroll(ScrollPane pane, int mouseX, int mouseY, int dwheel) {
    if (this.expanded) {
      float scale = getScaledScreen();
      mouseX = (int)(mouseX * scale);
      mouseY = (int)(mouseY * scale);
      if (!isHovered(mouseX, mouseY)) {
        this.expanded = false;
        return false;
      }
      int scrollDuration = (ClientOptions.getInstance()).scrollDuration;
      (ClientOptions.getInstance()).scrollDuration = 50;
      this.pane.scroll(this.elements, mouseX, mouseY, dwheel, scale);
      (ClientOptions.getInstance()).scrollDuration = scrollDuration;
      return true;
    }
    return false;
  }

  public void drawButton(int mouseX, int mouseY, boolean hovered) {
    if (!this.expanded)
      Screen.scissorStart(this.scissorPane);
    hovered = (hovered || this.expanded);
    this.fadingColor.fade(hovered);
    this.textColor.fade(hovered);
    if (this.expanded) {
      RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.pane.y + this.pane.height), 9.0D, this.fadingColor
          .getCurrentColor().getRGB());
    } else {
      RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, this.fadingColor
          .getCurrentColor().getRGB());
    }
    this.fontRenderer.drawString(ChatColor.translate(this.displayText), this.x + 4, this.y + this.height / 2 - this.fontRenderer
        .getStringHeight() / 2, this.textColor
        .getCurrentColor().getRGB());
    RenderUtils.setGlColor(this.textColor.getCurrentColor());
    RenderUtils.drawCustomSizedResource(this.expanded ? Resources.CHEVRON_UP : Resources.CHEVRON_DOWN, this.x + this.width - 12, this.y + this.height / 2 - 5, 10, 10);
    RenderUtils.resetColor();
    if (!this.expanded)
      Screen.scissorEnd(this.scissorPane);
    if (this.expanded) {
      boolean wasHovered = false;
      for (Button button : this.elements)
        button.drawButton(mouseX, mouseY, (!wasHovered && (wasHovered = button.isHovered(mouseX, mouseY))));
    }
  }

  public void onInteract(int mouseX, int mouseY, int mouseButton) {
    LinkedList<T> selected = new LinkedList<>(this.selected);
    if (super.isHovered(mouseX, mouseY)) {
      this.expanded = !this.expanded;
    } else if (isHovered(mouseX, mouseY)) {
      for (Button button : this.elements) {
        DropdownElement element = (DropdownElement)button;
        if (element.isHovered(mouseX, mouseY))
          selectElement(element);
      }
    } else {
      this.expanded = false;
    }
    if (!selected.equals(this.selected)) {
      this.updated = true;
      if (this.mc.currentScreen instanceof ScreenSettings)
        ((ScreenSettings)this.mc.currentScreen).getModule().onUpdate();
    }
  }

  public void mouseDown(Screen screen, int mouseX, int mouseY, int mouseButton) {
    boolean hovered = isHovered(mouseX, mouseY);
    if (this.expanded && !hovered)
      this.expanded = false;
  }

  public boolean isHovered(int mouseX, int mouseY) {
    if (this.expanded)
      return (mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height + this.pane.height);
    return super.isHovered(mouseX, mouseY);
  }

  public boolean wasUpdated() {
    if (this.updated) {
      this.updated = false;
      return true;
    }
    return false;
  }

  private void setScrollPane() {
    int height = Math.min(this.height * this.elements.size() + 4, this.height * 5 + 2);
    ScrollPane sp = new ScrollPane(this.x, this.y + this.height, this.width, height);
    if (this.pane != null) {
      this.pane.x = sp.x;
      this.pane.y = sp.y;
    } else {
      this.pane = sp;
    }
    this.scaledPane = this.pane.scale(getScaledScreen());
  }

  protected void setDisplayText() {
    String displayText;
    this.displayText = "";
    if (this.displayStringOverride != null) {
      displayText = this.displayStringOverride;
    } else if (this.selected.isEmpty()) {
      displayText = this.placeholderText;
    } else if (this.selected.size() > 1) {
      String fromFmt = String.format(this.displayStringFormat, this.selected.size());
      String joined = this.selected.stream().map(Object::toString).collect(Collectors.joining(", "));
      displayText = (this.fontRenderer.getStringWidth(joined) > this.width - 18) ? fromFmt : joined;
    } else {
      displayText = this.selected.get(0).toString();
    }
    while (this.fontRenderer.getStringWidth(this.displayText + "...") < this.width - 18 &&
      this.displayText.length() != displayText.length())
      this.displayText += displayText.charAt(this.displayText.length());
    if (!this.displayText.equals(displayText))
      this.displayText += "...";
  }

  @SafeVarargs
  public final void selectAll(T... values) {
    for (T value : values) {
      for (Button button : this.elements) {
        DropdownElement element = (DropdownElement)button;
        if (element.getElement() == value)
          selectElement(element);
      }
    }
  }

  private void selectElement(DropdownElement element) {
    Dropdown<T> value = getCurrentValue();
    if (this.onSelect != null && !this.onSelect.test(value, element.getElement()))
      return;
    if (value.select(element.getElement()) && !value.isMultiSelect())
      this.expanded = false;
    setDisplayText();
  }

  private class DropdownElement extends MenuButton {
    private final FadingColor stateColor;

    private final FadingColor glyphColor;

    private final T element;

    private final boolean top;

    private final boolean bottom;

    public T getElement() {
      return this.element;
    }

    public DropdownElement(int x, int y, int width, int height, T element, boolean top, boolean bottom) {
      super(-1, x, y, width, height, "");
      this.stateColor = new FadingColor(this.opts.neutralButtonBackground, this.opts.getColor(this.opts.mainColor, 180));
      this.glyphColor = new FadingColor(this.opts.getColor(this.opts.hoveredTextColor, 0), this.opts.getColor(this.opts.hoveredTextColor, 140));
      this.fontRenderer = Fonts.NUNITO_REGULAR_16;
      this.element = element;
      this.top = top;
      this.bottom = bottom;
      String elem = element.toString();
      while (this.fontRenderer.getStringWidth(this.displayText + "...") < this.width - this.height + 6 &&
        this.displayText.length() != elem.length())
        this.displayText += elem.charAt(this.displayText.length());
      if (!this.displayText.equals(elem))
        this.displayText += "...";
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
      Screen.scissorStart(DropdownButton.this.scaledPane);
      hovered = (this.enabled && hovered);
      this.selected = DropdownButton.this.selected.contains(getElement());
      this.fadingColor.fade(hovered);
      this.textColor.fade((hovered || this.selected));
      this.stateColor.fade(this.selected);
      this.glyphColor.fade(this.selected);
      RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, this.fadingColor
          .getCurrentColor().getRGB(), this.top, this.bottom, this.top, this.bottom);
      this.fontRenderer.drawString(ChatColor.translate(this.displayText), this.x + 4, this.y + this.height / 2 - this.fontRenderer
          .getStringHeight() / 2, this.textColor
          .getCurrentColor().getRGB());
      int boxSize = this.height - 6;
      RenderUtils.drawRoundedRect((this.x + this.width - 3 - boxSize), (this.y + 3), (this.x + this.width - 3), (this.y + this.height - 3), 6.0D, this.stateColor
          .getCurrentColor().getRGB());
      RenderUtils.setGlColor(this.glyphColor.getCurrentColor());
      RenderUtils.drawCustomSizedResource(Resources.CHECK, this.x + this.width - 1 - boxSize, this.y + 5, boxSize - 4, boxSize - 4);
      RenderUtils.resetColor();
      Screen.scissorEnd(DropdownButton.this.scaledPane);
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\settings\DropdownButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */