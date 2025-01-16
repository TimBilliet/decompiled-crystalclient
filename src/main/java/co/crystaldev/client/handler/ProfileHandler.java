package co.crystaldev.client.handler;

import co.crystaldev.client.Client;
import co.crystaldev.client.Reference;
import co.crystaldev.client.event.SubscribeEvent;
import co.crystaldev.client.event.impl.init.InitializationEvent;
import co.crystaldev.client.event.impl.init.ShutdownEvent;
import co.crystaldev.client.event.impl.network.ServerConnectEvent;
import co.crystaldev.client.event.impl.network.ServerDisconnectEvent;
import co.crystaldev.client.util.FileUtils;
import co.crystaldev.client.util.objects.profiles.Profile;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.util.UUIDTypeAdapter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class ProfileHandler {
    private static final File profileFile = new File(Client.getClientRunDirectory(), "profiles.json");

    private static ProfileHandler INSTANCE;

    private Profile DEFAULT_PROFILE;

    private final ArrayList<Profile> profiles = new ArrayList<>();

    public ArrayList<Profile> getProfiles() {
        return this.profiles;
    }

    private Profile selectedProfile = null;

    public Profile getSelectedProfile() {
        return this.selectedProfile;
    }

    private Profile lastProfile = null;

    public ProfileHandler() {
        INSTANCE = this;
    }

    public Profile createNewProfile(String name, boolean autoUseOnServer) {
        Profile profile = new Profile(name);
        swapToProfile(profile);
        this.profiles.add(this.selectedProfile);
        if (autoUseOnServer)
            this.selectedProfile.setAutoUseServer(Client.formatConnectedServerIp());
        return profile;
    }

    public void swapToProfile(Profile profile) {
        if (profile == null)
            profile = this.DEFAULT_PROFILE;
        if (this.selectedProfile != null)
            this.selectedProfile.saveCurrentModConfiguration();
        this.selectedProfile = profile;
        if (profile != null)
            profile.loadCurrentModConfiguration();
    }

    public void swapToProfile(UUID uuid) {
        for (Profile profile : this.profiles) {
            if (profile.getId().equals(uuid))
                swapToProfile(profile);
        }
    }

    public void unsetLastProfile() {
        if (this.lastProfile != null) {
            this.lastProfile.saveCurrentModConfiguration();
            this.lastProfile = null;
        }
    }

    public void removeProfile(Profile profile) {
        if (this.selectedProfile.equals(profile)) {
            if (this.lastProfile != null && !this.lastProfile.equals(this.selectedProfile)) {
                swapToProfile(this.lastProfile);
            } else {
                swapToProfile((Profile) null);
            }
            this.lastProfile = null;
        }
        this.profiles.remove(profile);
    }

    @SubscribeEvent
    public void onInitialization(InitializationEvent event) {
        populateProfiles();
    }

    @SubscribeEvent(priority = 0)
    public void onShutdown(ShutdownEvent event) {
        if (this.lastProfile != null)
            swapToProfile(this.lastProfile);
        if (this.selectedProfile != null)
            this.selectedProfile.saveCurrentModConfiguration();
        saveProfiles();
    }

    @SubscribeEvent(priority = 5)
    public void onServerConnection(ServerConnectEvent event) {
        for (Profile profile : this.profiles) {
            if (profile.getAutoUseServer() != null && Objects.equals(Client.formatConnectedServerIp(), profile.getAutoUseServer()) &&
                    !profile.equals(this.selectedProfile)) {
                this.lastProfile = this.selectedProfile;
                swapToProfile(profile);
        NotificationHandler.addNotification(String.format("Profile '%s' was automatically selected.", profile.getName()));
                break;
            }
        }
    }

    @SubscribeEvent
    public void onServerDisconnect(ServerDisconnectEvent event) {
        if (this.lastProfile != null) {
            swapToProfile(this.lastProfile);
            this.lastProfile = null;
        }
    }

    private void populateProfiles() {
        this.profiles.clear();
        if (profileFile.exists())
            try {
                FileReader fr = new FileReader(profileFile);
                JsonObject obj = (JsonObject) Reference.GSON_PRETTY.fromJson(fr, JsonObject.class);
                if (obj.has("available_profiles"))
                    this.profiles.addAll(Reference.GSON.fromJson((JsonElement) obj.getAsJsonArray("available_profiles"), (new TypeToken<ArrayList<Profile>>() {

                    }).getType()));
                if (obj.has("default_profile"))
                    this.DEFAULT_PROFILE = (Profile) Reference.GSON.fromJson(obj.get("default_profile"), Profile.class);
                if (obj.has("selected_profile")) {
                    UUID id = UUIDTypeAdapter.fromString(obj.get("selected_profile").getAsString());
                    swapToProfile(id);
                }
                fr.close();
            } catch (Exception ex) {
                Reference.LOGGER.error("An exception was thrown while populating profiles.", ex);
            }
        if (this.DEFAULT_PROFILE == null)
            this.DEFAULT_PROFILE = new Profile("__default");
        if (this.selectedProfile == null)
            swapToProfile(this.DEFAULT_PROFILE);
    }

    private void saveProfiles() {
        JsonObject obj = new JsonObject();
        if (this.selectedProfile != null)
            obj.addProperty("selected_profile", UUIDTypeAdapter.fromUUID(this.selectedProfile.getId()));
        obj.add("available_profiles", (JsonElement) Reference.GSON.fromJson(Reference.GSON.toJson(this.profiles), JsonArray.class));
        obj.add("default_profile", (JsonElement) Reference.GSON.fromJson(Reference.GSON.toJson(this.DEFAULT_PROFILE), JsonObject.class));
        if (!this.profiles.isEmpty()) {
            String json = null;
            while (json == null || !FileUtils.isValidJson(json))
                json = Reference.GSON.toJson((JsonElement) obj);
            try {
                FileWriter fileWriter = new FileWriter(profileFile);
                fileWriter.write(json);
                fileWriter.close();
            } catch (IOException ex) {
                Reference.LOGGER.error("An exception was raised while saving to the profiles file.", ex);
            }
        } else if (profileFile.exists()) {
            profileFile.delete();
        }
    }

    public static ProfileHandler getInstance() {
        return INSTANCE;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\handler\ProfileHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */