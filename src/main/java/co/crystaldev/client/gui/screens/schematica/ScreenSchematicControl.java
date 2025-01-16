package co.crystaldev.client.gui.screens.schematica;

import co.crystaldev.client.Client;
import co.crystaldev.client.Resources;
import co.crystaldev.client.feature.impl.factions.Schematica;
import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.group.objects.Group;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.buttons.Label;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.gui.buttons.NumberInputField;
import co.crystaldev.client.gui.buttons.settings.DropdownButton;
import co.crystaldev.client.gui.buttons.settings.SelectorMenuResourceButton;
import co.crystaldev.client.gui.buttons.settings.ToggleButton;
import co.crystaldev.client.gui.screens.screen_overlay.OverlaySchematicMaterials;
import co.crystaldev.client.gui.screens.screen_overlay.OverlaySchematicUpload;
import co.crystaldev.client.handler.SchematicHandler;
import co.crystaldev.client.mixin.accessor.net.minecraft.util.MixinEnumFacing;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.ChatColor;
import co.crystaldev.client.util.enums.SchematicaGuiType;
import co.crystaldev.client.util.objects.Schematic;
import co.crystaldev.client.util.objects.Transformation;
import com.github.lunatrius.schematica.client.printer.SchematicPrinter;
import com.github.lunatrius.schematica.client.renderer.RenderSchematic;
import com.github.lunatrius.schematica.client.util.FlipHelper;
import com.github.lunatrius.schematica.client.util.RotationHelper;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import net.minecraft.util.EnumFacing;
import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class ScreenSchematicControl extends ScreenSchematicaBase {
    private static final SchematicPrinter PRINTER = SchematicPrinter.INSTANCE;

    private SchematicWorld schematic;

    private NumberInputField xInput;

    private NumberInputField yInput;

    private NumberInputField zInput;

    private NumberInputField nfLayer;
    private boolean schemLoaded;

    public ScreenSchematicControl() {
        super(SchematicaGuiType.CONTROL_SCHEMATIC);
    }

    public void init() {
        super.init();
        schemLoaded = ((this.schematic = ClientProxy.currentSchematic.schematic) != null);
        int lw = 120;
        int lh = 18;
        int lx = this.content.x + 10;
        int ly = this.content.y + 5;
        addButton(new DropdownButton(-1, lx, ly, lw, lh, this.loadedSchematic));
        int histHeight = (lh + 2) * 5 - 2;
        ly = this.content.y + this.content.height / 2 - histHeight / 2;
        addButton(new Label(lx + lw / 2, ly - lh + 2 + lh / 2, "Schematic History", 16777215));
        int buttonIndex = 0;
        List<Schematic> history = new ArrayList<>(ClientOptions.getInstance().getSchematicHistory());
        int i;
        for (i = history.size() - 1; i >= 0; i--) {
            Schematic schematic = history.get(i);
            addButton(new SchematicHistoryButton(schematic, lx, ly, lw, lh, (buttonIndex == 0), (buttonIndex == 0), (buttonIndex == 4), (buttonIndex == 4)));
            ly += lh + 2;
            buttonIndex++;
        }
        for (; ++i < 5 - history.size(); i++) {
            addButton(new SchematicHistoryButton(null, lx, ly, lw, lh, (buttonIndex == 0), (buttonIndex == 0), (buttonIndex == 4), (buttonIndex == 4)));
            ly += lh + 2;
            buttonIndex++;
        }
        ly = this.pane.y + this.pane.height - 10 - lh;
        addButton(new ToggleButton(-1, lx, ly, lw, lh, "Printer", PRINTER.isPrinting()), b -> {
            b.setOnStateChange(a -> {
                if (schematic != null) {
                    boolean printing = SchematicPrinter.INSTANCE.togglePrinting();
                    Client.sendMessage(Schematica.getInstance().getToggleMessage("Schematic Printer", printing), true);
                }
            });
            b.setEnabled(schemLoaded);
        });
        ly -= lh + 5;
        addButton(new MenuButton(-1, lx, ly, lw, lh, "Materials"), b -> {
            b.setOnClick(() -> {
                this.addOverlay(new OverlaySchematicMaterials(schematic));
            });
            b.setEnabled(schemLoaded);
        });
        int rw = 120;
        int rh = 18;
        int rx = this.content.x + this.content.width - rw - 10;
        int ry = this.content.y + 5;
        addButton(new MenuButton(-1, rx, ry, rw, rh, "Share Schematic"), b -> {
            b.setEnabled(schemLoaded);
            b.setOnClick(() -> SchematicHandler.getInstance().shareCurrentSchematic());
        });
        ry += rh + 5;
        addButton(new MenuButton(-1, rx, ry, rw, rh, "Save to Group"), b -> {
            Group group = GroupManager.getSelectedGroup();
            b.setEnabled((schemLoaded && group != null && group.hasPermission(9)));
            b.setOnClick(() -> {
                this.addOverlay(new OverlaySchematicUpload());
            });
        });
        int ry1 = ry + rh;
        String[] validAxes = Stream.of(MixinEnumFacing.getValues()).map(EnumFacing::getName).toArray(String[]::new);
        ry = this.pane.y + this.pane.height - 10 - rh;
        addButton(new SelectorMenuResourceButton(-1, rx - 60, ry, rw + 60, rh, "Rotate", ClientProxy.currentSchematic.axisRotation
                .getName(), validAxes, Resources.ROTATE, 12), b -> {
            b.setEnabled(schemLoaded);
            b.setOnClick(() -> {
                if (RotationHelper.INSTANCE.rotate(schematic, ClientProxy.currentSchematic.axisRotation, isShiftKeyDown())) {
                    xInput.setValue(schematic.position.x);
                    yInput.setValue(schematic.position.y);
                    zInput.setValue(schematic.position.z);
                    RenderSchematic.INSTANCE.refresh();
                    SchematicPrinter.INSTANCE.refresh();
                    ClientProxy.currentSchematic.transformations.add(new Transformation(Transformation.Type.ROTATION, ClientProxy.currentSchematic.axisRotation, schematic.position.x, schematic.position.y, schematic.position.z));
                    ClientProxy.currentSchematic.replaceHistory.clear();
                }
            });
            b.setOnStateChange(a -> ClientProxy.currentSchematic.axisRotation = EnumFacing.valueOf(b.getCurrentValue().toUpperCase(Locale.ROOT)));
            b.setEntireButtonHitBox(true);
        });
        ry -= rh + 5;
        addButton(new SelectorMenuResourceButton(-1, rx - 60, ry, rw + 60, rh, "Flip", ClientProxy.currentSchematic.axisFlip
                .getName(), validAxes, Resources.FLIP, 12), b -> {
            b.setEnabled(schemLoaded);
            b.setOnClick(() -> {
                if (FlipHelper.INSTANCE.flip(this.schematic, ClientProxy.currentSchematic.axisFlip, isShiftKeyDown())) {
                    RenderSchematic.INSTANCE.refresh();
                    SchematicPrinter.INSTANCE.refresh();
                    ClientProxy.currentSchematic.transformations.add(new Transformation(Transformation.Type.FLIP, ClientProxy.currentSchematic.axisFlip, schematic.position.x, schematic.position.y, schematic.position.z));
                    ClientProxy.currentSchematic.replaceHistory.clear();
                }
            });
            b.setOnStateChange(a -> ClientProxy.currentSchematic.axisFlip = EnumFacing.valueOf(b.getCurrentValue().toUpperCase(Locale.ROOT)));
            b.setEntireButtonHitBox(true);
        });
        ry -= rh + 5;
        addButton(new MenuButton(-1, rx, ry, rw, rh, "Move Here"), b -> {
            b.setEnabled(schemLoaded);
            b.setOnClick(() -> {
                ClientProxy.moveSchematicToPlayer(schematic);
                RenderSchematic.INSTANCE.refresh();
                xInput.setValue(schematic.position.x);
                yInput.setValue(schematic.position.y);
                zInput.setValue(schematic.position.z);
            });
        });
        ry -= rh + 5;
        addButton(new MenuButton(-1, rx, ry, rw, rh, "Hide"), b -> {
            b.setEnabled(schemLoaded);
            b.setOnClick(() -> {
                if (schemLoaded) {
                    schematic.isRendering = !schematic.isRendering;
                    RenderSchematic.INSTANCE.refresh();
                    b.displayText = schematic.isRendering ? "Hide" : "Show";
                }

            });
            if (schemLoaded)
                b.displayText = this.schematic.isRendering ? "Hide" : "Show";
        });
        ry -= rh + 5;
        addButton(new MenuButton(-1, rx, ry, rw, rh, "Unload"), b -> {
            b.setEnabled(schemLoaded);
            b.setOnClick(() -> {
                com.github.lunatrius.schematica.Schematica.proxy.unloadSchematic();
                this.mc.displayGuiScreen(this.parent);
            });
        });
        ry -= rh;
        int sectionGap = ry - ry1;
        ry -= sectionGap / 2 + (rh + 2) / 2;
        addButton(new MenuButton(-1, rx, ry, rw, rh, (this.schematic == null || !this.schematic.isRenderingLayer) ? "All" : "Layers"), b -> {
            b.setEnabled(schemLoaded);
            b.setOnClick(() -> {
                schematic.isRenderingLayer = !schematic.isRenderingLayer;
                nfLayer.setEnabled(this.schematic.isRenderingLayer);
                b.displayText = (this.schematic == null || !this.schematic.isRenderingLayer) ? "All" : "Layers";
                RenderSchematic.INSTANCE.refresh();
            });
            ;
        });
        ry += rh + 5;
        addButton(
                (this.nfLayer = new NumberInputField(-1, rx, ry, rw, rh, (this.schematic == null) ? 0 : this.schematic.renderingLayer, 0, (this.schematic == null) ? 0 : (this.schematic.getHeight() - 1), true)), b -> {
                    b.setEnabled((schemLoaded && this.schematic.isRenderingLayer));
                    b.setOnTextInput(a -> {
                        this.schematic.renderingLayer = this.nfLayer.getValue();
                        RenderSchematic.INSTANCE.refresh();
                    });
                });
        int maxWidth = Fonts.NUNITO_REGULAR_20.getMaxWidth("X: ", "Y: ", "Z: ");
        int w = 90;
        int h = 20;
        int x = this.content.x + this.content.width / 2 - (w + 2 + maxWidth) / 2;
        int labelX = x + maxWidth / 2;
        int buttonX = x + maxWidth + 2;
        int y = this.content.y + this.content.height / 2 - ((h + 3) * 3 - 3) / 2;
        addButton(new Label(x + (buttonX + w - x) / 2, y - 3 - h / 2, "Move Schematic", 16777215));
        addButton(new Label(labelX, y + h / 2, "X: ", 16777215));
        addButton(
                (this.xInput = new NumberInputField(-1, buttonX, y, w, h, (this.schematic == null) ? 0 : this.schematic.position.x, (int) this.mc.theWorld.getWorldBorder().minX(), (int) this.mc.theWorld.getWorldBorder().maxX(), true)), b -> {
                    b.setEnabled(schemLoaded);
                    b.setOnTextInput(a -> {
                        schematic.position.x = b.getValue();
                        RenderSchematic.INSTANCE.refresh();
                    });
                });
        y += h + 3;
        addButton(new Label(labelX, y + h / 2, "Y: ", 16777215));
        addButton((this.yInput = new NumberInputField(-1, buttonX, y, w, h, (this.schematic == null) ? 0 : this.schematic.position.y, -9999, 9999, true)), b -> {
            b.setEnabled(schemLoaded);
            b.setOnTextInput(a -> {
                schematic.position.y = b.getValue();
                RenderSchematic.INSTANCE.refresh();
            });
        });
        y += h + 3;
        addButton(new Label(labelX, y + h / 2, "Z: ", 16777215));
        addButton(
                (this.zInput = new NumberInputField(-1, buttonX, y, w, h, (this.schematic == null) ? 0 : this.schematic.position.z, (int) this.mc.theWorld.getWorldBorder().minZ(), (int) this.mc.theWorld.getWorldBorder().maxZ(), true)), b -> {
                    b.setEnabled(schemLoaded);
                    b.setOnTextInput(a -> {
                        schematic.position.z = b.getValue();
                        RenderSchematic.INSTANCE.refresh();
                    });
                });
    }

    public void onButtonInteract(Button button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonInteract(button, mouseX, mouseY, mouseButton);
        if (button instanceof SchematicHistoryButton) {
            SchematicHistoryButton hist = (SchematicHistoryButton) button;
            if (hist.schematic == null || !hist.isEnabled())
                return;
            ClientOptions.getInstance().loadSchematicFromHistory(hist.schematic);
        }
    }

    private static class SchematicHistoryButton extends MenuButton {
        private final Schematic schematic;

        private final boolean tLeft;

        private final boolean tRight;

        private final boolean bLeft;

        private final boolean bRight;

        public SchematicHistoryButton(Schematic schematic, int x, int y, int width, int height, boolean tLeft, boolean tRight, boolean bLeft, boolean bRight) {
            super(-1, x, y, width, height, (schematic == null) ? "No Schematic Loaded" : FilenameUtils.removeExtension(schematic.getFile().getName()));
            this.schematic = schematic;
            this.tLeft = tLeft;
            this.tRight = tRight;
            this.bLeft = bLeft;
            this.bRight = bRight;
            int length = this.displayText.length();
            while (this.fontRenderer.getStringWidth(this.displayText + "...") > width - 10)
                this.displayText = this.displayText.substring(0, this.displayText.length() - 1);
            if (this.displayText.length() != length)
                this.displayText += "...";
            if (this.schematic == null) {
                this.enabled = false;
            } else {
                this
                        .enabled = (ClientProxy.currentSchematic.currentFile == null || !ClientProxy.currentSchematic.currentFile.equals(schematic.getFile()));
            }
        }

        public void drawButton(int mouseX, int mouseY, boolean hovered) {
            Screen.scissorStart(this.scissorPane);
            hovered = (this.enabled && hovered);
            this.fadingColor.fade(hovered);
            this.textColor.fade((hovered || this.selected));
            boolean colored = (this.schematic != null && !this.enabled);
            int color = colored ? this.opts.mainColor.getRGB() : this.opts.mainDisabled.getRGB();
            int color1 = colored ? this.opts.secondaryColor.getRGB() : this.opts.secondaryDisabled.getRGB();
            RenderUtils.drawRoundedRectWithGradientBorder(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, 1.3F, color, color1, this.fadingColor
                    .getCurrentColor().getRGB(), this.tLeft, this.tRight, this.bLeft, this.bRight);
            this.fontRenderer.drawCenteredString(ChatColor.translate(this.displayText), this.x + this.width / 2, this.y + this.height / 2, this.textColor
                    .getCurrentColor().getRGB());
            Screen.scissorEnd(this.scissorPane);
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\schematica\ScreenSchematicControl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */