package co.crystaldev.client.util.enums;

public enum EnumActionShift {
    ADD("ADD"),
    REMOVE("REMOVE");

    EnumActionShift(String serializationString) {
        this.serializationString = serializationString;
    }

    private final String serializationString;

    public String getSerializationString() {
        return this.serializationString;
    }

    public String toString() {
        return this.serializationString;
    }

    public static EnumActionShift fromString(String name) {
        for (EnumActionShift action : values()) {
            if (action.getSerializationString().equalsIgnoreCase(name))
                return action;
        }
        return null;
    }
}
