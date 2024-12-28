package wdl;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import wdl.api.IWDLMessageType;

public enum WDLMessageTypes implements IWDLMessageType {
  INFO("wdl.messages.message.info", EnumChatFormatting.RED, EnumChatFormatting.GOLD, true, MessageTypeCategory.CORE_RECOMMENDED),
  ERROR("wdl.messages.message.error", EnumChatFormatting.DARK_GREEN, EnumChatFormatting.DARK_RED, true, MessageTypeCategory.CORE_RECOMMENDED),
  UPDATES("wdl.messages.message.updates", EnumChatFormatting.RED, EnumChatFormatting.GOLD, true, MessageTypeCategory.CORE_RECOMMENDED),
  LOAD_TILE_ENTITY("wdl.messages.message.loadingTileEntity", false),
  ON_WORLD_LOAD("wdl.messages.message.onWorldLoad", false),
  ON_BLOCK_EVENT("wdl.messages.message.blockEvent", true),
  ON_MAP_SAVED("wdl.messages.message.mapDataSaved", false),
  ON_CHUNK_NO_LONGER_NEEDED("wdl.messages.message.chunkUnloaded", false),
  ON_GUI_CLOSED_INFO("wdl.messages.message.guiClosedInfo", true),
  ON_GUI_CLOSED_WARNING("wdl.messages.message.guiClosedWarning", true),
  SAVING("wdl.messages.message.saving", true),
  REMOVE_ENTITY("wdl.messages.message.removeEntity", false),
  PLUGIN_CHANNEL_MESSAGE("wdl.messages.message.pluginChannel", false),
  UPDATE_DEBUG("wdl.messages.message.updateDebug", false);
  
  private final String displayTextKey;
  
  private final EnumChatFormatting titleColor;
  
  private final EnumChatFormatting textColor;
  
  private final String descriptionKey;
  
  private final boolean enabledByDefault;
  
  WDLMessageTypes(String i18nKey, EnumChatFormatting titleColor, EnumChatFormatting textColor, boolean enabledByDefault, MessageTypeCategory category) {
    this.displayTextKey = i18nKey + ".text";
    this.titleColor = titleColor;
    this.textColor = textColor;
    this.descriptionKey = i18nKey + ".description";
    this.enabledByDefault = enabledByDefault;
    WDLMessages.registerMessage(name(), this, category);
  }

  WDLMessageTypes(String i18nKey, boolean enabledByDefault) {
    this.displayTextKey = i18nKey + ".text";
    this.titleColor = null;
    this.textColor = null;
    this.descriptionKey = i18nKey + ".description";
    this.enabledByDefault = enabledByDefault;
  }

  public String getDisplayName() {
    return I18n.format(this.displayTextKey, new Object[0]);
  }
  
  public EnumChatFormatting getTitleColor() {
    return this.titleColor;
  }
  
  public EnumChatFormatting getTextColor() {
    return this.textColor;
  }
  
  public String getDescription() {
    return I18n.format(this.descriptionKey, new Object[0]);
  }
  
  public boolean isEnabledByDefault() {
    return this.enabledByDefault;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\WDLMessageTypes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */