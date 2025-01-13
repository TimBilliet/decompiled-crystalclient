package co.crystaldev.client.group.objects.enums;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public enum Rank {
    LEADER("LEADER", "Leader"),
    ADMIN("ADMIN", "Admin"),
    MODERATOR("MODERATOR", "Moderator"),
    MEMBER("MEMBER", "Member");

    Rank(String serializedName, String displayText) {
        this.serializedName = serializedName;
        this.displayText = displayText;
    }

    private final String serializedName;

    private final String displayText;

    public String getSerializedName() {
        return this.serializedName;
    }

    public String getDisplayText() {
        return this.displayText;
    }

    public Rank promote() {
        if (this == LEADER || this == ADMIN)
            return null;
        return values()[ordinal() - 1];
    }

    public Rank demote() {
        if (this == MEMBER)
            return null;
        return (this == LEADER) ? LEADER : values()[ordinal() + 1];
    }

    public String toString() {
        return this.serializedName;
    }

    public static Rank fromString(String name) {
        for (Rank rank : values()) {
            if (rank.getSerializedName().equalsIgnoreCase(name))
                return rank;
        }
        return null;
    }

    public static class Adapter extends TypeAdapter<Rank> {
        public void write(JsonWriter out, Rank value) throws IOException {
            out.value(value.toString());
        }

        public Rank read(JsonReader in) throws IOException {
            String value = in.nextString();
            for (Rank rank : Rank.values()) {
                if (rank.toString().equalsIgnoreCase(value))
                    return rank;
            }
            return null;
        }
    }
}
