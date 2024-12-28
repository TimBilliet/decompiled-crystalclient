package co.crystaldev.client.gui.screens.groups;

import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.GroupSchematic;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.gui.buttons.groups.GroupSchematicButton;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SectionSchematics extends GroupSection {
  protected SectionSchematics(Pane pane) {
    super(pane);
  }

  public void init() {
    super.init();
    int x = this.pane.x + 20;
    int y = this.pane.y + 10;
    int w = this.pane.width - 40;
    int h = 18;
    final Pane scissor = this.pane.scale(getScaledScreen());
    addButton((Button)new MenuButton(-1, x, y, w, h, "Upload Current Schematic") {

        });
    y += h + 5;
    List<GroupSchematic> sorted = new ArrayList<>(GroupManager.getSelectedGroup().getSchematics());
    sorted.sort(Comparator.comparing(s -> s.getName().toLowerCase()));
    for (GroupSchematic schematic : sorted) {
      addButton((Button)new GroupSchematicButton(schematic, x, y, w, h) {

          });
      y += h + 5;
    }
    this.pane.updateMaxScroll(this, 0);
    this.pane.addScrollbarToScreen(this);
  }

  public void draw(int mouseX, int mouseY, float partialTicks) {}
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\groups\SectionSchematics.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */