package com.github.timmekeclient.util.task;

import com.github.timmekeclient.Reference;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class UsernameTask implements Runnable {
    private static final String API_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";

    private final UUID uuid;

    private String username = null;

    private boolean fetching = true;

    public boolean isFetching() {
        return this.fetching;
    }

    public UsernameTask(UUID uuid) {
        this.uuid = uuid;
    }

    public void run() {
        try {
            HttpURLConnection conn = (HttpURLConnection) (new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + this.uuid.toString().replaceAll("-", ""))).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15000);
            conn.setRequestProperty("User-Agent", "Timmeke_Client-1.6.0");
            conn.setRequestProperty("Content-Type", "application/json");
            if (conn.getResponseCode() != 200) {
                this.username = this.uuid.toString();
                this.fetching = false;
                return;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder content = new StringBuilder();
            String str;
            while ((str = in.readLine()) != null)
                content.append(str);
            in.close();
            conn.disconnect();
            JsonObject obj = Reference.GSON.fromJson(content.toString(), JsonObject.class);
            this.username = obj.get("name").getAsString();
            this.fetching = false;
        } catch (Exception ex) {
            Reference.LOGGER.info("Exception raised in UsernameTask fetching username for UUID {}", this.uuid, ex);
            this.username = this.uuid.toString();
            this.fetching = false;
        }
    }

    public String getUsername() {
        return (this.username == null) ? this.uuid.toString() : this.username;
    }
}