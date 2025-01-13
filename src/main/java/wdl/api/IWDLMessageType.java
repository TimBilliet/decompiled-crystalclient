package wdl.api;

import net.minecraft.util.EnumChatFormatting;

public interface IWDLMessageType {
    EnumChatFormatting getTitleColor();

    EnumChatFormatting getTextColor();

    String getDisplayName();

    String getDescription();

    boolean isEnabledByDefault();
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\api\IWDLMessageType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */