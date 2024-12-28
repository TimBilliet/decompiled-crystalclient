package co.crystaldev.client.account;

import co.crystaldev.client.Client;
import co.crystaldev.client.Reference;
import co.crystaldev.client.event.impl.init.SessionUpdateEvent;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.MixinMinecraft;
import co.crystaldev.client.util.FileUtils;
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
  
  public static UUID getClientToken() {
    return clientToken;
  }
  
  private static UUID clientToken = UUID.randomUUID();
  
  private static AccountData currentAccount;
  
  private static LinkedList<AccountData> accounts;
  
  public static AccountData getCurrentAccount() {
    return currentAccount;
  }
  
  public static void setCurrentAccount(AccountData currentAccount) {
    AltManager.currentAccount = currentAccount;
  }
  
  public static LinkedList<AccountData> getAccounts() {
    return accounts;
  }
  
  public AltManager() {
    accounts = new LinkedList<>();
    INSTANCE = this;
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
      if (acc.getId().equals(uuid))
        return true; 
    } 
    return false;
  }
  
  public void addAccount(AccountData account) {
    addAccount(account, true);
  }
  
  public void addAccount(AccountData account, boolean setToCurrent) {
    if (account == null)
      return; 
    int index = accounts.size();
    if (currentAccount != null && currentAccount.getId().equals(account.getId()))
      currentAccount = null; 
    for (int i = 0; i < accounts.size(); i++) {
      AccountData data = accounts.get(i);
      if (data.getId().equals(account.getId())) {
        accounts.remove(i);
        index = i;
        break;
      } 
    } 
    accounts.add(index, account);
    if (setToCurrent)
      setAccount(account); 
    saveAltManager();
  }
  
  public void removeAccount(AccountData account) {
    if (account == null)
      return; 
    if (currentAccount != null && currentAccount.getId().equals(account.getId()))
      currentAccount = null; 
    accounts.removeIf(a -> a.getId().equals(account.getId()));
    saveAltManager();
  }
  
  public void populateAltManager() {
    File file = getAltManagerFile();
    if (file.exists())
      try {
        FileReader fr = new FileReader(file);
        JsonObject obj = (JsonObject)Reference.GSON.fromJson(fr, JsonObject.class);
        fr.close();
        if (obj.has("accounts")) {
          JsonObject accounts = obj.get("accounts").getAsJsonObject();
          for (Map.Entry<String, JsonElement> entry : (Iterable<Map.Entry<String, JsonElement>>)accounts.entrySet()) {
            String uuid = entry.getKey();
            JsonObject account = accounts.get(uuid).getAsJsonObject();
            AltManager.accounts.add(new AccountData(account.get("access_token").getAsString(), account.get("name").getAsString(), uuid));
          } 
        } 
        if (obj.has("client_token"))
          clientToken = UUIDTypeAdapter.fromString(obj.get("client_token").getAsString()); 
        boolean loginCheck = true;
        Session session = this.mc.getSession();
        if (!session.getSessionID().contains("FML:")) {
          AccountData data = getAccountData(UUIDTypeAdapter.fromString(session.getPlayerID()));
          if (data != null)
            removeAccount(data); 
          data = new AccountData(session.getToken(), session.getUsername(), session.getPlayerID());
          addAccount(data);
          saveAltManager();
          loginCheck = false;
        } else if (obj.has("selected_account")) {
          UUID selected = UUIDTypeAdapter.fromString(obj.get("selected_account").getAsString());
          for (AccountData data : AltManager.accounts) {
            if (data.getId().equals(selected)) {
              currentAccount = data;
              break;
            } 
          } 
        } 
        if (isLoggedIn() && loginCheck)
          AuthManager.login(getCurrentAccount()); 
      } catch (RuntimeException|IOException ex) {
        Reference.LOGGER.error("Exception thrown while parsing alt manager file", ex);
      }  
  }
  
  public void saveAltManager() {
    JsonObject obj = new JsonObject(), accounts = new JsonObject();
    for (AccountData data : AltManager.accounts) {
      JsonObject acc = new JsonObject();
      acc.addProperty("access_token", data.getAccessToken());
      acc.addProperty("name", data.getName());
      accounts.add(data.getUnformattedId(), (JsonElement)acc);
    } 
    obj.add("accounts", (JsonElement)accounts);
    obj.addProperty("client_token", clientToken.toString());
    if (currentAccount != null)
      obj.addProperty("selected_account", currentAccount.getUnformattedId()); 
    try {
      String json = null;
      while (json == null || !FileUtils.isValidJson(json))
        json = Reference.GSON.toJson((JsonElement)obj); 
      FileWriter fileWriter = new FileWriter(getAltManagerFile());
      fileWriter.write(json);
      fileWriter.close();
    } catch (IOException ex) {
      Reference.LOGGER.error("Exception thrown while saving alt manager", ex);
    } 
  }
  
  public void setAccount(AccountData data) {
    currentAccount = data;
    ((MixinMinecraft)this.mc).setSession(new Session(data.getName(), data.getUnformattedId(), data.getAccessToken(), "mojang"));
    (new SessionUpdateEvent(this.mc.getSession())).call();
  }
  
  public void setAccount(UUID uuid) {
    for (AccountData data : accounts) {
      if (data.getId().equals(uuid)) {
        setAccount(data);
        break;
      } 
    } 
  }
  
  public AccountData getAccountData(UUID uuid) {
    for (AccountData data : accounts) {
      if (data.getId().equals(uuid))
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


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\account\AltManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */