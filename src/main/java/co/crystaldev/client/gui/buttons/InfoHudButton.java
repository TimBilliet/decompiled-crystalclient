package co.crystaldev.client.gui.buttons;

import co.crystaldev.client.feature.base.HudModuleText;
import co.crystaldev.client.feature.impl.hud.InfoHud;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;
import co.crystaldev.client.util.type.Tuple;

import java.util.ArrayList;
import java.util.List;

public class InfoHudButton extends Button {
  private final List<ElementSelectionButton> subButtons = new ArrayList<>();

  private int draggingId = -1, mouseOverId = -1;

  public InfoHudButton(int x, int y, int width) {
    super(-1, x, y, width, Fonts.NUNITO_REGULAR_16.getStringHeight() + 2);
    this.fontRenderer = Fonts.NUNITO_REGULAR_16;
    InfoHud infoHud = InfoHud.getInstance();
    int size = infoHud.getRegisteredModules().size();
    int columns = ((size & 0xFFFFFFFE) >> 1) + ((size % 2 == 0) ? 0 : 1);
    this.height = (Fonts.NUNITO_REGULAR_16.getStringHeight() + 4) * columns;
    int bx = this.x;
    int by = this.y;
    int bw = this.width / 2 - 1;
    int bh = Fonts.NUNITO_REGULAR_16.getStringHeight() + 4;
    boolean nextColumn = false;
    for (Tuple<Integer, HudModuleText> tuple : (Iterable<Tuple<Integer, HudModuleText>>)infoHud.getRegisteredModules()) {
      if (by >= this.y + this.height) {
        bx += bw;
        by = this.y;
        nextColumn = true;
      }
      this.subButtons.add(new ElementSelectionButton(tuple, bx + 1, by + 1, bw - 2, bh - 2, this.subButtons
            .isEmpty(), (!this.subButtons.isEmpty() && by == this.y), (!nextColumn && by == this.y + this.height - bh), (nextColumn && by == this.y + this.height - bh)));
      by += bh;
    }
  }

  public void onUpdate() {
    for (ElementSelectionButton button : this.subButtons) {
      button.x = this.x + button.relativeX;
      button.y = this.y + button.relativeY;
    }
  }

  public void drawButton(int mouseX, int mouseY, boolean hovered) {
    Screen.scissorStart(this.scissorPane);
    RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, this.opts.neutralButtonBackground
        .getRGB());
    boolean wasHovered = false;
    for (ElementSelectionButton button : this.subButtons)
      button.drawButton(mouseX, mouseY, (!wasHovered && (wasHovered = button.isHovered(mouseX, mouseY))));
    if (this.draggingId != -1) {
      ElementSelectionButton dragged = this.subButtons.get(this.draggingId);
      ElementSelectionButton mouseOver = this.subButtons.stream().filter(b -> b.isHovered(mouseX, mouseY)).findFirst().orElse(null);
      if (mouseOver != null && this.mouseOverId != mouseOver.id && this.draggingId != mouseOver.id) {
        swap(dragged, mouseOver);
        this.mouseOverId = mouseOver.id;
      }
    } else {
      this.mouseOverId = -1;
    }
    Screen.scissorEnd(this.scissorPane);
    if (this.draggingId != -1) {
      ElementSelectionButton b = this.subButtons.get(this.draggingId);
      this.fontRenderer.drawCenteredString(((HudModuleText)b.tuple.getItem2()).name, mouseX, mouseY, -1);
    }
  }

  public void onInteract(int mouseX, int mouseY, int mouseButton) {
    super.onInteract(mouseX, mouseY, mouseButton);
    for (ElementSelectionButton button : this.subButtons) {
      if (button.isHovered(mouseX, mouseY)) {
        button.onInteract(mouseX, mouseY, mouseButton);
        break;
      }
    }
  }

  public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    super.mouseReleased(mouseX, mouseY, mouseButton);
    if (this.draggingId != -1 && mouseButton == 0) {
      this.draggingId = -1;
      for (ElementSelectionButton button : this.subButtons) {
        button.dragging = false;
        if (button.tempModule != null)
          button.tuple.setItem2(button.tempModule);
        button.tempModule = null;
      }
    }
  }

  private void undoSwap() {
    for (ElementSelectionButton button : this.subButtons)
      button.tempModule = null;
  }

  private void swap(ElementSelectionButton dragging, ElementSelectionButton mouseOver) {
    undoSwap();
    int direction = (dragging.id < mouseOver.id) ? -1 : 1;
    int min = Math.min(dragging.id + direction * -1, mouseOver.id);
    int max = Math.max(dragging.id + direction * -1, mouseOver.id);
    for (int i = min; i <= max; i++) {
      ElementSelectionButton b = this.subButtons.get(i);
      ElementSelectionButton b1 = this.subButtons.get(i + direction);
      b1.tempModule = (HudModuleText)b.tuple.getItem2();
    }
    (this.subButtons.get(mouseOver.id)).tempModule = (HudModuleText)(this.subButtons.get(dragging.id)).tuple.getItem2();
  }

  private class ElementSelectionButton extends Button {
    private final Tuple<Integer, HudModuleText> tuple;

    private HudModuleText tempModule = null;

    private final int relativeX;

    private final int relativeY;

    private final boolean tLeft;

    private final boolean tRight;

    private final boolean bLeft;

    private final boolean bRight;

    private final FadingColor background;

    private final FadingColor text;

    private final FadingColor status;

    private boolean dragging = false;

    public ElementSelectionButton(Tuple<Integer, HudModuleText> tuple, int x, int y, int width, int height, boolean tLeft, boolean tRight, boolean bLeft, boolean bRight) {
      super(((Integer)tuple.getItem1()).intValue(), x, y, width, height);
      this.fontRenderer = InfoHudButton.this.fontRenderer;
      this.relativeX = x - InfoHudButton.this.x;
      this.relativeY = y - InfoHudButton.this.y;
      this.tuple = tuple;
      this.tLeft = tLeft;
      this.tRight = tRight;
      this.bLeft = bLeft;
      this.bRight = bRight;
      this.background = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
      this.text = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
      this.status = new FadingColor(this.opts.mainDisabled, this.opts.mainColor);
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
      boolean enabled = ((HudModuleText)this.tuple.getItem2()).infoHudEnabled;
      this.background.fade((hovered || this.dragging));
      this.text.fade((hovered || this.dragging));
      this.status.fade(enabled);
      RenderUtils.drawRoundedRectWithBorder(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, 1.3F, this.status
          .getCurrentColor().getRGB(), this.background.getCurrentColor().getRGB(), this.tLeft, this.tRight, this.bLeft, this.bRight);
      this.fontRenderer.drawString(Integer.toString(this.id + 1), this.x + 5, this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.text
          .getCurrentColor().getRGB());
      if (!((HudModuleText)this.tuple.getItem2()).enabled)
        this.fontRenderer.drawString("(disabled)", this.x + 10 + this.fontRenderer.getStringWidth((this.id + 1) + ""), this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.opts.secondaryRed
            .getRGB());
      if (!this.dragging || this.tempModule != null) {
        HudModuleText module = (this.tempModule != null) ? this.tempModule : (HudModuleText)this.tuple.getItem2();
        this.fontRenderer.drawString(module.name, this.x + this.width - 5 - this.fontRenderer
            .getStringWidth(module.name), this.y + this.height / 2 - this.fontRenderer
            .getStringHeight() / 2, this.text
            .getCurrentColor().getRGB());
      }
    }

    public void onInteract(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == 0) {
        this.dragging = true;
        InfoHudButton.this.draggingId = this.id;
      } else if (mouseButton == 1 && !this.dragging) {
        ((HudModuleText)this.tuple.getItem2()).infoHudEnabled = !((HudModuleText)this.tuple.getItem2()).infoHudEnabled;
      }
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\InfoHudButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */