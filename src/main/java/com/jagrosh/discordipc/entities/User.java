package com.jagrosh.discordipc.entities;

public class User {
  private final String name;
  
  private final String discriminator;
  
  private final long id;
  
  private final String avatar;
  
  public User(String name, String discriminator, long id, String avatar) {
    this.name = name;
    this.discriminator = discriminator;
    this.id = id;
    this.avatar = avatar;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getDiscriminator() {
    return this.discriminator;
  }
  
  public long getIdLong() {
    return this.id;
  }
  
  public String getId() {
    return Long.toString(this.id);
  }
  
  public String getAvatarId() {
    return this.avatar;
  }
  
  public String getAvatarUrl() {
    return (getAvatarId() == null) ? null : ("https://cdn.discordapp.com/avatars/" + getId() + "/" + getAvatarId() + (
      getAvatarId().startsWith("a_") ? ".gif" : ".png"));
  }
  
  public String getDefaultAvatarId() {
    return DefaultAvatar.values()[Integer.parseInt(getDiscriminator()) % (DefaultAvatar.values()).length].toString();
  }
  
  public String getDefaultAvatarUrl() {
    return "https://discordapp.com/assets/" + getDefaultAvatarId() + ".png";
  }
  
  public String getEffectiveAvatarUrl() {
    return (getAvatarUrl() == null) ? getDefaultAvatarUrl() : getAvatarUrl();
  }
  
  public boolean isBot() {
    return false;
  }
  
  public String getAsMention() {
    return "<@" + this.id + '>';
  }
  
  public boolean equals(Object o) {
    if (!(o instanceof User))
      return false; 
    User oUser = (User)o;
    return (this == oUser || this.id == oUser.id);
  }
  
  public int hashCode() {
    return Long.hashCode(this.id);
  }
  
  public String toString() {
    return "U:" + getName() + '(' + this.id + ')';
  }
  
  public enum DefaultAvatar {
    BLURPLE("6debd47ed13483642cf09e832ed0bc1b"),
    GREY("322c936a8c8be1b803cd94861bdfa868"),
    GREEN("dd4dbc0016779df1378e7812eabaa04d"),
    ORANGE("0e291f67c9274a1abdddeb3fd919cbaa"),
    RED("1cbd08c76f8af6dddce02c5138971129");
    
    private final String text;
    
    DefaultAvatar(String text) {
      this.text = text;
    }
    
    public String toString() {
      return this.text;
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\jagrosh\discordipc\entities\User.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */