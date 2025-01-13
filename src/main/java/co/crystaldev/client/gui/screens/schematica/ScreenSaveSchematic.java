package co.crystaldev.client.gui.screens.schematica;

import co.crystaldev.client.Resources;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.buttons.*;
import co.crystaldev.client.gui.buttons.settings.ToggleButton;
import co.crystaldev.client.util.enums.SchematicaGuiType;
import com.github.lunatrius.core.util.MBlockPos;
import com.github.lunatrius.schematica.Schematica;
import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.github.lunatrius.schematica.world.schematic.SchematicFormat;

import java.io.File;

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
        addButton((rx = new NumberInputField(-1, x, y, w, h, red.x, -30000000, 30000000, true)),
                b -> b.setOnTextInput(a -> {
                    ClientProxy.pointA.x = rx.getValue();
                    ClientProxy.updatePoints();
                }));
        y += h + 5;
        addButton(new Label(tx, y + Fonts.NUNITO_REGULAR_20.getStringHeight() / 2, "Y: ", -1));
        NumberInputField ry;
        addButton((ry = new NumberInputField(-1, x, y, w, h, red.y, -30000000, 30000000, true)),
                b -> b.setOnTextInput(a -> {
                    ClientProxy.pointA.y = ry.getValue();
                    ClientProxy.updatePoints();
                }));
        y += h + 5;
        addButton(new Label(tx, y + Fonts.NUNITO_REGULAR_20.getStringHeight() / 2, "Z: ", -1));
        NumberInputField rz;
        addButton((rz = new NumberInputField(-1, x, y, w, h, red.z, -30000000, 30000000, true)),
                b -> b.setOnTextInput(a -> {
                    ClientProxy.pointA.z = rz.getValue();
                    ClientProxy.updatePoints();
                }));
        y += h + 5;
        MenuButton redSelector;
        addButton((redSelector = new MenuButton(-1, x, y, w, h, "Select Red Point")), b -> b.setOnClick(() -> {
            if (ClientProxy.isRenderingGuide) {
                ClientProxy.movePointToPlayer(ClientProxy.pointA);
                ClientProxy.updatePoints();
                rx.setValue(ClientProxy.pointA.x);
                ry.setValue(ClientProxy.pointA.y);
                rz.setValue(ClientProxy.pointA.z);

            }
        }));
        x = this.content.x + this.content.width / 2 + 5 + textWidth + 3;
        tx = x - textWidth / 2 - 3;
        y = defY;
        addButton(new Label(x - textWidth + (w + textWidth) / 2, y - 3 - Fonts.NUNITO_REGULAR_20.getStringHeight() / 2, "Blue Point", 5137148));
        addButton(new Label(tx, y + Fonts.NUNITO_REGULAR_20.getStringHeight() / 2, "X: ", -1));
        NumberInputField bx;
        addButton((bx = new NumberInputField(-1, x, y, w, h, blue.x, -30000000, 30000000, true)),
                b -> b.setOnTextInput(a -> {
                    ClientProxy.pointB.x = bx.getValue();
                    ClientProxy.updatePoints();
                }));
        y += h + 5;
        addButton(new Label(tx, y + Fonts.NUNITO_REGULAR_20.getStringHeight() / 2, "Y: ", -1));
        NumberInputField by;
        addButton((by = new NumberInputField(-1, x, y, w, h, blue.y, -30000000, 30000000, true)),
                b -> b.setOnTextInput(a -> {
                    ClientProxy.pointB.y = by.getValue();
                    ClientProxy.updatePoints();
                }));
        y += h + 5;
        addButton(new Label(tx, y + Fonts.NUNITO_REGULAR_20.getStringHeight() / 2, "Z: ", -1));
        NumberInputField bz;
        addButton((bz = new NumberInputField(-1, x, y, w, h, blue.z, -30000000, 30000000, true)),
                b -> b.setOnTextInput(a -> {
                    ClientProxy.pointB.z = bz.getValue();
                    ClientProxy.updatePoints();
                }));
        y += h + 5;
        MenuButton blueSelector;
        addButton((blueSelector = new MenuButton(-1, x, y, w, h, "Select Blue Point")), b -> b.setOnClick(() -> {
            if (ClientProxy.isRenderingGuide) {
                ClientProxy.movePointToPlayer(ClientProxy.pointB);
                ClientProxy.updatePoints();
                bx.setValue(ClientProxy.pointB.x);
                by.setValue(ClientProxy.pointB.y);
                bz.setValue(ClientProxy.pointB.z);
            }
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
            if (!nameInput.getText().equals("")) {
                String path = nameInput.getText() + ".schematic";
                if (ClientProxy.isRenderingGuide) {
                    if (Schematica.proxy.saveSchematic(this.mc.thePlayer, ConfigurationHandler.schematicDirectory, path, this.mc.theWorld, ClientProxy.pointMin, ClientProxy.pointMax)) {
                        nameInput.setText("");
                    }
                } else {
                    SchematicFormat.writeToFileAndNotify(new File(ConfigurationHandler.schematicDirectory, path), ClientProxy.currentSchematic.schematic.getSchematic(), this.mc.thePlayer);
                }
            }

        }));
        addButton(new ToggleButton(-1, x, y - h - 5, w, h, "Show Guide", ClientProxy.isRenderingGuide), b -> {
            b.setOnStateChange(a -> {
                ClientProxy.isRenderingGuide = b.getCurrentValue();
                blueSelector.setEnabled(ClientProxy.isRenderingGuide);
                redSelector.setEnabled(ClientProxy.isRenderingGuide);
                rx.setEnabled(ClientProxy.isRenderingGuide);
                ry.setEnabled(ClientProxy.isRenderingGuide);
                rz.setEnabled(ClientProxy.isRenderingGuide);
                bx.setEnabled(ClientProxy.isRenderingGuide);
                by.setEnabled(ClientProxy.isRenderingGuide);
                bz.setEnabled(ClientProxy.isRenderingGuide);
                saveButton.setEnabled(ClientProxy.isRenderingGuide);
                nameInput.setEnabled(ClientProxy.isRenderingGuide);
            });
            b.getOnStateChange().accept(b.getCurrentValue());
        });
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\schematica\ScreenSaveSchematic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */