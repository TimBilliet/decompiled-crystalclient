package co.crystaldev.client.account;

import com.mojang.util.UUIDTypeAdapter;

import java.util.UUID;

public class AccountData {
  private final String accessToken;
  
  private final String name;
  
  private final UUID id;
  
  private final String unformattedId;
  
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
  
  public AccountData(String accessToken, String name, String id) {
    this.name = name;
    this.id = UUIDTypeAdapter.fromString(this.unformattedId = id);
    this.accessToken = accessToken;
  }
  
  public boolean equals(Object other) {
    if (other instanceof AccountData) {
      AccountData acc = (AccountData)other;
      return (this.accessToken.equals(acc.accessToken) && getId().equals(acc.getId()));
    } 
    return false;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\account\AccountData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */