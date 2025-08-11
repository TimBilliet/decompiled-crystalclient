package com.github.timmekeclient.account;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.Reference;
import com.github.timmekeclient.event.impl.init.SessionUpdateEvent;
import com.github.timmekeclient.mixin.accessor.net.minecraft.client.MixinMinecraft;
import com.github.timmekeclient.util.FileUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class AltManager {
    private static AltManager INSTANCE;

    private final Minecraft mc = Minecraft.getMinecraft();

    private static AccountData currentAccount;

    private static LinkedList<AccountData> accounts;

    public static AccountData getCurrentAccount() {
        return currentAccount;
    }

    public static LinkedList<AccountData> getAccounts() {
        return accounts;
    }

    public AltManager() {
        accounts = new LinkedList<>();
        INSTANCE = this;
        populateAltManager();
    }

    public boolean contains(AccountData account) {
        for (AccountData acc : accounts) {
            if (acc.equals(account))
                return true;
        }
        return false;
    }

    public boolean contains(UUID uuid) {
        for (AccountData acc : accounts) {
            if (acc.getUuid().equals(uuid))
                return true;
        }
        return false;
    }

    public void addAccount(AccountData account) {
        if (account == null)
            return;
        if (currentAccount != null && currentAccount.getUuid().equals(account.getUuid()))
            currentAccount = null;
        accounts.removeIf(a -> a.getUuid().equals(account.getUuid()));
        accounts.add(account);
        setAccount(account);
        saveAltManager();
    }

    public void removeAccount(AccountData account) {
        if (account == null)
            return;
        if (currentAccount != null && currentAccount.getUuid().equals(account.getUuid()))
            currentAccount = null;
        accounts.removeIf(a -> a.getUuid().equals(account.getUuid()));
        saveAltManager();
    }

    public void populateAltManager() {
        File file = getAltManagerFile();
        if (file.exists())
            try {
                FileReader fr = new FileReader(file);
                JsonObject obj = Reference.GSON_PRETTY.fromJson(fr, JsonObject.class);
                fr.close();
                if (obj.has("accounts")) {
                    JsonObject accounts = obj.get("accounts").getAsJsonObject();
                    for (Map.Entry<String, JsonElement> entry : accounts.entrySet()) {
                        String uuid = entry.getKey();
                        JsonObject account = accounts.get(uuid).getAsJsonObject();
                        if (account.get("access_token") == null) {
                            AltManager.accounts.add(new AccountData(account.get("name").getAsString(), uuid));
                        } else {
                            if (account.get("refresh_token") == null) {
                                AltManager.accounts.add(new AccountData(account.get("access_token").getAsString(), null, account.get("name").getAsString(), uuid));
                            } else {
                                AltManager.accounts.add(new AccountData(account.get("access_token").getAsString(), account.get("refresh_token").getAsString(), account.get("name").getAsString(), uuid));
                            }
                        }
                    }
                }
                boolean loginCheck = true;
                Session session = this.mc.getSession();
                if (!session.getSessionID().contains("FML:")) {
                    AccountData data = getAccountData(UUIDTypeAdapter.fromString(session.getPlayerID()));
                    String refreshToken = null;
                    if (data != null) {
                        refreshToken = data.getRefreshToken();
                        removeAccount(data);
                    }
                    data = new AccountData(session.getToken(), refreshToken, session.getUsername(), session.getPlayerID());
                    addAccount(data);
                    saveAltManager();
                    loginCheck = false;
                } else if (obj.has("selected_account")) {//dev environment
                    UUID selected = UUIDTypeAdapter.fromString(obj.get("selected_account").getAsString());
                    for (AccountData data : AltManager.accounts) {
                        if (data.getUuid().equals(selected)) {
                            currentAccount = data;
                            break;
                        }
                    }
                }
                if (isLoggedIn() && loginCheck) {
                    if (currentAccount.isOffline()) {
                        setOfflineSession(currentAccount);
                    } else {
                        AuthManager.login(currentAccount);
                    }
                }

            } catch (RuntimeException | IOException ex) {
                Reference.LOGGER.error("Exception thrown while parsing alt manager file", ex);
            }
    }

    public void saveAltManager() {
        JsonObject obj = new JsonObject(), accounts = new JsonObject();
        for (AccountData data : AltManager.accounts) {
            JsonObject acc = new JsonObject();
            acc.addProperty("access_token", data.getMcAccessToken());
            acc.addProperty("refresh_token", data.getRefreshToken());
            acc.addProperty("name", data.getName());
            accounts.add(data.getUuidString(), acc);
        }
        obj.add("accounts", accounts);
        if (currentAccount != null)
            obj.addProperty("selected_account", currentAccount.getUuidString());
        try {
            String json = null;
            while (json == null || !FileUtils.isValidJson(json))
                json = Reference.GSON_PRETTY.toJson(obj);
            FileWriter fileWriter = new FileWriter(getAltManagerFile());
            fileWriter.write(json);
            fileWriter.close();
        } catch (IOException ex) {
            Reference.LOGGER.error("Exception thrown while saving alt manager", ex);
        }
    }

    public void setAccount(AccountData data) {
        currentAccount = data;
        if (data.isOffline()) {
            setOfflineSession(data);
        } else {
            ((MixinMinecraft) this.mc).setSession(new Session(data.getName(), data.getUuidString(), data.getMcAccessToken(), "mojang"));
        }
        (new SessionUpdateEvent(this.mc.getSession())).call();
    }

    public void setAccount(UUID uuid) {
        for (AccountData data : accounts) {
            if (data.getUuid().equals(uuid)) {
                setAccount(data);
                break;
            }
        }
    }

    public void setOfflineSession(AccountData data) {
        ((MixinMinecraft) this.mc).setSession(new Session(data.getName(), data.getUuidString(), "X", "legacy"));
    }

    public AccountData getAccountData(UUID uuid) {
        for (AccountData data : accounts) {
            if (data.getUuid().equals(uuid))
                return data;
        }
        return null;
    }

    public File getAltManagerFile() {
        return new File(Client.getClientRunDirectory(), "alt_manager_accounts.json");
    }

    public static boolean isLoggedIn() {
        return (currentAccount != null);
    }

    public static AltManager getInstance() {
        return INSTANCE;
    }
}