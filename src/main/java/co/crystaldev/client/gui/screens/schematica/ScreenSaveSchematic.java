package co.crystaldev.client.gui.screens.schematica;

import co.crystaldev.client.Resources;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.buttons.*;
import co.crystaldev.client.gui.buttons.settings.ToggleButton;
import co.crystaldev.client.util.enums.SchematicaGuiType;
import com.github.lunatrius.core.util.MBlockPos;
import com.github.lunatrius.schematica.proxy.ClientProxy;

public class ScreenSaveSchematic extends ScreenSchematicaBase {
  public ScreenSaveSchematic() {
    super(SchematicaGuiType.SAVE_SCHEMATIC);
  }

  public void init() {
    super.init();
    int textWidth = Fonts.NUNITO_REGULAR_20.getMaxWidth("X: ", "Y: ", "Z: ");
    int w = 90;
    int h = 18;
    int defY = this.content.y + this.content.height / 2 - ((h + 5) * 4 - 5) / 2;
    int x = this.content.x + this.content.width / 2 - 5 - w;
    int tx = x - textWidth / 2 - 3;
    int y = defY;
    MBlockPos red = ClientProxy.pointA;
    MBlockPos blue = ClientProxy.pointB;
    addButton(new Label(x - textWidth + (w + textWidth) / 2, y - 3 - Fonts.NUNITO_REGULAR_20.getStringHeight() / 2, "Red Point", 16334912));
    addButton(new Label(tx, y + Fonts.NUNITO_REGULAR_20.getStringHeight() / 2, "X: ", -1));
    NumberInputField rx;
    //addButton((Button)(rx = new NumberInputField(-1, x, y, w, h, red.x, -30000000, 30000000, true)), b -> b.setOnTextInput(()));
    addButton((rx = new NumberInputField(-1, x, y, w, h, red.x, -30000000, 30000000, true)), b -> b.setOnTextInput(a->{}));

    y += h + 5;
    addButton(new Label(tx, y + Fonts.NUNITO_REGULAR_20.getStringHeight() / 2, "Y: ", -1));
    NumberInputField ry;
    addButton((ry = new NumberInputField(-1, x, y, w, h, red.y, -30000000, 30000000, true)), b -> b.setOnTextInput(a->{}));
    y += h + 5;
    addButton(new Label(tx, y + Fonts.NUNITO_REGULAR_20.getStringHeight() / 2, "Z: ", -1));
    NumberInputField rz;
    addButton((rz = new NumberInputField(-1, x, y, w, h, red.z, -30000000, 30000000, true)), b -> b.setOnTextInput(a->{}));
    y += h + 5;
    MenuButton redSelector;
    addButton((redSelector = new MenuButton(-1, x, y, w, h, "Select Red Point")), b -> b.setOnClick(null));
    x = this.content.x + this.content.width / 2 + 5 + textWidth + 3;
    tx = x - textWidth / 2 - 3;
    y = defY;
    addButton(new Label(x - textWidth + (w + textWidth) / 2, y - 3 - Fonts.NUNITO_REGULAR_20.getStringHeight() / 2, "Blue Point", 5137148));
    addButton(new Label(tx, y + Fonts.NUNITO_REGULAR_20.getStringHeight() / 2, "X: ", -1));
    NumberInputField bx;
    addButton((bx = new NumberInputField(-1, x, y, w, h, blue.x, -30000000, 30000000, true)), b -> b.setOnTextInput(a->{}));
    y += h + 5;
    addButton(new Label(tx, y + Fonts.NUNITO_REGULAR_20.getStringHeight() / 2, "Y: ", -1));
    NumberInputField by;
    addButton((by = new NumberInputField(-1, x, y, w, h, blue.y, -30000000, 30000000, true)), b -> b.setOnTextInput(a->{}));
    y += h + 5;
    addButton(new Label(tx, y + Fonts.NUNITO_REGULAR_20.getStringHeight() / 2, "Z: ", -1));
    NumberInputField bz;
    addButton((bz = new NumberInputField(-1, x, y, w, h, blue.z, -30000000, 30000000, true)), b -> b.setOnTextInput(a->{}));
    y += h + 5;
    MenuButton blueSelector;
    addButton((blueSelector = new MenuButton(-1, x, y, w, h, "Select Blue Point")), b -> b.setOnClick(() -> {

    }));
    w = 120;
    x = this.content.x + this.content.width - 10 - w;
    y = this.content.y + this.content.height - 10 - h;
    TextInputField nameInput;
    addButton((nameInput = new TextInputField(-1, x, y, w - 5 - h, h, "Schematic Name")), b -> {
          b.setMaxLength(255);
          b.fontRenderer = Fonts.NUNITO_REGULAR_20;
        });
    ResourceButton saveButton;
    addButton((saveButton = new ResourceButton(-1, x + w - h, y, h, h, Resources.CHECK)), b -> b.setOnClick(() -> {

    }));
    addButton(new ToggleButton(-1, x, y - h - 5, w, h, "Show Guide", ClientProxy.isRenderingGuide), b -> {
          b.setOnStateChange(a->{});
          b.getOnStateChange().accept(b.getCurrentValue());
        });
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\schematica\ScreenSaveSchematic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */