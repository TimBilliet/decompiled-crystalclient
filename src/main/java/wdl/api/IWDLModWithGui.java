package wdl.api;

import net.minecraft.client.gui.GuiScreen;

public interface IWDLModWithGui extends IWDLMod {
  String getButtonName();
  
  void openGui(GuiScreen paramGuiScreen);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\api\IWDLModWithGui.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */