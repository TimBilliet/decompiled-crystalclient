package com.jagrosh.discordipc.entities;

public enum DiscordBuild {
  CANARY("//canary.discordapp.com/api"),
  PTB("//ptb.discordapp.com/api"),
  STABLE("//discordapp.com/api"),
//  ANY;
ANY("");
  
  private final String endpoint;
  
  DiscordBuild(String endpoint) {
    this.endpoint = endpoint;
  }
  
  public static DiscordBuild from(String endpoint) {
    for (DiscordBuild value : values()) {
      if (value.endpoint != null && value.endpoint.equals(endpoint))
        return value; 
    } 
    return ANY;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\jagrosh\discordipc\entities\DiscordBuild.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */