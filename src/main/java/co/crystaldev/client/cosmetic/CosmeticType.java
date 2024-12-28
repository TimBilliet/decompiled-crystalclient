package co.crystaldev.client.cosmetic;

public enum CosmeticType {
  CLOAK("Cloaks", "cloak", "cloak", Position.TORSO, false),
  EMOTE("Emotes", "emote", "emote", Position.TORSO, true),
  WINGS("Wings", "wings", "wings", Position.TORSO, false),
  COLOR("Colors", "color", "color", Position.HEAD, true),
  UNKNOWN(null, null, "unknown", Position.TORSO, true);
  
  private final String pluralForm;
  
  private final String singularForm;
  
  private final String internalName;
  
  private final Position position;
  
  private final boolean front;
  
  public String getPluralForm() {
    return this.pluralForm;
  }
  
  public String getSingularForm() {
    return this.singularForm;
  }
  
  public String getInternalName() {
    return this.internalName;
  }
  
  public Position getPosition() {
    return this.position;
  }
  
  public boolean isFront() {
    return this.front;
  }
  
  CosmeticType(String pluralForm, String singularForm, String internalName, Position position, boolean front) {
    this.pluralForm = pluralForm;
    this.singularForm = singularForm;
    this.internalName = internalName;
    this.position = position;
    this.front = front;
  }
  
  public String toString() {
    return this.pluralForm;
  }
  
  public String getType() {
    return super.toString();
  }
  
  public enum Position {
    HEAD, TORSO, WAIST, LEGS, FEET;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\cosmetic\CosmeticType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */