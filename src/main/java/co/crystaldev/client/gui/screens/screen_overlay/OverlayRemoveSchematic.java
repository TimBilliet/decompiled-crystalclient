package co.crystaldev.client.gui.screens.screen_overlay;

import co.crystaldev.client.font.FontRenderer;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.buttons.Label;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.gui.screens.schematica.ScreenLoadSchematic;
import co.crystaldev.client.gui.screens.schematica.ScreenSchematicaBase;
import co.crystaldev.client.util.enums.SchematicaGuiType;
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
        String desc = String.format("Are you sure you wish to remove schematic '%s'? Removing this schematic is permanent and cannot be undone.", FilenameUtils.removeExtension(this.schematic.getName()));
        int y = this.pane.y + 28;
        for (String str : WordUtils.wrap(desc, 45).split("\n")) {
            addButton(new Label(this.pane.x + this.pane.width / 2, y, str, this.opts.neutralTextColor.getRGB(), fr));
            y += fr.getStringHeight();
        }
        y += 2;
        addButton(new MenuButton(-1, this.pane.x + 5, y, this.pane.width / 2 - 7, 18, "Cancel"), b -> b.onClick = this::closeOverlay);
        addButton(new MenuButton(-1, this.pane.x + this.pane.width / 2 + 2, y, this.pane.width / 2 - 7, 18, "Delete Schematic"), b -> {
            b.onClick = (() -> {
                if(schematic.delete() && this.mc.currentScreen instanceof ScreenLoadSchematic) {
                    ScreenLoadSchematic screen = (ScreenLoadSchematic) this.mc.currentScreen;
                    screen.initSchematics();
                    screen.initSchematicInfo();
                    closeOverlay();
                }
            });
            b.setTextColor(new FadingColor(this.opts.secondaryRed, this.opts.mainRed));
        });
        while (this.pane.y + this.pane.height < y + 18 + 5)
            this.pane.height++;
        center();
    }
}