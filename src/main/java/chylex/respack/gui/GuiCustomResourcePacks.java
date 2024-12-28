package chylex.respack.gui;

import chylex.respack.packs.ResourcePackListEntryFolder;
import chylex.respack.packs.ResourcePackListEntryFoundCustom;
import chylex.respack.packs.ResourcePackListProcessor;
import co.crystaldev.client.Reference;
import co.crystaldev.client.util.ReflectionHelper;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.*;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GuiCustomResourcePacks extends GuiScreenResourcePacks {
  private static Constructor<ResourcePackRepository.Entry> entryConstructor;
  
  private final GuiScreen parentScreen;
  
  private GuiTextField searchField;
  
  private GuiResourcePackAvailable guiPacksAvailable;
  
  private GuiResourcePackSelected guiPacksSelected;
  
  private List<ResourcePackListEntry> listPacksAvailable;
  
  private List<ResourcePackListEntry> listPacksAvailableProcessed;
  
  private List<ResourcePackListEntry> listPacksDummy;
  
  private List<ResourcePackListEntry> listPacksSelected;
  
  private ResourcePackListProcessor listProcessor;
  
  public static ResourcePackRepository.Entry createEntryInstance(ResourcePackRepository repository, File file) {
    try {
      if (entryConstructor == null) {
        entryConstructor = ResourcePackRepository.Entry.class.getDeclaredConstructor(new Class[] { ResourcePackRepository.class, File.class });
        entryConstructor.setAccessible(true);
      } 
      return entryConstructor.newInstance(new Object[] { repository, file });
    } catch (Throwable ex) {
      Reference.LOGGER.error("Unable to create repository entry", ex);
      return null;
    } 
  }
  
  private final Field top = ReflectionHelper.findField(GuiSlot.class, new String[] { "top", "top", "d" });
  
  private File currentFolder;
  
  private GuiButton selectedButton;
  
  private boolean hasUpdated;
  
  private boolean requiresReload;
  
  private Comparator<ResourcePackListEntry> currentSorter;
  
  public GuiCustomResourcePacks(GuiScreen parentScreen) {
    super(parentScreen);
    this.parentScreen = parentScreen;
  }
  
  public void initGui() {
    Keyboard.enableRepeatEvents(true);
    this.buttonList.add(new GuiOptionButton(1, this.width / 2 + 100 - 75, this.height - 26, I18n.format("gui.done")));
    this.buttonList.add(new GuiOptionButton(2, this.width / 2 + 100 - 75, this.height - 48, I18n.format("resourcePack.openFolder")));
    this.buttonList.add(new GuiOptionButton(10, this.width / 2 - 204, this.height - 26, 40, 20, "A-Z"));
    this.buttonList.add(new GuiOptionButton(11, this.width / 2 - 204 + 44, this.height - 26, 40, 20, "Z-A"));
    this.buttonList.add(new GuiOptionButton(20, this.width / 2 - 74, this.height - 26, 70, 20, "Refresh"));
    String prevText = (this.searchField == null) ? "" : this.searchField.getText();
    this.searchField = new GuiTextField(30, this.fontRendererObj, this.width / 2 - 203, this.height - 46, 198, 16);
    this.searchField.setText(prevText);
    if (!this.requiresReload) {
      this.listPacksAvailable = Lists.newArrayListWithCapacity(8);
      this.listPacksAvailableProcessed = Lists.newArrayListWithCapacity(8);
      this.listPacksDummy = Lists.newArrayListWithCapacity(1);
      this.listPacksSelected = Lists.newArrayListWithCapacity(8);
      ResourcePackRepository repository = this.mc.getResourcePackRepository();
      repository.updateRepositoryEntriesAll();
      this.currentFolder = repository.getDirResourcepacks();
      this.listPacksAvailable.addAll(createAvailablePackList(repository));
      for (ResourcePackRepository.Entry entry : Lists.reverse(repository.getRepositoryEntries()))
        this.listPacksSelected.add(new ResourcePackListEntryFoundCustom(this, entry)); 
      this.listPacksSelected.add(new ResourcePackListEntryDefault(this));
    } 
    this.guiPacksAvailable = new GuiResourcePackAvailable(this.mc, 200, this.height, this.listPacksAvailableProcessed);
    this.guiPacksAvailable.setSlotXBoundsFromLeft(this.width / 2 - 204);
    this.guiPacksAvailable.registerScrollButtons(7, 8);
    try {
      this.top.set(this.guiPacksAvailable, Integer.valueOf(4));
    } catch (IllegalAccessException ex) {
      Reference.LOGGER.error("Unable to set field value in guiPacksAvailable", ex);
    } 
    this.guiPacksSelected = new GuiResourcePackSelected(this.mc, 200, this.height, this.listPacksSelected);
    this.guiPacksSelected.setSlotXBoundsFromLeft(this.width / 2 + 4);
    this.guiPacksSelected.registerScrollButtons(7, 8);
    try {
      this.top.set(this.guiPacksSelected, Integer.valueOf(4));
    } catch (IllegalAccessException ex) {
      Reference.LOGGER.error("Unable to set field value in guiPacksSelected", ex);
    } 
    this.listProcessor = new ResourcePackListProcessor(this.listPacksAvailable, this.listPacksAvailableProcessed);
    this.listProcessor.setSorter((this.currentSorter == null) ? (this.currentSorter = ResourcePackListProcessor.sortAZ) : this.currentSorter);
    this.listProcessor.setFilter(this.searchField.getText().trim());
  }
  
  protected void actionPerformed(GuiButton button) {
    if (button.id == 20) {
      refreshAvailablePacks();
    } else if (button.id == 11) {
      this.listProcessor.setSorter(this.currentSorter = ResourcePackListProcessor.sortZA);
    } else if (button.id == 10) {
      this.listProcessor.setSorter(this.currentSorter = ResourcePackListProcessor.sortAZ);
    } else if (button.id == 2) {
      GuiUtils.openFolder(this.mc.getResourcePackRepository().getDirResourcepacks());
    } else if (button.id == 1) {
      if (this.requiresReload) {
        List<ResourcePackRepository.Entry> selected = refreshSelectedPacks();
        this.mc.gameSettings.resourcePacks.clear();
        for (ResourcePackRepository.Entry entry : selected)
          this.mc.gameSettings.resourcePacks.add(entry.getResourcePackName()); 
        this.mc.gameSettings.saveOptions();
        this.mc.refreshResources();
      } 
      this.mc.displayGuiScreen(this.parentScreen);
    } 
  }
  
  protected void mouseClicked(int mouseX, int mouseY, int buttonId) {
    if (buttonId == 0)
      for (GuiButton button : this.buttonList) {
        if (button.isMouseOver() && button.mousePressed(this.mc, mouseX, mouseY)) {
          this.selectedButton = button;
          button.playPressSound(this.mc.getSoundHandler());
          actionPerformed(button);
        } 
      }  
    this.guiPacksAvailable.mouseClicked(mouseX, mouseY, buttonId);
    this.guiPacksSelected.mouseClicked(mouseX, mouseY, buttonId);
    this.searchField.mouseClicked(mouseX, mouseY, buttonId);
    this.listProcessor.refresh();
  }
  
  public void handleMouseInput() throws IOException {
    try {
      super.handleMouseInput();
    } catch (NullPointerException nullPointerException) {}
    this.guiPacksAvailable.handleMouseInput();
    this.guiPacksSelected.handleMouseInput();
  }
  
  protected void mouseMovedOrUp(int mouseX, int mouseY, int eventType) {
    if (eventType == 0 && this.selectedButton != null) {
      this.selectedButton.mouseReleased(mouseX, mouseY);
      this.selectedButton = null;
    } 
  }
  
  protected void keyTyped(char keyChar, int keyCode) throws IOException {
    super.keyTyped(keyChar, keyCode);
    if (this.searchField.isFocused()) {
      this.searchField.textboxKeyTyped(keyChar, keyCode);
      this.listProcessor.setFilter(this.searchField.getText().trim());
    } 
  }
  
  public void onGuiClosed() {
    Keyboard.enableRepeatEvents(false);
  }
  
  public void updateScreen() {
    this.searchField.updateCursorCounter();
    if (this.hasUpdated) {
      this.hasUpdated = false;
      refreshSelectedPacks();
      refreshAvailablePacks();
    } 
  }
  
  public void moveToFolder(File folder) {
    this.currentFolder = folder;
    refreshSelectedPacks();
    refreshAvailablePacks();
  }
  
  public void refreshAvailablePacks() {
    this.listPacksAvailable.clear();
    this.listPacksAvailable.addAll(createAvailablePackList(this.mc.getResourcePackRepository()));
    this.listProcessor.refresh();
  }
  
  public List<ResourcePackRepository.Entry> refreshSelectedPacks() {
    List<ResourcePackRepository.Entry> selected = Lists.newArrayListWithCapacity(this.listPacksSelected.size());
    for (ResourcePackListEntry entry : this.listPacksSelected) {
      if (!(entry instanceof ResourcePackListEntryFound))
        continue; 
      ResourcePackListEntryFound packEntry = (ResourcePackListEntryFound)entry;
      if (packEntry.func_148318_i() != null)
          selected.add(packEntry.func_148318_i());
      //if (packEntry.getResourcePackEntry() != null)
      //  selected.add(packEntry.getResourcePackEntry());
    } 
    Collections.reverse(selected);
    this.mc.getResourcePackRepository().setRepositories(selected);
    return selected;
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTickTime) {
    drawBackground(0);
    this.guiPacksAvailable.drawScreen(mouseX, mouseY, partialTickTime);
    this.guiPacksSelected.drawScreen(mouseX, mouseY, partialTickTime);
    this.searchField.drawTextBox();
    for (GuiButton button : this.buttonList)
      button.drawButton(this.mc, mouseX, mouseY); 
  }
  
  private List<ResourcePackListEntryFoundCustom> createAvailablePackList(ResourcePackRepository repository) {
    List<ResourcePackListEntryFoundCustom> list = Lists.newArrayList();
    if (!repository.getDirResourcepacks().equals(this.currentFolder))
      list.add(new ResourcePackListEntryFolder(this, this.currentFolder.getParentFile(), true)); 
    File[] files = this.currentFolder.listFiles();
    if (files != null)
      for (File file : files) {
        if (file.isDirectory() && !(new File(file, "pack.mcmeta")).isFile()) {
          list.add(new ResourcePackListEntryFolder(this, file));
        } else {
          ResourcePackRepository.Entry entry = createEntryInstance(repository, file);
          if (entry != null)
            try {
              entry.updateResourcePack();
              list.add(new ResourcePackListEntryFoundCustom(this, entry));
            } catch (Exception exception) {} 
        } 
      }  
    List<ResourcePackRepository.Entry> repositoryEntries = repository.getRepositoryEntries();
    //list.removeIf(listEntry -> (listEntry.getResourcePackEntry() != null && repositoryEntries.contains(listEntry.getResourcePackEntry())));
    list.removeIf(listEntry -> (listEntry.func_148318_i() != null && repositoryEntries.contains(listEntry.func_148318_i())));

    return list;
  }
  
  public boolean hasResourcePackEntry(ResourcePackListEntry entry) {
    return this.listPacksSelected.contains(entry);
  }
  
  public List<ResourcePackListEntry> getListContaining(ResourcePackListEntry entry) {
    return hasResourcePackEntry(entry) ? this.listPacksSelected : this.listPacksAvailable;
  }
  
  public List<ResourcePackListEntry> getAvailableResourcePacks() {
    this.hasUpdated = true;
    this.listPacksDummy.clear();
    return this.listPacksDummy;
  }
  
  public List<ResourcePackListEntry> getSelectedResourcePacks() {
    this.hasUpdated = true;
    return this.listPacksSelected;
  }
  
  public void markChanged() {
    this.requiresReload = true;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\chylex\chylex.respack\gui\GuiCustomResourcePacks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */