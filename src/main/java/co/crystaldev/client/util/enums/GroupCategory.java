package co.crystaldev.client.util.enums;

public enum GroupCategory {
  LANDING("Landing"),
  SETTINGS("Settings"),
  SCHEMATICS("Schematics"),
  USERS("Users"),
  RANKS("Ranks");
  
  private final String translationKey;
  
  public String getTranslationKey() {
    return this.translationKey;
  }
  
  public String toString() {
    return this.translationKey;
  }
  
  GroupCategory(String translationKey) {
    this.translationKey = translationKey;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\enums\GroupCategory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */