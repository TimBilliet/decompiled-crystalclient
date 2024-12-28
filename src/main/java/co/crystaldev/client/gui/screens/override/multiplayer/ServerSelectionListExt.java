package co.crystaldev.client.gui.screens.override.multiplayer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class ServerSelectionListExt extends GuiListExtended {
  private final GuiMultiplayerExt owner;

  private final List<ServerListEntryNormalExt> serverListInternet = new ArrayList<>();

  private int selectedSlotIndex = -1;

  public ServerSelectionListExt(GuiMultiplayerExt ownerIn, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
    super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
    this.owner = ownerIn;
  }

  public IGuiListEntry getListEntry(int index) {
    int size = this.serverListInternet.size();
    return this.serverListInternet.get(MathHelper.clamp_int(index, 0, size - 1));
  }

  protected int getSize() {
    return this.serverListInternet.size();
  }

  public void setSelectedSlotIndex(int selectedSlotIndexIn) {
    this.selectedSlotIndex = selectedSlotIndexIn;
  }

  protected boolean isSelected(int slotIndex) {
    return (slotIndex == this.selectedSlotIndex);
  }

  public int func_148193_k() {
    return this.selectedSlotIndex;
  }

  public void func_148195_a(ServerListExt p_148195_1_) {
    this.serverListInternet.clear();
    for (int i = 0; i < p_148195_1_.countServers(); i++)
      this.serverListInternet.add(new ServerListEntryNormalExt(this.owner, p_148195_1_.getServerData(i)));
  }

  protected int getScrollBarX() {
    return super.getScrollBarX() + 30;
  }

  public int getListWidth() {
    return super.getListWidth() + 85;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\override\multiplayer\ServerSelectionListExt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */