package co.crystaldev.client.util.enums;

public enum MinecraftVersion {
  V1_7_10("1.7.10", 1, 7, 10),
  V1_8_9("1.8.9", 1, 8, 9),
  V1_12_2("1.12.2", 1, 12, 2),
  V1_16_5("1.16.5", 1, 16, 5),
  V1_17_1("1.17.1", 1, 17, 1),
  V1_18_1("1.18.1", 1, 18, 1);
  
  MinecraftVersion(String versionString, int major, int minor, int patch) {
    this.versionString = versionString;
    this.major = major;
    this.minor = minor;
    this.patch = patch;
  }
  
  private final String versionString;
  
  private final int major;
  
  private final int minor;
  
  private final int patch;
  
  public String getVersionString() {
    return this.versionString;
  }
  
  public int getMajor() {
    return this.major;
  }
  
  public int getMinor() {
    return this.minor;
  }
  
  public int getPatch() {
    return this.patch;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\enums\MinecraftVersion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */