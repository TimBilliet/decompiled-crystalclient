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
