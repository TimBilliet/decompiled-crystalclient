package mchorse.emoticons.common.emotes;

import mchorse.emoticons.utils.Time;

import java.util.HashMap;
import java.util.Map;

public class Emotes {
  public static final Map<String, Emote> EMOTES = new HashMap<>();
  
  public static boolean has(String emote) {
    if (emote.contains(":"))
      emote = emote.split(":")[0]; 
    return EMOTES.containsKey(emote);
  }
  
  public static Emote get(String emote) {
    if (emote.contains(":")) {
      String[] splits = emote.split(":");
      Emote emote1 = EMOTES.get(splits[0]);
      return (emote1 == null) ? null : emote1.getDynamicEmote(splits[1]);
    } 
    Emote meme = EMOTES.get(emote);
    return (meme == null) ? null : meme.getDynamicEmote();
  }
  
  public static Emote getDefault(String emote) {
    Emote emo = get(emote);
    return (emo == null) ? get("default") : emo;
  }
  
  public static void register() {
    EMOTES.clear();
    String bestMates = createSound("emoticons:best_mates");
    String boneless = createSound("emoticons:boneless");
    String defaultEmote = createSound("emoticons:default");
    String discoFever = createSound("emoticons:disco_fever");
    String electroShuffle = createSound("emoticons:electro_shuffle");
    String floss = createSound("emoticons:floss");
    String fresh = createSound("emoticons:fresh");
    String gangnamStyle = createSound("emoticons:gangnam_style");
    String hype = createSound("emoticons:hype");
    String infiniteDab = createSound("emoticons:infinite_dab");
    String orangeJustice = createSound("emoticons:orange_justice");
    String skibidi = createSound("emoticons:skibidi");
    String squatKick = createSound("emoticons:squat_kick");
    String starPower = createSound("emoticons:star_power");
    String takeTheL = createSound("emoticons:take_the_l");
    String tidy = createSound("emoticons:tidy");
    String freeFlow = createSound("emoticons:free_flow");
    String shimmer = createSound("emoticons:shimmer");
    String getFunky = createSound("emoticons:getFunky");
    String rockstar = createSound("emoticons:rockstar");
    String renegade = createSound("emoticons:renegade");
    String armPump = createSound("emoticons:arm_pump");
    String bigBank = createSound("emoticons:big_bank");
    String partyGirl = createSound("emoticons:party_girl");
    String savage = createSound("emoticons:savage");
    String ohNaNaNA = createSound("emoticons:oh_na_na_na");
    String wap = createSound("emoticons:wap");
    String ronaldo = createSound("emoticons:ronaldo");
    String hitItFergie = createSound("emoticons:hit_it_fergie");
    String saySo = createSound("emoticons:say_so");
    String toosieSlide = createSound("emoticons:toosie_slide");
    register(new Emote("best_mates", 11, true, bestMates));
    register(new Emote("boneless", 40, true, boneless));
    register(new Emote("default", 139, true, defaultEmote));
    register(new Emote("disco_fever", 175, true, discoFever));
    register(new Emote("electro_shuffle", 169, true, electroShuffle));
    register(new Emote("floss", 32, true, floss));
    register(new Emote("fresh", 101, true, fresh));
    register(new Emote("gangnam_style", 18, true, gangnamStyle));
    register(new Emote("hype", 68, true, hype));
    register(new Emote("infinite_dab", 19, true, infiniteDab));
    register(new Emote("orange_justice", 130, true, orangeJustice));
    register(new Emote("skibidi", 16, true, skibidi));
    register(new Emote("squat_kick", 232, true, squatKick));
    register(new StarPowerEmote("star_power", 160, true, starPower));
    register(new Emote("take_the_l", 16, true, takeTheL));
    register(new Emote("tidy", 104, true, tidy));
    register(new Emote("free_flow", 158, true, freeFlow));
    register(new Emote("shimmer", 156, true, shimmer));
    register(new Emote("get_funky", 172, true, getFunky));
    register(new Emote("boy", 29, false, null));
    register(new Emote("bow", 43, false, null));
    register(new Emote("calculated", 33, false, null));
    register(new Emote("chicken", 19, true, null));
    register(new Emote("clapping", 15, true, null));
    register(new Emote("club", 20, true, null));
    register(new Emote("confused", 140, false, null));
    register(new CryingEmote("crying", 27, true, null));
    register(new Emote("dab", 23, false, null));
    register(new Emote("facepalm", 104, false, null));
    register(new Emote("fist", 53, false, null));
    register(new Emote("laughing", 15, true, null));
    register(new Emote("no", 30, false, null));
    register(new Emote("pointing", 33, false, null));
    register(new PureSaltEmote("pure_salt", 104, false, null));
    register(new RockPaperScissorsEmote("rock_paper_scissors", 60, false, null));
    register(new Emote("salute", 50, false, null));
    register(new Emote("shrug", 50, false, null));
    register(new Emote("t_pose", 80, true, null));
    register(new Emote("thinking", 100, true, null));
    register(new Emote("twerk", 14, true, null));
    register(new Emote("wave", 40, false, null));
    register(new Emote("yes", 23, false, null));
    register(new Emote("bitchslap", Time.toTicks(100), false, null));
    register(new Emote("bongo_cat", Time.toTicks(238), false, null));
    register(new Emote("breathtaking", Time.toTicks(154), false, null));
    register(new DisgustedEmote("disgusted", Time.toTicks(200), false, null));
    register(new Emote("exhausted", Time.toTicks(330), true, null));
    register(new Emote("punch", Time.toTicks(58), false, null));
    register(new SneezeEmote("sneeze", Time.toTicks(200), false, null));
    register(new Emote("threatening", Time.toTicks(70), false, null));
    register(new Emote("woah", Time.toTicks(66), false, null));
    register(new Emote("stick_bug", Time.toTicks(25), true, null));
    register(new Emote("am_stuff", Time.toTicks(80), false, null));
    register(new Emote("slow_clap", Time.toTicks(200), false, null));
    register(new Emote("hell_yeah", Time.toTicks(70), false, null));
    register(new Emote("paranoid", Time.toTicks(315), false, null));
    register(new Emote("scared", Time.toTicks(50), true, null));
    register(new Emote("rockstar", 280, false, rockstar));
    register(new Emote("renegade", 303, false, renegade));
    register(new Emote("wiggle", 75, false, null));
    register(new Emote("arm_pump", 173, false, armPump));
    register(new Emote("backflip", 35, false, null));
    register(new Emote("head_spin", 213, false, null));
    register(new Emote("big_bank", 110, false, bigBank));
    register(new Emote("party_girl", 273, false, partyGirl));
    register(new Emote("savage", 280, false, savage));
    register(new Emote("oh_na_na_na", 286, false, ohNaNaNA));
    register(new Emote("wap", 229, false, wap));
    register(new Emote("ronaldo", 90, false, ronaldo));
    register(new Emote("jumping_jacks", 66, false, null));
    register(new Emote("hit_it_fergie", 300, false, hitItFergie));
    register(new Emote("say_so", 226, false, saySo));
    register(new Emote("toosie_slide", 153, false, toosieSlide));
  }
  
  public static void register(Emote emote) {
    EMOTES.put(emote.name, emote);
  }
  
  private static String createSound(String path) {
    return path;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\common\emotes\Emotes.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */