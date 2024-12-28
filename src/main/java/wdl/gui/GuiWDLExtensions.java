package wdl.gui;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import wdl.api.IWDLModWithGui;
import wdl.api.WDLApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiWDLExtensions extends GuiScreen {
  private int bottomLocation;
  
  private static final int TOP_HEIGHT = 23;
  
  private static final int MIDDLE_HEIGHT = 19;
  
  private static final int BOTTOM_HEIGHT = 32;
  
  private int selectedModIndex = -1;
  
  private final GuiScreen parent;
  
  private ModList list;
  
  private ModDetailList detailsList;
  
  private boolean dragging;
  
  private int dragOffset;
  
  private class ModList extends GuiListExtended {
    private final List<IGuiListEntry> entries;

    public ModList() {
      super(GuiWDLExtensions.this.mc, GuiWDLExtensions.this.width, GuiWDLExtensions.this
          .bottomLocation, 23, GuiWDLExtensions.this.bottomLocation, 22);
      this.entries = new ArrayList<IGuiListEntry>() {

        };
      this.showSelectionBox = true;
    }

    private class ModEntry implements IGuiListEntry {
      public final WDLApi.ModInfo<?> mod;

      private final String modDescription;

      private String label;

      private GuiButton button;

      private final GuiButton disableButton;

      public ModEntry(WDLApi.ModInfo<?> mod) {
        this.mod = mod;
        String name = mod.getDisplayName();
        this.modDescription = I18n.format("wdl.gui.extensions.modVersion", new Object[] { name, mod.version });
        if (!mod.isEnabled()) {
          this.label = "" + EnumChatFormatting.GRAY + EnumChatFormatting.ITALIC + this.modDescription;
        } else {
          this.label = this.modDescription;
        }
        if (mod.mod instanceof IWDLModWithGui) {
          IWDLModWithGui guiMod = (IWDLModWithGui)mod.mod;
          String buttonName = guiMod.getButtonName();
          if (buttonName == null || buttonName.isEmpty())
            buttonName = I18n.format("wdl.gui.extensions.defaultSettingsButtonText", new Object[0]);
          this.button = new GuiButton(0, 0, 0, 80, 20, guiMod.getButtonName());
        }
        this.disableButton = new GuiButton(0, 0, 0, 80, 20, I18n.format("wdl.gui.extensions." + (mod.isEnabled() ? "enabled" : "disabled"), new Object[0]));
      }

      public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
        if (this.button != null) {
          this.button.xPosition = GuiWDLExtensions.this.width - 180;
          this.button.yPosition = y - 1;
          this.button.drawButton(ModList.this.mc, mouseX, mouseY);
        }
        this.disableButton.xPosition = GuiWDLExtensions.this.width - 92;
        this.disableButton.yPosition = y - 1;
        this.disableButton.drawButton(ModList.this.mc, mouseX, mouseY);
        int centerY = y + slotHeight / 2 - GuiWDLExtensions.this.fontRendererObj.FONT_HEIGHT / 2;
        GuiWDLExtensions.this.fontRendererObj.drawString(this.label, x, centerY, 16777215);
      }

      public boolean mousePressed(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
        if (this.button != null && this.button.mousePressed(ModList.this.mc, x, y)) {
          if (this.mod.mod instanceof IWDLModWithGui)
            ((IWDLModWithGui)this.mod.mod).openGui(GuiWDLExtensions.this);
          this.button.playPressSound(ModList.this.mc.getSoundHandler());
          return true;
        }
        if (this.disableButton.mousePressed(ModList.this.mc, x, y)) {
          this.mod.toggleEnabled();
          this.disableButton.playPressSound(ModList.this.mc.getSoundHandler());
          this.disableButton.displayString = I18n.format("wdl.gui.extensions." + (this.mod.isEnabled() ? "enabled" : "disabled"), new Object[0]);
          if (!this.mod.isEnabled()) {
            this.label = "" + EnumChatFormatting.GRAY + EnumChatFormatting.ITALIC + this.modDescription;
          } else {
            this.label = this.modDescription;
          }
          return true;
        }
        if (GuiWDLExtensions.this.selectedModIndex != slotIndex) {
          GuiWDLExtensions.this.selectedModIndex = slotIndex;
          //func_147674_a
          ModList.this.mc.getSoundHandler().playSound((ISound)PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
          GuiWDLExtensions.this.updateDetailsList(this.mod);
          return true;
        }
        return false;
      }

      public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
        if (this.button != null)
          this.button.mouseReleased(x, y);
      }

      public void setSelected(int slotIndex, int p_178011_2_, int p_178011_3_) {}
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.height = this.bottom = GuiWDLExtensions.this.bottomLocation;
      super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public IGuiListEntry getListEntry(int index) {
      return this.entries.get(index);
    }
    
    protected int getSize() {
      return this.entries.size();
    }
    
    protected boolean isSelected(int slotIndex) {
      return (slotIndex == GuiWDLExtensions.this.selectedModIndex);
    }
    
    public int getListWidth() {
      return GuiWDLExtensions.this.width - 20;
    }
    
    protected int getScrollBarX() {
      return GuiWDLExtensions.this.width - 10;
    }
    
    public void handleMouseInput() {
      if (this.mouseY < GuiWDLExtensions.this.bottomLocation)
        super.handleMouseInput(); 
    }
  }
  
  private class ModDetailList extends TextList {
    public ModDetailList() {
      super(GuiWDLExtensions.this.mc, GuiWDLExtensions.this.width, GuiWDLExtensions.this.height - GuiWDLExtensions.this
          .bottomLocation, 19, 32);
    }
    
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      GlStateManager.translate(0.0F, GuiWDLExtensions.this.bottomLocation, 0.0F);
      this.height = GuiWDLExtensions.this.height - GuiWDLExtensions.this.bottomLocation;
      this.bottom = this.height - 32;
      super.drawScreen(mouseX, mouseY, partialTicks);
      GuiWDLExtensions.this.drawCenteredString(GuiWDLExtensions.this.fontRendererObj, 
          I18n.format("wdl.gui.extensions.detailsCaption", new Object[0]), GuiWDLExtensions.this.width / 2, 5, 16777215);
      GlStateManager.translate(0.0F, -GuiWDLExtensions.this.bottomLocation, 0.0F);
    }
    
    protected void overlayBackground(int y1, int y2, int alpha1, int alpha2) {
      if (y1 == 0) {
        super.overlayBackground(y1, y2, alpha1, alpha2);
        return;
      } 
      GlStateManager.translate(0.0F, -GuiWDLExtensions.this.bottomLocation, 0.0F);
      super.overlayBackground(y1 + GuiWDLExtensions.this.bottomLocation, y2 + GuiWDLExtensions.this
          .bottomLocation, alpha1, alpha2);
      GlStateManager.translate(0.0F, GuiWDLExtensions.this.bottomLocation, 0.0F);
    }
    
    public void handleMouseInput() {
      this.mouseY -= GuiWDLExtensions.this.bottomLocation;
      if (this.mouseY > 0)
        super.handleMouseInput(); 
      this.mouseY += GuiWDLExtensions.this.bottomLocation;
    }
  }
  
  private void updateDetailsList(WDLApi.ModInfo<?> selectedMod) {
    this.detailsList.clearLines();
    if (selectedMod != null) {
      String info = selectedMod.getInfo();
      this.detailsList.addLine(info);
    } 
  }
  
  public void initGui() {
    this.bottomLocation = this.height - 100;
    this.dragging = false;
    this.list = new ModList();
    this.detailsList = new ModDetailList();
    this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height - 29, 
          I18n.format("gui.done", new Object[0])));
  }
  
  protected void actionPerformed(GuiButton button) throws IOException {
    if (button.id == 0)
      this.mc.displayGuiScreen(this.parent); 
  }
  
  public GuiWDLExtensions(GuiScreen parent) {
    this.dragging = false;
    this.parent = parent;
  }
  
  public void handleMouseInput() throws IOException {
    super.handleMouseInput();
    this.list.handleMouseInput();
    this.detailsList.handleMouseInput();
  }
  
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    if (mouseY > this.bottomLocation && mouseY < this.bottomLocation + 19) {
      this.dragging = true;
      this.dragOffset = mouseY - this.bottomLocation;
      return;
    } 
    if (this.list.mouseClicked(mouseX, mouseY, mouseButton))
      return; 
    if (this.detailsList.mouseClicked(mouseX, mouseY, mouseButton))
      return; 
    super.mouseClicked(mouseX, mouseY, mouseButton);
  }
  
  protected void mouseMovedOrUp(int mouseX, int mouseY, int state) {
    this.dragging = false;
    if (this.list.mouseReleased(mouseX, mouseY, state))
      return; 
    if (this.detailsList.mouseReleased(mouseX, mouseY, state))
      return;
    super.mouseClickMove(mouseX, mouseY, state, 0);
//    super.mouseMovedOrUp(mouseX, mouseY, state);
  }
  
  protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
    if (this.dragging)
      this.bottomLocation = mouseY - this.dragOffset; 
    if (this.bottomLocation < 31)
      this.bottomLocation = 31; 
    if (this.bottomLocation > this.height - 32 - 8)
      this.bottomLocation = this.height - 32 - 8; 
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    if (this.bottomLocation < 56)
      this.bottomLocation = 56; 
    if (this.bottomLocation > this.height - 19 - 32 - 33)
      this.bottomLocation = this.height - 19 - 32 - 33; 
    this.list.drawScreen(mouseX, mouseY, partialTicks);
    this.detailsList.drawScreen(mouseX, mouseY, partialTicks);
    drawCenteredString(this.fontRendererObj, 
        I18n.format("wdl.gui.extensions.title", new Object[0]), this.width / 2, 8, 16777215);
    super.drawScreen(mouseX, mouseY, partialTicks);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\gui\GuiWDLExtensions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */