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
