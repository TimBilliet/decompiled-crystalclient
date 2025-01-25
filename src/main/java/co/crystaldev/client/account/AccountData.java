package co.crystaldev.client.account;

import com.mojang.util.UUIDTypeAdapter;

import java.util.UUID;

public class AccountData {
    private final String accessToken;

    private final String name;

    private final UUID id;

    private final String unformattedId;

    private final boolean isOffline;

    public String toString() {
        return "AccountData(accessToken=" + getAccessToken() + ", name=" + getName() + ", id=" + getId() + ", unformattedId=" + getUnformattedId() + ")";
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getName() {
        return this.name;
    }

    public UUID getId() {
        return this.id;
    }

    public String getUnformattedId() {
        return this.unformattedId;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public AccountData(String accessToken, String name, String id) {
        this.name = name;
        this.id = UUIDTypeAdapter.fromString(this.unformattedId = id);
        this.accessToken = accessToken;
        isOffline = accessToken == null || accessToken.equals("X");
    }

    public boolean equals(Object other) {
        if (other instanceof AccountData) {
            AccountData acc = (AccountData) other;
            if (isOffline && acc.isOffline)
                return name.equals(acc.name) && id.equals(acc.id);
            else
                return (this.accessToken.equals(acc.accessToken) && id.equals(acc.id));
        }
        return false;
    }
}