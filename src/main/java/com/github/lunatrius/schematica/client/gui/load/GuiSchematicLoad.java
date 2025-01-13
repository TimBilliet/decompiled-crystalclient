package com.github.lunatrius.schematica.client.gui.load;

import co.crystaldev.client.Client;
import co.crystaldev.client.util.enums.ChatColor;
import com.github.lunatrius.core.client.gui.GuiScreenBase;
import com.github.lunatrius.schematica.Schematica;
import com.github.lunatrius.schematica.client.gui.buttons.LoadedSchematicButton;
import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.github.lunatrius.schematica.reference.Reference;
import com.github.lunatrius.schematica.util.FileFilterSchematic;
import com.github.lunatrius.schematica.util.LoadedSchematic;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.Sys;

import java.awt.*;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class GuiSchematicLoad extends GuiScreenBase {
    private static final int SEARCH_BOX_COLOR_EMPTY = (new Color(160, 160, 160, 255)).getRGB();

    private static final FileFilterSchematic FILE_FILTER_FOLDER = new FileFilterSchematic(true);

    private static final FileFilterSchematic FILE_FILTER_SCHEMATIC = new FileFilterSchematic(false);

    private static final ItemStack GRASS = new ItemStack((Block) Blocks.grass);

    private GuiSchematicLoadSlot guiSchematicLoadSlot;

    private GuiButton btnOpenDir = null;

    private GuiButton btnDone = null;

    private GuiTextField moduleSearchBar;

    private static String searchString;

    private final String strTitle = I18n.format("schematica.gui.title", new Object[0]);

    private final String strFolderInfo = I18n.format("schematica.gui.folderInfo", new Object[0]);

    private final String strNoSchematic = I18n.format("schematica.gui.noschematic", new Object[0]);

    private boolean loading = false;

    protected File currentDirectory = ConfigurationHandler.schematicDirectory;

    protected final List<GuiSchematicEntry> schematicFiles = new ArrayList<>();

    public GuiSchematicLoad(GuiScreen guiScreen) {
        super(guiScreen);
        System.out.println("guischematicload");
    }

    public void initGui() {
        int id = 0;
        super.initGui();
        this.btnOpenDir = new GuiButton(id++, this.width / 2 - 154, this.height - 36, 150, 20, I18n.format("schematica.gui.openFolder", new Object[0]));
        this.buttonList.add(this.btnOpenDir);
        this.btnDone = new GuiButton(id++, this.width / 2 + 4, this.height - 36, 150, 20, I18n.format("schematica.gui.done", new Object[0]));
        this.buttonList.add(this.btnDone);
        this.guiSchematicLoadSlot = new GuiSchematicLoadSlot(this);
        if (this.moduleSearchBar == null) {
            this.moduleSearchBar = new GuiTextField(id, this.fontRendererObj, this.width / 2 - 70 - 140, 1, 140, 13);
            this.moduleSearchBar.setMaxStringLength(50);
            this.moduleSearchBar.setFocused(true);
        }
        if (searchString != null)
            this.moduleSearchBar.setText(searchString);
        int x = 5, y = 18, h = 20;
        for (LoadedSchematic schematic : ClientProxy.loadedSchematics) {
            String name = (schematic.currentFile == null) ? "No Schematic Loaded" : schematic.currentFile.getName().replace(".schematic", "");
            if (name.length() > 20)
                name = name.substring(0, 19) + "...";
            LoadedSchematicButton button = new LoadedSchematicButton(schematic, name, x, y, this.fontRendererObj.getStringWidth(name) + 12, h);
            this.buttonList.add(button);
            y += h + 5;
            if (!(button.enabled = !ClientProxy.currentSchematic.equals(schematic)))
                button.displayString = ChatColor.translate("&a" + button.displayString);
        }
        reloadSchematics();
    }

    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        this.guiSchematicLoadSlot.handleMouseInput();
    }

    protected void actionPerformed(GuiButton guiButton) {
        System.out.println("actionperformed guischematicload");
        if (guiButton.enabled) {
            if (guiButton instanceof LoadedSchematicButton) {
                System.out.println("loadschembutton");
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
            if (guiButton.id == this.btnOpenDir.id) {
                boolean retry = false;
                try {
                    Class<?> c = Class.forName("java.awt.Desktop");
                    Object m = c.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
                    c.getMethod("browse", new Class[]{URI.class}).invoke(m, new Object[]{ConfigurationHandler.schematicDirectory.toURI()});
                } catch (Throwable e) {
                    retry = true;
                }
                if (retry) {
                    Reference.logger.info("Opening via Sys class!");
                    Sys.openURL("file://" + ConfigurationHandler.schematicDirectory.getAbsolutePath());
                }
            } else if (guiButton.id == this.btnDone.id) {
                if (Schematica.proxy.isLoadEnabled) {
                    if (!this.loading)
                        loadSchematic();
                    this.loading = true;
                }
                this.mc.displayGuiScreen(this.parentScreen);
            } else {
                this.guiSchematicLoadSlot.actionPerformed(guiButton);
            }
        }
    }

    public void drawScreen(int x, int y, float partialTicks) {
        this.guiSchematicLoadSlot.drawScreen(x, y, partialTicks);
        drawCenteredString(this.fontRendererObj, this.strTitle, this.width / 2, 4, 16777215);
        drawCenteredString(this.fontRendererObj, this.strFolderInfo, this.width / 2 - 78, this.height - 12, 8421504);
        this.moduleSearchBar.drawTextBox();
        if (this.moduleSearchBar.getText().isEmpty() && !this.moduleSearchBar.isFocused())
            this.fontRendererObj.drawString("Search...", this.moduleSearchBar.xPosition + 4, this.moduleSearchBar.yPosition + 3, SEARCH_BOX_COLOR_EMPTY);
        super.drawScreen(x, y, partialTicks);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (this.moduleSearchBar.isFocused() && (this.moduleSearchBar.getText().length() != 0 || keyCode != (co.crystaldev.client.feature.impl.factions.Schematica.getInstance()).loadSchematic.getKeyCode())) {
            this.moduleSearchBar.textboxKeyTyped(typedChar, keyCode);
            searchString = this.moduleSearchBar.getText();
            initGui();
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.moduleSearchBar.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void updateScreen() {
        super.updateScreen();
        this.moduleSearchBar.updateCursorCounter();
    }

    protected void changeDirectory(String directory) {
        this.currentDirectory = new File(this.currentDirectory, directory);
        try {
            this.currentDirectory = this.currentDirectory.getCanonicalFile();
        } catch (IOException ioe) {
            Reference.logger.error("Failed to canonize directory!", ioe);
        }
        reloadSchematics();
    }

    protected void reloadSchematics() {
        this.schematicFiles.clear();
        try {
            if (!this.currentDirectory.getCanonicalPath().equals(ConfigurationHandler.schematicDirectory.getCanonicalPath()))
                this.schematicFiles.add(new GuiSchematicEntry("..", Items.lava_bucket, 0, null, true));
        } catch (IOException e) {
            Reference.logger.error("Failed to add GuiSchematicEntry!", e);
        }
        File[] filesFolders = this.currentDirectory.listFiles((FileFilter) FILE_FILTER_FOLDER);
        if (filesFolders == null) {
            Reference.logger.error("listFiles returned null (directory: {})!", new Object[]{this.currentDirectory});
        } else {
            for (File file : filesFolders) {
                if (file != null) {
                    String name = file.getName();
                    File[] files = file.listFiles();
                    Item item = (files == null || files.length == 0) ? Items.bucket : Items.water_bucket;
                    if (matchesSearch(name))
                        this.schematicFiles.add(new GuiSchematicEntry(name, item, 0, file, file.isDirectory()));
                }
            }
        }
        File[] filesSchematics = this.currentDirectory.listFiles((FileFilter) FILE_FILTER_SCHEMATIC);
        if (filesSchematics == null || filesSchematics.length == 0) {
            this.schematicFiles.add(new GuiSchematicEntry(this.strNoSchematic, Blocks.dirt, 0, null, false));
        } else {
            for (File file : filesSchematics) {
                String name = file.getName();
                if (matchesSearch(name))
                    this.schematicFiles.add(new GuiSchematicEntry(name, GRASS, file, file.isDirectory()));
            }
        }
    }

    private void loadSchematic() {
        int selectedIndex = this.guiSchematicLoadSlot.selectedIndex;
        try {
            if (selectedIndex >= 0 && selectedIndex < this.schematicFiles.size()) {
                GuiSchematicEntry schematicEntry = this.schematicFiles.get(selectedIndex);
                Client.sendMessage("Loading schematic " + schematicEntry.getName(), true);
                Schematica.proxy.unloadSchematic();
                Schematica.proxy.loadSchematic(null, this.currentDirectory, schematicEntry.getName());
            }
        } catch (Exception e) {
            Reference.logger.error("Failed to load schematic!", e);
        }
    }

    private boolean matchesSearch(String text) {
        if (StringUtils.isEmpty(this.moduleSearchBar.getText()))
            return true;
        if (text.equals(this.strNoSchematic))
            return true;
        return text.toLowerCase().contains(this.moduleSearchBar.getText().toLowerCase());
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\gui\load\GuiSchematicLoad.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */