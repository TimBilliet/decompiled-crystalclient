package com.github.timmekeclient.account;

import com.mojang.util.UUIDTypeAdapter;

import java.util.Objects;
import java.util.UUID;

public class AccountData {
    private String mcAccessToken;

    private String refreshToken;

    private String name;

    private UUID uuid;

    private String uuidString;

    private boolean isOffline;


    @Override
    public String toString() {
        return "AccountData{" +
                "accessToken='" + mcAccessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", name='" + name + '\'' +
                ", uuid=" + uuid +
                ", uuidString='" + uuidString + '\'' +
                ", isOffline=" + isOffline +
                '}';
    }

    public String getMcAccessToken() {
        return this.mcAccessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getName() {
        return this.name;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getUuidString() {
        return this.uuidString;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
        uuidString = UUIDTypeAdapter.fromUUID(uuid);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMcAccessToken(String mcAccessToken) {
        this.mcAccessToken = mcAccessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public AccountData(String name, String uuidString) {
        this(null, null, name, uuidString);
        isOffline = true;
    }

    public AccountData(String mcAccessToken, String refreshToken, String name, String uuidString) {
        this.name = name;
        this.uuid = UUIDTypeAdapter.fromString(this.uuidString = uuidString);
        this.mcAccessToken = mcAccessToken;
        this.refreshToken = refreshToken;
        isOffline = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountData that = (AccountData) o;
        return isOffline == that.isOffline && name.equals(that.name) && uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, uuid, isOffline);
    }
}