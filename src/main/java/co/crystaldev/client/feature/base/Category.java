package co.crystaldev.client.feature.base;

public enum Category {
    ALL("All"),
    HUD("HUD"),
    COMBAT("Combat"),
    FACTIONS("Factions"),
    MECHANIC("Mechanic");

    Category(String formattedName) {
        this.formattedName = formattedName;
    }

    private final String formattedName;

    public String getFormattedName() {
        return this.formattedName;
    }

    public String toString() {
        return this.formattedName;
    }
}
