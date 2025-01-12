package co.crystaldev.client.gui.screens.schematica;

import chylex.respack.gui.GuiUtils;
import co.crystaldev.client.Client;
import co.crystaldev.client.Reference;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.buttons.Label;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.gui.buttons.SchematicEntryButton;
import co.crystaldev.client.gui.buttons.SearchButton;
import co.crystaldev.client.gui.buttons.settings.DropdownButton;
import co.crystaldev.client.gui.screens.screen_overlay.OverlayRemoveSchematic;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.ChatColor;
import co.crystaldev.client.util.enums.SchematicaGuiType;
import com.github.lunatrius.schematica.Schematica;
import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import com.github.lunatrius.schematica.util.FileFilterSchematic;
import com.github.lunatrius.schematica.world.schematic.SchematicUtil;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ScreenLoadSchematic extends ScreenSchematicaBase {
  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

  private static final Pattern SCHEMATIC_FILE_PATTERN = Pattern.compile("\\.schematic$");

  private static final FileFilterSchematic FILE_FILTER_FOLDER = new FileFilterSchematic(true);

  private static final FileFilterSchematic FILE_FILTER_SCHEMATIC = new FileFilterSchematic(false);

  private Pane schematicInfo;

  private File currentDirectory;

  private SearchButton search;

  private File selected = null;

  public ScreenLoadSchematic() {
    super(SchematicaGuiType.LOAD_SCHEMATIC);
    this.currentDirectory = ConfigurationHandler.schematicDirectory;
    DECIMAL_FORMAT.setGroupingUsed(true);
    DECIMAL_FORMAT.setGroupingSize(3);
  }

  public void init() {
    super.init();
    this.schematicInfo = new Pane(this.content.x + this.content.width - 140, this.content.y, 130, this.content.height - 10);
    this.content.width -= 140;
    this.content.setScrollIf(b -> b.hasAttribute("schematic_entry"));
    int x = this.schematicInfo.x + 8;
    int y = this.schematicInfo.y;
    int w = this.schematicInfo.width - 16;
    int h = 18;
    addButton(new Label(x + w / 2, y + 5 + Fonts.NUNITO_SEMI_BOLD_18.getStringHeight() / 2, "Schematic Info", 16777215, Fonts.NUNITO_SEMI_BOLD_18));
    y += 20;
    addButton((Button)new DropdownButton(-1, x, y, w, h, this.loadedSchematic), b -> b.renderLast = true);
    initSchematicInfo();
    int half = this.header.height / 2 - Fonts.NUNITO_SEMI_BOLD_24.getStringHeight() / 2;
    int searchSize = this.header.height - half * 2;
    addButton((this.search = new SearchButton(this.header.x + this.header.width - half - searchSize, this.header.y + half, searchSize, searchSize * 6, searchSize)));
    initSchematics();
  }

  public void draw(int mouseX, int mouseY, float partialTicks) {
    super.draw(mouseX, mouseY, partialTicks);
    RenderUtils.drawRoundedRect(this.schematicInfo.x, this.schematicInfo.y, (this.schematicInfo.x + this.schematicInfo.width), (this.schematicInfo.y + this.schematicInfo.height), 22.0D, this.opts.sidebarBackground

        .getRGB());
    this.content.scroll(this, mouseX, mouseY);
    if (this.search.wasUpdated())
      initSchematics();
  }

  public void onButtonInteract(Button button, int mouseX, int mouseY, int mouseButton) {
    super.onButtonInteract(button, mouseX, mouseY, mouseButton);
    if (button instanceof SchematicEntryButton) {
      SchematicEntryButton entry = (SchematicEntryButton)button;
      if (entry.isDirectory()) {
        this.content.reset();
        changeDirectory(entry.displayText);
      } else {
        this.selected = entry.getFile();
        if (entry.isDeleteHovered(mouseX, mouseY)) {
          addOverlay(new OverlayRemoveSchematic(entry.getFile()));
        } else if (entry.isLoadHovered(mouseX, mouseY)) {
          loadSchematic();
        } else {
          ((Screen)this.mc.currentScreen).buttons.forEach(b -> {
                if (b instanceof SchematicEntryButton)
                  ((SchematicEntryButton)b).setSelected(false);
              });
          entry.setSelected(true);
          initSchematicInfo();
        }
      }
    }
  }

  public void initSchematicInfo() {
    removeButton(b -> b.hasAttribute("schematic_info_entry"));
    int w = this.schematicInfo.width - 16;
    int h = 18;
    int x = this.schematicInfo.x + 8;
    int y = this.schematicInfo.y + 20 + h + 10;
    int loadedY = this.schematicInfo.y + this.schematicInfo.height - 8 - 18;
    if (this.selected == null) {
      addButton((Button)new Label(x + w / 2, y + h / 2, ChatColor.translate("&oNo Schematic Selected"), 16777215, Fonts.NUNITO_SEMI_BOLD_18), b -> b.addAttribute("schematic_info_entry"));
    } else {
      try {
        if (!this.selected.exists()) {
          this.selected = null;
          initSchematicInfo();
          return;
        }
        NBTTagCompound schematic = SchematicUtil.readTagCompoundFromFile(this.selected);
        if (schematic != null) {
          BasicFileAttributes bfa = Files.readAttributes(this.selected.toPath(), BasicFileAttributes.class, new java.nio.file.LinkOption[0]);
          FileTime creation = bfa.creationTime();
          String name = this.selected.getName().replaceAll("\\.schematic", "");
          int len = name.length();
          while (Fonts.NUNITO_SEMI_BOLD_16.getStringWidth(name + "...") > w)
            name = name.substring(0, name.length() - 1);
          if (name.length() != len)
            name = name + "...";
          addButton(new SchematicDataEntry(x, y, w, h, name, null), b -> {
                b.addAttribute("schematic_info_entry");
                b.fontRenderer = Fonts.NUNITO_SEMI_BOLD_16;
              });
          y += h;
          addButton(new SchematicDataEntry(x, y, w, h, "Modified", (new SimpleDateFormat("MM/dd/yyyy")).format(creation.toMillis())), b -> b.addAttribute("schematic_info_entry"));
          y += h;
          int blocks = 0;
          byte[] arrayOfByte = schematic.getByteArray("Blocks");
          for(byte b : arrayOfByte) {
            if(b!= 0) {
              blocks++;
            }
          }
          addButton(new SchematicDataEntry(x, y, w, h, "Total Blocks", DECIMAL_FORMAT.format(blocks)), b -> b.addAttribute("schematic_info_entry"));
          y += h;
          addButton(new SchematicDataEntry(x, y, w, h, "Length", DECIMAL_FORMAT.format(schematic.getInteger("Length"))), b -> b.addAttribute("schematic_info_entry"));
          y += h;
          addButton(new SchematicDataEntry(x, y, w, h, "Width", DECIMAL_FORMAT.format(schematic.getInteger("Width"))), b -> b.addAttribute("schematic_info_entry"));
          y += h;
          addButton(new SchematicDataEntry(x, y, w, h, "Height", DECIMAL_FORMAT.format(schematic.getInteger("Height"))), b -> b.addAttribute("schematic_info_entry"));
          addButton(new MenuButton(-1, x, this.schematicInfo.y + this.schematicInfo.height - 8 - 18, w, h, "Load Schematic"), b -> {
                b.addAttribute("schematic_info_entry");
                b.onClick = this::loadSchematic;

              });
          addButton(new MenuButton(-1, x, this.schematicInfo.y + this.schematicInfo.height - 8 - 41, w, h, "Upload to WorldEdit"), b -> {
                b.addAttribute("schematic_info_entry");
            b.setOnClick(new Runnable() {
              @Override
              public void run() {
              }
            });
              });
        }
        loadedY = this.schematicInfo.y + this.schematicInfo.height - 8 - ((schematic == null) ? 18 : 64);
      } catch (IOException ex) {
        Reference.LOGGER.error("Unable to read NBT data from schematic file", ex);
      }
    }
    addButton(new MenuButton(-1, x, loadedY, w, h, "Open Schematic Folder"), b -> {
          b.addAttribute("schematic_info_entry");
          b.onClick = () -> GuiUtils.openFolder(this.currentDirectory);
    });
  }

  public void initSchematics() {
    removeButton(b -> b.hasAttribute("schematic_entry"));
    int w = this.content.width - 56;
    int h = 22;
    int x = this.content.x + 28;
    int y = this.content.y + 5;
    Pane scissor = this.content.scale(getScaledScreen());
    try {
      if (!this.currentDirectory.getCanonicalPath().equals(ConfigurationHandler.schematicDirectory.getCanonicalPath())) {
        addButton((Button)new SchematicEntryButton(null, x, y, w, h, "..", true), b -> {
              b.addAttribute("schematic_entry");
              b.setScissorPane(scissor);
            });
        y += h + 5;
      }
    } catch (IOException ex) {
      Reference.LOGGER.error("Exception was raised while checking directory.", ex);
    }
    List<File> files = new ArrayList<>();
    File[] fileList;
    files.addAll(Arrays.asList(((fileList = this.currentDirectory.listFiles((FileFilter)FILE_FILTER_FOLDER)) == null) ? new File[0] : fileList));
    files.addAll(Arrays.asList(((fileList = this.currentDirectory.listFiles((FileFilter)FILE_FILTER_SCHEMATIC)) == null) ? new File[0] : fileList));
    for (File file : files) {
      if (file == null)
        continue;
      if ((file.isDirectory() && (fileList = file.listFiles()) == null) || fileList.length == 0)
        continue;
      String fileName = (!file.isDirectory() && SCHEMATIC_FILE_PATTERN.matcher(file.getName()).find()) ? FilenameUtils.removeExtension(file.getName()) : file.getName();
      if (file.isDirectory() ? !doesFolderMatchQuery(file) : !this.search.matchesQuery(fileName))
        continue;
      addButton((Button)new SchematicEntryButton(file, x, y, w, h, fileName, file.isDirectory()), b -> {
            b.addAttribute("schematic_entry");
            b.setScissorPane(scissor);
          });
      y += h + 5;
    }
    this.content.addScrollbarToScreen(this);
    this.content.updateMaxScroll(this, 5);
  }

  private boolean doesFolderMatchQuery(File dir) {
    File[] fileList;
    for (File schematic : ((fileList = dir.listFiles((FileFilter)FILE_FILTER_SCHEMATIC)) == null) ? new File[0] : fileList) {
      if (this.search.matchesQuery(FilenameUtils.removeExtension(schematic.getName())))
        return true;
    }
    for (File folder : ((fileList = dir.listFiles(File::isDirectory)) == null) ? new File[0] : fileList) {
      if (doesFolderMatchQuery(folder))
        return true;
    }
    return false;
  }

  private void changeDirectory(String dir) {
    this.currentDirectory = new File(this.currentDirectory, dir);
    try {
      this.currentDirectory = this.currentDirectory.getCanonicalFile();
    } catch (IOException ex) {
      Reference.LOGGER.error("Exception raised while canonizing directory", ex);
    }
    this.selected = null;
    initSchematics();
    initSchematicInfo();
  }

  private void loadSchematic() {
    if (this.selected == null)
      return;
    try {
      this.mc.displayGuiScreen(null);
      Client.sendMessage("Loading schematic " + FilenameUtils.removeExtension(this.selected.getName()), true);
      Schematica.proxy.unloadSchematic();
      Schematica.proxy.loadSchematic(null, this.currentDirectory, this.selected.getName());
    } catch (Exception ex) {
      Reference.LOGGER.error("Exception raised while loading schematic", ex);
    }
  }

  private static class SchematicDataEntry extends Button {
    private final String header;

    private final String value;

    public SchematicDataEntry(int x, int y, int width, int height, String header, String value) {
      super(-1, x, y, width, height);
      this.header = (header == null) ? null : ChatColor.translate("&l" + header);
      this.value = value;
      this.fontRenderer = Fonts.NUNITO_REGULAR_16;
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
      if (this.header != null)
        this.fontRenderer.drawString(this.header, this.x, this.y + this.height / 2 - this.fontRenderer
            .getStringHeight() / 2, 16777215);
      if (this.value != null)
        this.fontRenderer.drawString(this.value, this.x + this.width - this.fontRenderer.getStringWidth(this.value), this.y + this.height / 2 - this.fontRenderer
            .getStringHeight() / 2, 10855845);
    }
  }
}