package com.github.lunatrius.schematica.client.gui.control;

import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.handler.SchematicHandler;
import co.crystaldev.client.mixin.accessor.net.minecraft.util.MixinEnumFacing;
import co.crystaldev.client.util.enums.ChatColor;
import co.crystaldev.client.util.objects.Schematic;
import co.crystaldev.client.util.objects.Transformation;
import com.github.lunatrius.core.client.gui.GuiNumericField;
import com.github.lunatrius.core.client.gui.GuiScreenBase;
import com.github.lunatrius.schematica.Schematica;
import com.github.lunatrius.schematica.client.gui.buttons.GuiButtonExt;
import com.github.lunatrius.schematica.client.gui.buttons.GuiUnicodeGlyphButton;
import com.github.lunatrius.schematica.client.gui.buttons.LoadedSchematicButton;
import com.github.lunatrius.schematica.client.gui.buttons.SchematicHistoryButton;
import com.github.lunatrius.schematica.client.printer.SchematicPrinter;
import com.github.lunatrius.schematica.client.renderer.RenderSchematic;
import com.github.lunatrius.schematica.client.util.FlipHelper;
import com.github.lunatrius.schematica.client.util.RotationHelper;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.github.lunatrius.schematica.util.LoadedSchematic;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiSchematicControl extends GuiScreenBase {
    private final SchematicWorld schematic;

    private final SchematicPrinter printer;

    private int centerX = 0;

    private int centerY = 0;

    private GuiNumericField numericX = null;

    private GuiNumericField numericY = null;

    private GuiNumericField numericZ = null;

    private GuiButtonExt btnUnload = null;

    private GuiButtonExt btnLayerMode = null;

    private GuiNumericField nfLayer = null;

    private GuiButtonExt btnHide = null;

    private GuiButtonExt btnMove = null;

    private GuiButtonExt btnFlipDirection = null;

    private GuiButtonExt btnFlip = null;

    private GuiButtonExt btnRotateDirection = null;

    private GuiButtonExt btnRotate = null;

    private GuiButtonExt btnShare = null;

    private GuiButtonExt btnMaterials = null;

    private GuiButtonExt btnPrint = null;

    private final String strMoveSchematic = I18n.format("schematica.gui.moveschematic", new Object[0]);

    private final String strOperations = I18n.format("schematica.gui.operations", new Object[0]);

    private final String strUnload = I18n.format("schematica.gui.unload", new Object[0]);

    private final String strAll = I18n.format("schematica.gui.all", new Object[0]);

    private final String strLayers = I18n.format("schematica.gui.layers", new Object[0]);

    private final String strMaterials = I18n.format("schematica.gui.materials", new Object[0]);

    private final String strPrinter = I18n.format("schematica.gui.printer", new Object[0]);

    private final String strHide = I18n.format("schematica.gui.hide", new Object[0]);

    private final String strShow = I18n.format("schematica.gui.show", new Object[0]);

    private final String strX = I18n.format("schematica.gui.x", new Object[0]);

    private final String strY = I18n.format("schematica.gui.y", new Object[0]);

    private final String strZ = I18n.format("schematica.gui.z", new Object[0]);

    private final String strOn = I18n.format("schematica.gui.on", new Object[0]);

    private final String strOff = I18n.format("schematica.gui.off", new Object[0]);

    public GuiSchematicControl(GuiScreen guiScreen) {
        super(guiScreen);
        this.schematic = ClientProxy.currentSchematic.schematic;
        this.printer = SchematicPrinter.INSTANCE;
    }

    public void initGui() {
        this.centerX = this.width / 2;
        this.centerY = this.height / 2;
        this.buttonList.clear();
        int id = 0;
        this.numericX = new GuiNumericField(this.fontRendererObj, id++, this.centerX - 50, this.centerY - 30, 100, 20);
        this.buttonList.add(this.numericX);
        this.numericY = new GuiNumericField(this.fontRendererObj, id++, this.centerX - 50, this.centerY - 5, 100, 20);
        this.buttonList.add(this.numericY);
        this.numericZ = new GuiNumericField(this.fontRendererObj, id++, this.centerX - 50, this.centerY + 20, 100, 20);
        this.buttonList.add(this.numericZ);
        this.btnUnload = new GuiButtonExt(id++, this.width - 90, this.height - 200, 80, 20, this.strUnload);
        this.buttonList.add(this.btnUnload);
        this.btnLayerMode = new GuiButtonExt(id++, this.width - 90, this.height - 150 - 25, 80, 20, (this.schematic != null && this.schematic.isRenderingLayer) ? this.strLayers : this.strAll);
        this.buttonList.add(this.btnLayerMode);
        this.btnShare = new GuiButtonExt(id++, this.width - 90, this.height - 150 - 25 - 25 - 25, 80, 20, "Share Schem");
        this.buttonList.add(this.btnShare);
        this.nfLayer = new GuiNumericField(this.fontRendererObj, id++, this.width - 90, this.height - 150, 80, 20);
        this.buttonList.add(this.nfLayer);
        this.btnHide = new GuiButtonExt(id++, this.width - 90, this.height - 105, 80, 20, (this.schematic != null && this.schematic.isRendering) ? this.strHide : this.strShow);
        this.buttonList.add(this.btnHide);
        this.btnMove = new GuiButtonExt(id++, this.width - 90, this.height - 80, 80, 20, I18n.format("schematica.gui.movehere", new Object[0]));
        this.buttonList.add(this.btnMove);
        this.btnFlipDirection = new GuiButtonExt(id++, this.width - 180, this.height - 55, 80, 20, I18n.format("schematica.gui." + ClientProxy.currentSchematic.axisFlip.getName(), new Object[0]));
        this.buttonList.add(this.btnFlipDirection);
        this.btnFlip = (GuiButtonExt) new GuiUnicodeGlyphButton(id++, this.width - 90, this.height - 55, 80, 20, " " + I18n.format("schematica.gui.flip", new Object[0]), "↔", 2.0F);
        this.buttonList.add(this.btnFlip);
        this.btnRotateDirection = new GuiButtonExt(id++, this.width - 180, this.height - 30, 80, 20, I18n.format("schematica.gui." + ClientProxy.currentSchematic.axisRotation.getName(), new Object[0]));
        this.buttonList.add(this.btnRotateDirection);
        this.btnRotate = (GuiButtonExt) new GuiUnicodeGlyphButton(id++, this.width - 90, this.height - 30, 80, 20, " " + I18n.format("schematica.gui.rotate", new Object[0]), "↻", 2.0F);
        this.buttonList.add(this.btnRotate);
        this.btnMaterials = new GuiButtonExt(id++, 10, this.height - 70, 80, 20, this.strMaterials);
        this.buttonList.add(this.btnMaterials);
        this.btnPrint = new GuiButtonExt(id++, 10, this.height - 30, 80, 20, this.printer.isPrinting() ? this.strOn : this.strOff);
        this.buttonList.add(this.btnPrint);
        int x = 5, y = this.height / 2 - 60, w = 220, h = 20;
        List<Schematic> history = new ArrayList<>(ClientOptions.getInstance().getSchematicHistory());
        int i;
        for (i = history.size() - 1; i >= 0; i--) {
            Schematic schematic = history.get(i);
            this.buttonList.add(new SchematicHistoryButton(schematic, x, y, w, h) {

            });
            y += h + 5;
        }
        for (i = 0; i < 5 - history.size(); i++) {
            this.buttonList.add(new GuiButton(-1, x, y, w, h, "No schematic loaded") {

            });
            y += h + 5;
        }
        y = 5;
        for (LoadedSchematic schematic : ClientProxy.loadedSchematics) {
            String name = (schematic.currentFile == null) ? "No Schematic Loaded" : schematic.currentFile.getName().replace(".schematic", "");
            if (name.length() > 20)
                name = name.substring(0, 19) + "...";
            LoadedSchematicButton button = new LoadedSchematicButton(schematic, name, x, y, this.fontRendererObj.getStringWidth(name) + 12, h);
            this.buttonList.add(button);
            x += button.getButtonWidth() + 5;
            if (!(button.enabled = !ClientProxy.currentSchematic.equals(schematic)))
                button.displayString = ChatColor.translate("&a" + button.displayString);
        }
        this.numericX.setEnabled((this.schematic != null));
        this.numericY.setEnabled((this.schematic != null));
        this.numericZ.setEnabled((this.schematic != null));
        this.btnShare.enabled = (this.schematic != null);
        this.btnUnload.enabled = (this.schematic != null);
        this.btnLayerMode.enabled = (this.schematic != null);
        this.nfLayer.setEnabled((this.schematic != null && this.schematic.isRenderingLayer));
        this.btnHide.enabled = (this.schematic != null);
        this.btnMove.enabled = (this.schematic != null);
        this.btnFlipDirection.enabled = (this.schematic != null);
        this.btnFlip.enabled = (this.schematic != null);
        this.btnRotateDirection.enabled = (this.schematic != null);
        this.btnRotate.enabled = (this.schematic != null);
        this.btnMaterials.enabled = (this.schematic != null);
        this.btnPrint.enabled = (this.schematic != null && this.printer.isEnabled());
        setMinMax(this.numericX);
        setMinMax(this.numericY);
        setMinMax(this.numericZ);
        if (this.schematic != null)
            setPoint(this.numericX, this.numericY, this.numericZ, (BlockPos) this.schematic.position);
        this.nfLayer.setMinimum(0);
        this.nfLayer.setMaximum((this.schematic != null) ? (this.schematic.getHeight() - 1) : 0);
        if (this.schematic != null)
            this.nfLayer.setValue(this.schematic.renderingLayer);
    }

    private void setMinMax(GuiNumericField numericField) {
        numericField.setMinimum(-30000000);
        numericField.setMaximum(30000000);
    }

    private void setPoint(GuiNumericField numX, GuiNumericField numY, GuiNumericField numZ, BlockPos point) {
        numX.setValue(point.getX());
        numY.setValue(point.getY());
        numZ.setValue(point.getZ());
    }

    protected void actionPerformed(GuiButton guiButton) {
        if (guiButton.enabled) {
            if (guiButton instanceof SchematicHistoryButton) {
                ClientOptions.getInstance().loadSchematicFromHistory(((SchematicHistoryButton) guiButton).schematic);
                return;
            }
            if (guiButton instanceof LoadedSchematicButton) {
                ClientProxy.currentSchematic = ((LoadedSchematicButton) guiButton).schematic;
                if (ClientProxy.currentSchematic.schematic != null) {
                    ClientProxy.currentSchematic.schematic.isRendering = true;
                    ClientProxy.moveToPlayer = false;
                    Schematica.proxy.awaitingChange = true;
                } else {
                    initGui();
                }
                return;
            }
            if (this.schematic == null)
                return;
            if (guiButton.id == this.btnShare.id)
                SchematicHandler.getInstance().shareCurrentSchematic();
            if (guiButton.id == this.numericX.id) {
                this.schematic.position.x = this.numericX.getValue();
                RenderSchematic.INSTANCE.refresh();
            } else if (guiButton.id == this.numericY.id) {
                this.schematic.position.y = this.numericY.getValue();
                RenderSchematic.INSTANCE.refresh();
            } else if (guiButton.id == this.numericZ.id) {
                this.schematic.position.z = this.numericZ.getValue();
                RenderSchematic.INSTANCE.refresh();
            } else if (guiButton.id == this.btnUnload.id) {
                Schematica.proxy.unloadSchematic();
                this.mc.displayGuiScreen(this.parentScreen);
            } else if (guiButton.id == this.btnLayerMode.id) {
                this.schematic.isRenderingLayer = !this.schematic.isRenderingLayer;
                this.btnLayerMode.displayString = this.schematic.isRenderingLayer ? this.strLayers : this.strAll;
                this.nfLayer.setEnabled(this.schematic.isRenderingLayer);
                RenderSchematic.INSTANCE.refresh();
            } else if (guiButton.id == this.nfLayer.id) {
                this.schematic.renderingLayer = this.nfLayer.getValue();
                RenderSchematic.INSTANCE.refresh();
            } else if (guiButton.id == this.btnHide.id) {
                this.btnHide.displayString = this.schematic.toggleRendering() ? this.strHide : this.strShow;
                RenderSchematic.INSTANCE.refresh();
            } else if (guiButton.id == this.btnMove.id) {
                ClientProxy.moveSchematicToPlayer(this.schematic);
                RenderSchematic.INSTANCE.refresh();
                setPoint(this.numericX, this.numericY, this.numericZ, (BlockPos) this.schematic.position);
            } else if (guiButton.id == this.btnFlipDirection.id) {
                EnumFacing[] values = MixinEnumFacing.getValues();
                ClientProxy.currentSchematic.axisFlip = values[(ClientProxy.currentSchematic.axisFlip.ordinal() + 2) % values.length];
                guiButton.displayString = I18n.format("schematica.gui." + ClientProxy.currentSchematic.axisFlip.getName(), new Object[0]);
            } else if (guiButton.id == this.btnFlip.id) {
                if (FlipHelper.INSTANCE.flip(this.schematic, ClientProxy.currentSchematic.axisFlip, isShiftKeyDown())) {
                    RenderSchematic.INSTANCE.refresh();
                    SchematicPrinter.INSTANCE.refresh();
                    ClientProxy.currentSchematic.transformations.add(new Transformation(Transformation.Type.FLIP, ClientProxy.currentSchematic.axisFlip, this.schematic.position.x, this.schematic.position.y, this.schematic.position.z));
                    ClientProxy.currentSchematic.replaceHistory.clear();
                }
            } else if (guiButton.id == this.btnRotateDirection.id) {
                EnumFacing[] values = MixinEnumFacing.getValues();
                ClientProxy.currentSchematic.axisRotation = values[(ClientProxy.currentSchematic.axisRotation.ordinal() + 1) % values.length];
                guiButton.displayString = I18n.format("schematica.gui." + ClientProxy.currentSchematic.axisRotation.getName(), new Object[0]);
            } else if (guiButton.id == this.btnRotate.id) {
                if (RotationHelper.INSTANCE.rotate(this.schematic, ClientProxy.currentSchematic.axisRotation, isShiftKeyDown())) {
                    setPoint(this.numericX, this.numericY, this.numericZ, (BlockPos) this.schematic.position);
                    RenderSchematic.INSTANCE.refresh();
                    SchematicPrinter.INSTANCE.refresh();
                    ClientProxy.currentSchematic.transformations.add(new Transformation(Transformation.Type.ROTATION, ClientProxy.currentSchematic.axisRotation, this.schematic.position.x, this.schematic.position.y, this.schematic.position.z));
                    ClientProxy.currentSchematic.replaceHistory.clear();
                }
            } else if (guiButton.id == this.btnMaterials.id) {
                this.mc.displayGuiScreen((GuiScreen) new GuiSchematicMaterials((GuiScreen) this));
            } else if (guiButton.id == this.btnPrint.id && this.printer.isEnabled()) {
                boolean isPrinting = this.printer.togglePrinting();
                this.btnPrint.displayString = isPrinting ? this.strOn : this.strOff;
            }
        }
    }

    public void handleKeyboardInput() throws IOException {
        super.handleKeyboardInput();
        if (this.btnFlip.enabled)
            this.btnFlip.packedFGColour = isShiftKeyDown() ? 16711680 : 0;
        if (this.btnRotate.enabled)
            this.btnRotate.packedFGColour = isShiftKeyDown() ? 16711680 : 0;
    }

    public void drawScreen(int par1, int par2, float par3) {
        drawCenteredString(this.fontRendererObj, this.strMoveSchematic, this.centerX, this.centerY - 45, 16777215);
        drawCenteredString(this.fontRendererObj, this.strMaterials, 50, this.height - 85, 16777215);
        drawCenteredString(this.fontRendererObj, this.strPrinter, 50, this.height - 45, 16777215);
        drawCenteredString(this.fontRendererObj, this.strLayers, this.width - 50, this.height - 165, 16777215);
        drawCenteredString(this.fontRendererObj, this.strOperations, this.width - 50, this.height - 120, 16777215);
        drawString(this.fontRendererObj, this.strX, this.centerX - 65, this.centerY - 24, 16777215);
        drawString(this.fontRendererObj, this.strY, this.centerX - 65, this.centerY + 1, 16777215);
        drawString(this.fontRendererObj, this.strZ, this.centerX - 65, this.centerY + 26, 16777215);
        super.drawScreen(par1, par2, par3);
    }
}