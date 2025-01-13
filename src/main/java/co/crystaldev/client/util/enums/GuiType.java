package co.crystaldev.client.util.enums;

public enum GuiType {
    MODULES("Mods"),
    AUTO_TEXT("Auto Text"),
    GROUPS("Groups"),
    WAYPOINTS("Waypoints"),
    PROFILES("Profiles"),
    COSMETICS("Cosmetics");

    GuiType(String displayText) {
        this.displayText = displayText;
    }

    private final String displayText;

    public String getDisplayText() {
        return this.displayText;
    }

    public String toString() {
        return this.displayText;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\enums\GuiType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */