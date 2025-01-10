package co.crystaldev.client.gui.screens.override.multiplayer;

import co.crystaldev.client.network.socket.shared.PacketServerList;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiMultiplayerExt extends GuiScreen {
  private final OldServerPinger oldServerPinger = new OldServerPinger();

  private final GuiScreen parentScreen;

  private ServerSelectionListExt serverListSelector;

  private ServerListExt savedServerList;

  private GuiButton btnEditServer;

  private GuiButton btnSelectServer;

  private GuiButton btnDeleteServer;

  private boolean deletingServer;

  private boolean addingServer;

  private boolean editingServer;

  private boolean directConnect;

  private String hoveringText;

  private ServerData selectedServer;

  private boolean initialized;

  public GuiMultiplayerExt(GuiScreen parentScreen) {
    this.parentScreen = parentScreen;
  }

  public void initGui() {
    Keyboard.enableRepeatEvents(true);
    this.buttonList.clear();
    if (!this.initialized) {
      this.initialized = true;
      this.savedServerList = new ServerListExt(this.mc);
      this.savedServerList.loadServerList();
      this.serverListSelector = new ServerSelectionListExt(this, this.mc, this.width, this.height, 32, this.height - 64, 36);
      this.serverListSelector.func_148195_a(this.savedServerList);
    } else {
      this.serverListSelector.setDimensions(this.width, this.height, 32, this.height - 64);
    }
    createButtons();
  }

  public void handleMouseInput() throws IOException {
    super.handleMouseInput();
    this.serverListSelector.handleMouseInput();
  }

  public void createButtons() {
    this.buttonList.add(this.btnEditServer = new GuiButton(7, this.width / 2 - 154, this.height - 28, 70, 20, I18n.format("selectServer.edit")));
    this.buttonList.add(this.btnDeleteServer = new GuiButton(2, this.width / 2 - 74, this.height - 28, 70, 20, I18n.format("selectServer.delete")));
    this.buttonList.add(this.btnSelectServer = new GuiButton(1, this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("selectServer.select")));
    this.buttonList.add(new GuiButton(4, this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("selectServer.direct")));
    this.buttonList.add(new GuiButton(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.format("selectServer.add")));
    this.buttonList.add(new GuiButton(8, this.width / 2 + 4, this.height - 28, 70, 20, I18n.format("selectServer.refresh")));
    this.buttonList.add(new GuiButton(0, this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.format("gui.cancel")));
    selectServer(this.serverListSelector.func_148193_k());
  }

  public void updateScreen() {
    super.updateScreen();
    this.oldServerPinger.pingPendingNetworks();
  }

  public void onGuiClosed() {
    Keyboard.enableRepeatEvents(false);
    this.oldServerPinger.clearPendingNetworks();
  }

  protected void actionPerformed(GuiButton button) throws IOException {
    if (button.enabled) {
      GuiListExtended.IGuiListEntry guilistextended$iguilistentry = (this.serverListSelector.func_148193_k() < 0) ? null : this.serverListSelector.getListEntry(this.serverListSelector.func_148193_k());
      if (button.id == 2 && guilistextended$iguilistentry instanceof ServerListEntryNormalExt) {
        String s4 = (((ServerListEntryNormalExt)guilistextended$iguilistentry).getServerData()).serverName;
        if (s4 != null) {
          this.deletingServer = true;
          String s = I18n.format("selectServer.deleteQuestion");
          String s1 = "'" + s4 + "' " + I18n.format("selectServer.deleteWarning");
          String s2 = I18n.format("selectServer.deleteButton");
          String s3 = I18n.format("gui.cancel");
          GuiYesNo guiyesno = new GuiYesNo((GuiYesNoCallback)this, s, s1, s2, s3, this.serverListSelector.func_148193_k());
          this.mc.displayGuiScreen((GuiScreen)guiyesno);
        }
      } else if (button.id == 1) {
        connectToSelected();
      } else if (button.id == 4) {
        this.directConnect = true;
        this.mc.displayGuiScreen((GuiScreen)new GuiScreenServerList(this, this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false)));
      } else if (button.id == 3) {
        this.addingServer = true;
        this.mc.displayGuiScreen((GuiScreen)new GuiScreenAddServer(this, this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false)));
      } else if (button.id == 7 && guilistextended$iguilistentry instanceof ServerListEntryNormalExt) {
        this.editingServer = true;
        ServerData serverdata = ((ServerListEntryNormalExt)guilistextended$iguilistentry).getServerData();
        this.selectedServer = new ServerData(serverdata.serverName, serverdata.serverIP, false);
        this.selectedServer.copyFrom(serverdata);
        this.mc.displayGuiScreen((GuiScreen)new GuiScreenAddServer(this, this.selectedServer));
      } else if (button.id == 0) {
        this.mc.displayGuiScreen(this.parentScreen);
      } else if (button.id == 8) {
        refreshServerList();
      }
    }
  }

  private void refreshServerList() {
    this.mc.displayGuiScreen(new GuiMultiplayerExt(this.parentScreen));
  }

  public void confirmClicked(boolean result, int id) {
    GuiListExtended.IGuiListEntry guilistextended$iguilistentry = (this.serverListSelector.func_148193_k() < 0) ? null : this.serverListSelector.getListEntry(this.serverListSelector.func_148193_k());
    if (this.deletingServer) {
      this.deletingServer = false;
      if (result && guilistextended$iguilistentry instanceof ServerListEntryNormalExt) {
        this.savedServerList.removeServerData(this.serverListSelector.func_148193_k());
        this.savedServerList.saveServerList();
        this.serverListSelector.setSelectedSlotIndex(-1);
        this.serverListSelector.func_148195_a(this.savedServerList);
      }
      this.mc.displayGuiScreen(this);
    } else if (this.directConnect) {
      this.directConnect = false;
      if (result) {
        connectToServer(this.selectedServer);
      } else {
        this.mc.displayGuiScreen(this);
      }
    } else if (this.addingServer) {
      this.addingServer = false;
      if (result) {
        this.savedServerList.addServerData(this.selectedServer);
        this.savedServerList.saveServerList();
        this.serverListSelector.setSelectedSlotIndex(-1);
        this.serverListSelector.func_148195_a(this.savedServerList);
      }
      this.mc.displayGuiScreen(this);
    } else if (this.editingServer) {
      this.editingServer = false;
      if (result && guilistextended$iguilistentry instanceof ServerListEntryNormalExt) {
        ServerData serverdata = ((ServerListEntryNormalExt)guilistextended$iguilistentry).getServerData();
        serverdata.serverName = this.selectedServer.serverName;
        serverdata.serverIP = this.selectedServer.serverIP;
        serverdata.copyFrom(this.selectedServer);
        this.savedServerList.saveServerList();
        this.serverListSelector.func_148195_a(this.savedServerList);
      }
      this.mc.displayGuiScreen(this);
    }
  }

  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    int i = this.serverListSelector.func_148193_k();
    GuiListExtended.IGuiListEntry guilistextended$iguilistentry = (i < 0) ? null : this.serverListSelector.getListEntry(i);
    if (keyCode == 63) {
      refreshServerList();
    } else if (i >= 0) {
      if (keyCode == 200) {
        if (isShiftKeyDown()) {
          if (i > 0 && guilistextended$iguilistentry instanceof ServerListEntryNormalExt) {
            this.savedServerList.swapServers(i, i - 1);
            selectServer(this.serverListSelector.func_148193_k() - 1);
            this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
            this.serverListSelector.func_148195_a(this.savedServerList);
          }
        } else if (i > 0) {
          selectServer(this.serverListSelector.func_148193_k() - 1);
          this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
        } else {
          selectServer(-1);
        }
      } else if (keyCode == 208) {
        if (isShiftKeyDown()) {
          if (i < this.savedServerList.countServers() - 1) {
            this.savedServerList.swapServers(i, i + 1);
            selectServer(i + 1);
            this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
            this.serverListSelector.func_148195_a(this.savedServerList);
          }
        } else if (i < this.serverListSelector.getSize() - 1) {
          selectServer(this.serverListSelector.func_148193_k() + 1);
          this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
        } else {
          selectServer(-1);
        }
      } else if (keyCode != 28 && keyCode != 156) {
        super.keyTyped(typedChar, keyCode);
      } else {
        actionPerformed(this.buttonList.get(2));
      }
    } else {
      super.keyTyped(typedChar, keyCode);
    }
  }

  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    this.hoveringText = null;
    drawDefaultBackground();
    this.serverListSelector.drawScreen(mouseX, mouseY, partialTicks);
    drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.title"), this.width / 2, 20, 16777215);
    super.drawScreen(mouseX, mouseY, partialTicks);
    if (this.hoveringText != null)
      drawHoveringText(Lists.newArrayList(Splitter.on("\n").split(this.hoveringText)), mouseX, mouseY);
  }

  public void connectToSelected() {
    GuiListExtended.IGuiListEntry guilistextended$iguilistentry = (this.serverListSelector.func_148193_k() < 0) ? null : this.serverListSelector.getListEntry(this.serverListSelector.func_148193_k());
    if (guilistextended$iguilistentry instanceof ServerListEntryNormalExt)
      connectToServer(((ServerListEntryNormalExt)guilistextended$iguilistentry).getServerData());
  }

  private void connectToServer(ServerData server) {
    if (this.mc.theWorld != null)
      this.mc.theWorld.sendQuittingDisconnectingPacket();
    this.mc.displayGuiScreen((GuiScreen)new GuiConnecting(this, this.mc, server));
  }

  public void selectServer(int index) {
    this.serverListSelector.setSelectedSlotIndex(index);
    GuiListExtended.IGuiListEntry guilistextended$iguilistentry = (index < 0) ? null : this.serverListSelector.getListEntry(index);
    this.btnSelectServer.enabled = false;
    this.btnEditServer.enabled = false;
    this.btnDeleteServer.enabled = false;
    if (guilistextended$iguilistentry != null) {
      this.btnSelectServer.enabled = true;
      if (guilistextended$iguilistentry instanceof ServerListEntryNormalExt) {
        boolean flag = !(((ServerListEntryNormalExt)guilistextended$iguilistentry).getServerData() instanceof co.crystaldev.client.util.objects.partners.PartneredServerData);
        this.btnEditServer.enabled = flag;
        this.btnDeleteServer.enabled = flag;
      }
    }
  }

  public OldServerPinger getOldServerPinger() {
    return this.oldServerPinger;
  }

  public void setHoveringText(String p_146793_1_) {
    this.hoveringText = p_146793_1_;
  }

  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    super.mouseClicked(mouseX, mouseY, mouseButton);
    this.serverListSelector.mouseClicked(mouseX, mouseY, mouseButton);
  }

  protected void mouseReleased(int mouseX, int mouseY, int state) {
    super.mouseReleased(mouseX, mouseY, state);
    this.serverListSelector.mouseReleased(mouseX, mouseY, state);
  }

  public ServerListExt getServerList() {
    return this.savedServerList;
  }

  public boolean canMoveUp(ServerListEntryNormalExt p_175392_1_, int p_175392_2_) {
    if (p_175392_1_.getServerData() instanceof co.crystaldev.client.util.objects.partners.PartneredServerData)
      return false;
    return (p_175392_2_ > PacketServerList.partneredServers.size());
  }

  public boolean canMoveDown(ServerListEntryNormalExt p_175394_1_, int p_175394_2_) {
    if (p_175394_1_.getServerData() instanceof co.crystaldev.client.util.objects.partners.PartneredServerData)
      return false;
    return (p_175394_2_ < this.savedServerList.countServers() - 1);
  }

  public void moveEntryUp(ServerListEntryNormalExt p_175391_1_, int p_175391_2_, boolean p_175391_3_) {
    if (p_175391_1_.getServerData() instanceof co.crystaldev.client.util.objects.partners.PartneredServerData)
      return;
    int i = p_175391_3_ ? 0 : (p_175391_2_ - 1);
    this.savedServerList.swapServers(p_175391_2_, i);
    if (this.serverListSelector.func_148193_k() == p_175391_2_)
      selectServer(i);
    this.serverListSelector.func_148195_a(this.savedServerList);
  }

  public void moveEntryDown(ServerListEntryNormalExt p_175393_1_, int p_175393_2_, boolean p_175393_3_) {
    if (p_175393_1_.getServerData() instanceof co.crystaldev.client.util.objects.partners.PartneredServerData)
      return;
    int i = p_175393_3_ ? (this.savedServerList.countServers() - 1) : (p_175393_2_ + 1);
    this.savedServerList.swapServers(p_175393_2_, i);
    if (this.serverListSelector.func_148193_k() == p_175393_2_)
      selectServer(i);
    this.serverListSelector.func_148195_a(this.savedServerList);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\override\multiplayer\GuiMultiplayerExt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */