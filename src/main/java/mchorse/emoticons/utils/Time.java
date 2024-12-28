package mchorse.emoticons.utils;

public class Time {
  public static int toTicks(int frames30) {
    return (int)Math.floor((frames30 / 30.0F * 20.0F));
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticon\\utils\Time.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */