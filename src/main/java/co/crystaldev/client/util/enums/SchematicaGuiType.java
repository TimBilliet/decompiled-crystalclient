package co.crystaldev.client.util.enums;

public enum SchematicaGuiType {
    LOAD_SCHEMATIC("Load"),
    CONTROL_SCHEMATIC("Control"),
    SAVE_SCHEMATIC("Save");

    SchematicaGuiType(String displayText) {
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

