package co.crystaldev.client.gui.screens.screen_overlay;

import co.crystaldev.client.font.FontRenderer;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.buttons.Label;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.util.objects.FadingColor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.io.File;

public class OverlayRemoveSchematic extends ScreenOverlay {
  private static final FontRenderer fr = Fonts.NUNITO_REGULAR_16;

  private final File schematic;

  public OverlayRemoveSchematic(File schematic) {
    super(0, 0, 200, 10, "Remove Schematic");
    this.schematic = schematic;
  }

  public void init() {
    String desc = String.format("Are you sure you wish to remove schematic '%s'? Removing this schematic is permanent and cannot be undone.", new Object[] { FilenameUtils.removeExtension(this.schematic.getName()) });
    int y = this.pane.y + 28;
    for (String str : WordUtils.wrap(desc, 45).split("\n")) {
      addButton((Button)new Label(this.pane.x + this.pane.width / 2, y, str, this.opts.neutralTextColor.getRGB(), fr));
      y += fr.getStringHeight();
    }
    y += 2;
    addButton(new MenuButton(-1, this.pane.x + 5, y, this.pane.width / 2 - 7, 18, "Cancel"), b -> b.onClick = this::closeOverlay);
    addButton(new MenuButton(-1, this.pane.x + this.pane.width / 2 + 2, y, this.pane.width / 2 - 7, 18, "Delete Schematic"), b -> {
          b.onClick = (null);
          b.setTextColor(new FadingColor(this.opts.secondaryRed, this.opts.mainRed));
        });
    while (this.pane.y + this.pane.height < y + 18 + 5)
      this.pane.height++;
    center();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\screen_overlay\OverlayRemoveSchematic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */