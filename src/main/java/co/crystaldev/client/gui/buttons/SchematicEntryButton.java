package co.crystaldev.client.gui.buttons;

import co.crystaldev.client.Resources;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;

import java.io.File;

public class SchematicEntryButton extends Button {
  private final File file;

  private final boolean directory;

  private final FadingColor background;

  private final FadingColor outline;

  private final FadingColor outline1;

  private final FadingColor text;

  private final ResourceButton delete;

  private final ResourceButton load;

  public File getFile() {
    return this.file;
  }

  public boolean isDirectory() {
    return this.directory;
  }

  private boolean selected = false;

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public SchematicEntryButton(File file, int x, int y, int width, int height, String name, boolean directory) {
    super(-1, x, y, width, height, name);
    this.file = file;
    this.directory = directory;
    this.background = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
    this.outline = new FadingColor(this.opts.mainDisabled, this.opts.mainColor);
    this.outline1 = new FadingColor(this.opts.secondaryDisabled, this.opts.secondaryColor);
    this.text = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
    int bSize = 16;
    int bX = this.x + this.width - bSize - 5;
    int bY = this.y + this.height / 2 - bSize / 2;
    this.load = new ResourceButton(-1, bX, bY, bSize, bSize, Resources.CHECK);
    bX -= bSize + 5;
    this.delete = new ResourceButton(-1, bX, bY, bSize, bSize, Resources.CLOSE);
    this.delete.setIconColor(new FadingColor(this.opts.neutralTextColor, this.opts.secondaryRed));
    this.fontRenderer = Fonts.NUNITO_SEMI_BOLD_18;
    if (!this.directory) {
      int size = this.displayText.length();
      while (this.x + 5 + this.fontRenderer.getStringWidth(this.displayText + "...") > this.delete.x - 5)
        this.displayText = this.displayText.substring(0, this.displayText.length() - 1);
      if (this.displayText.length() != size)
        this.displayText += "...";
    }
  }

  public void onUpdate() {
    int bSize = 16;
    this.delete.y = this.y + this.height / 2 - bSize / 2;
  }

  public void drawButton(int mouseX, int mouseY, boolean hovered) {
    Screen.scissorStart(this.scissorPane);
    this.background.fade((hovered || this.selected));
    this.outline.fade(this.selected);
    this.outline1.fade(this.selected);
    this.text.fade((hovered || this.selected));
    String display = this.displayText.equals("..") ? "< Back" : this.displayText;
    if (!this.directory) {
      RenderUtils.drawRoundedRectWithGradientBorder(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, 1.5F, this.outline
          .getCurrentColor().getRGB(), this.outline1.getCurrentColor().getRGB(), this.background
          .getCurrentColor().getRGB());
      this.delete.drawButton(mouseX, mouseY, this.delete.isHovered(mouseX, mouseY));
      this.load.drawButton(mouseX, mouseY, this.load.isHovered(mouseX, mouseY));
      this.fontRenderer.drawString(display, this.x + 5, this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.text
          .getCurrentColor().getRGB());
    } else {
      RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, this.background
          .getCurrentColor().getRGB());
      int size = (int)(this.height * 0.8F);
      RenderUtils.setGlColor(this.text.getCurrentColor());
      RenderUtils.drawCustomSizedResource(Resources.FILE_FOLDER, this.x + 5, this.y + this.height / 2 - size / 2, size, size);
      RenderUtils.resetColor();
      this.fontRenderer.drawString(display, this.x + 10 + size, this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.text
          .getCurrentColor().getRGB());
    }
    RenderUtils.resetColor();
    Screen.scissorEnd(this.scissorPane);
  }

  public boolean isDeleteHovered(int mouseX, int mouseY) {
    return this.delete.isHovered(mouseX, mouseY);
  }

  public boolean isLoadHovered(int mouseX, int mouseY) {
    return this.load.isHovered(mouseX, mouseY);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\SchematicEntryButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */