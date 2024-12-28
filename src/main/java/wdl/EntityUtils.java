package wdl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.projectile.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wdl.api.IEntityAdder;
import wdl.api.ISpecialEntityHandler;
import wdl.api.WDLApi;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntityUtils {
  private static final Logger logger = LogManager.getLogger();
  
  public static final Map<String, Class<?>> stringToClassMapping;
  
  public static final Map<Class<?>, String> classToStringMapping;
  
  static {
    try {
      Map<?, ?> map1 = null;
      Map<?, ?> map2 = null;
      for (Field field : EntityList.class.getDeclaredFields()) {
        if (field.getType().equals(Map.class)) {
          field.setAccessible(true);
          Map<?, ?> m = (Map<?, ?>)field.get((Object)null);
          Map.Entry<?, ?> e = (Map.Entry<?, ?>)m.entrySet().toArray()[0];
          if (e.getKey() instanceof String && e
            .getValue() instanceof Class) {
            Map<?, ?> map = m;
            map1 = map;
          } 
          if (e.getKey() instanceof Class && e
            .getValue() instanceof String) {
            Map<?, ?> map = m;
            map2 = map;
          } 
        } 
      } 
      if (map1 == null)
        throw new Exception("WDL: Failed to find stringToClassMapping!"); 
      if (map2 == null)
        throw new Exception("WDL: Failed to find classToStringMapping!"); 
      stringToClassMapping = (Map)map1;
      classToStringMapping = (Map)map2;
    } catch (Exception e) {
      Minecraft.getMinecraft().crashed(new CrashReport("World Downloader Mod: failed to set up entity ranges!", e));
      throw new Error("World Downloader Mod: failed to set up entity ranges!", e);
    } 
  }
  
  public static Set<String> getEntityTypes() {
    Set<String> returned = new HashSet<>();
    for (Map.Entry<String, Class<?>> e : stringToClassMapping.entrySet()) {
      if (Modifier.isAbstract(((Class)e.getValue()).getModifiers()))
        continue; 
      returned.add(e.getKey());
    } 
    for (WDLApi.ModInfo<ISpecialEntityHandler> info : (Iterable<WDLApi.ModInfo<ISpecialEntityHandler>>)WDLApi.getImplementingExtensions(ISpecialEntityHandler.class))
      returned.addAll(((ISpecialEntityHandler)info.mod).getSpecialEntities().values()); 
    return returned;
  }
  
  public static Multimap<String, String> getEntitiesByGroup() {
    HashMultimap hashMultimap = HashMultimap.create();
    Set<String> types = getEntityTypes();
    for (String type : types)
      hashMultimap.put(getEntityGroup(type), type); 
    return (Multimap<String, String>)hashMultimap;
  }
  
  public static int getDefaultEntityRange(String type) {
    if (type == null)
      return -1; 
    for (WDLApi.ModInfo<IEntityAdder> info : (Iterable<WDLApi.ModInfo<IEntityAdder>>)WDLApi.getImplementingExtensions(IEntityAdder.class)) {
      List<String> names = ((IEntityAdder)info.mod).getModEntities();
      if (names == null) {
        logger.warn(info + " returned null for getModEntities()!");
        continue;
      } 
      if (names.contains(type))
        return ((IEntityAdder)info.mod).getDefaultEntityTrackDistance(type); 
    } 
    for (WDLApi.ModInfo<ISpecialEntityHandler> info : (Iterable<WDLApi.ModInfo<ISpecialEntityHandler>>)WDLApi.getImplementingExtensions(ISpecialEntityHandler.class)) {
      Multimap<String, String> specialEntities = ((ISpecialEntityHandler)info.mod).getSpecialEntities();
      if (specialEntities == null) {
        logger.warn(info + " returned null for getSpecialEntities()!");
        continue;
      } 
      for (Map.Entry<String, String> e : (Iterable<Map.Entry<String, String>>)specialEntities.entries()) {
        if (((String)e.getValue()).equals(type)) {
          int trackDistance = ((ISpecialEntityHandler)info.mod).getSpecialEntityTrackDistance(e.getValue());
          if (trackDistance < 0)
            trackDistance = getMostLikelyEntityTrackDistance(e.getKey()); 
          return trackDistance;
        } 
      } 
    } 
    return getVanillaEntityRange(stringToClassMapping.get(type));
  }
  
  public static int getEntityTrackDistance(Entity e) {
    return getEntityTrackDistance(getTrackDistanceMode(), e);
  }
  
  public static int getEntityTrackDistance(String mode, Entity e) {
    if ("default".equals(mode))
      return getMostLikelyEntityTrackDistance(e); 
    if ("server".equals(mode))
      return 32; 
    if ("user".equals(mode)) {
      String prop = WDL.worldProps.getProperty("Entity." + 
          getEntityType(e) + ".TrackDistance", "-1");
      int value = Integer.valueOf(prop).intValue();
      if (value == -1)
        return getEntityTrackDistance("server", e); 
      return value;
    } 
    throw new IllegalArgumentException("Mode is not a valid mode: " + mode);
  }
  
  public static int getEntityTrackDistance(String type) {
    return getEntityTrackDistance(getTrackDistanceMode(), type);
  }
  
  public static int getEntityTrackDistance(String mode, String type) {
    if ("default".equals(mode)) {
      int mostLikelyDistance = getMostLikelyEntityTrackDistance(type);
      if (mostLikelyDistance < 0)
        for (WDLApi.ModInfo<ISpecialEntityHandler> info : (Iterable<WDLApi.ModInfo<ISpecialEntityHandler>>)WDLApi.getImplementingExtensions(ISpecialEntityHandler.class)) {
          Multimap<String, String> specialEntities = ((ISpecialEntityHandler)info.mod).getSpecialEntities();
          for (Map.Entry<String, String> mapping : (Iterable<Map.Entry<String, String>>)specialEntities.entries()) {
            if (type.equals(mapping.getValue()))
              return getEntityTrackDistance(mode, mapping
                  .getKey()); 
          } 
        }  
      return mostLikelyDistance;
    } 
    if ("server".equals(mode))
      return 32; 
    if ("user".equals(mode)) {
      String prop = WDL.worldProps.getProperty("Entity." + type + ".TrackDistance", "-1");
      int value = Integer.valueOf(prop).intValue();
      if (value == -1)
        return getEntityTrackDistance("server", type); 
      return value;
    } 
    throw new IllegalArgumentException("Mode is not a valid mode: " + mode);
  }
  
  public static String getEntityGroup(String type) {
    if (type == null)
      return null; 
    for (WDLApi.ModInfo<IEntityAdder> info : (Iterable<WDLApi.ModInfo<IEntityAdder>>)WDLApi.getImplementingExtensions(IEntityAdder.class)) {
      List<String> names = ((IEntityAdder)info.mod).getModEntities();
      if (names == null) {
        logger.warn(info + " returned null for getModEntities()!");
        continue;
      } 
      if (names.contains(type))
        return ((IEntityAdder)info.mod).getEntityCategory(type); 
    } 
    for (WDLApi.ModInfo<ISpecialEntityHandler> info : (Iterable<WDLApi.ModInfo<ISpecialEntityHandler>>)WDLApi.getImplementingExtensions(ISpecialEntityHandler.class)) {
      Multimap<String, String> specialEntities = ((ISpecialEntityHandler)info.mod).getSpecialEntities();
      if (specialEntities == null) {
        logger.warn(info + " returned null for getSpecialEntities()!");
        continue;
      } 
      if (specialEntities.containsValue(type))
        return ((ISpecialEntityHandler)info.mod).getSpecialEntityCategory(type); 
    } 
    if (stringToClassMapping.containsKey(type)) {
      Class<?> clazz = stringToClassMapping.get(type);
      if (IMob.class.isAssignableFrom(clazz))
        return "Hostile"; 
      if (IAnimals.class.isAssignableFrom(clazz))
        return "Passive"; 
      return "Other";
    } 
    return null;
  }
  
  public static boolean isEntityEnabled(Entity e) {
    return isEntityEnabled(getEntityType(e));
  }
  
  public static boolean isEntityEnabled(String type) {
    boolean groupEnabled = WDL.worldProps.getProperty("EntityGroup." + getEntityGroup(type) + ".Enabled", "true").equals("true");
    boolean singleEnabled = WDL.worldProps.getProperty("Entity." + type + ".Enabled", "true").equals("true");
    return (groupEnabled && singleEnabled);
  }
  
  public static String getEntityType(Entity e) {
    String vanillaName = EntityList.getEntityString(e);
    for (WDLApi.ModInfo<ISpecialEntityHandler> info : (Iterable<WDLApi.ModInfo<ISpecialEntityHandler>>)WDLApi.getImplementingExtensions(ISpecialEntityHandler.class)) {
      if (((ISpecialEntityHandler)info.mod).getSpecialEntities().containsKey(vanillaName)) {
        String specialName = ((ISpecialEntityHandler)info.mod).getSpecialEntityName(e);
        if (specialName != null)
          return specialName; 
      } 
    } 
    return vanillaName;
  }
  
  public static int getMostLikelyEntityTrackDistance(Entity e) {
    if (WDL.isSpigot())
      return getDefaultSpigotEntityRange(e.getClass()); 
    return getDefaultEntityRange(getEntityType(e));
  }
  
  public static int getMostLikelyEntityTrackDistance(String type) {
    if (WDL.isSpigot()) {
      Class<?> c = stringToClassMapping.get(type);
      if (c != null)
        return getDefaultSpigotEntityRange(c); 
      return getDefaultEntityRange(type);
    } 
    return getDefaultEntityRange(type);
  }
  
  public static int getVanillaEntityRange(String type) {
    return getVanillaEntityRange(classToStringMapping.get(type));
  }
  
  public static String getTrackDistanceMode() {
    return WDL.worldProps.getProperty("Entity.TrackDistanceMode", "server");
  }
  
  public static int getVanillaEntityRange(Class<?> c) {
    if (c == null)
      return -1; 
    if (EntityFishHook.class.isAssignableFrom(c) || EntityArrow.class
      .isAssignableFrom(c) || EntitySmallFireball.class
      .isAssignableFrom(c) || EntityFireball.class
      .isAssignableFrom(c) || EntitySnowball.class
      .isAssignableFrom(c) || EntityEnderPearl.class
      .isAssignableFrom(c) || EntityEnderEye.class
      .isAssignableFrom(c) || EntityEgg.class
      .isAssignableFrom(c) || EntityPotion.class
      .isAssignableFrom(c) || EntityExpBottle.class
      .isAssignableFrom(c) || EntityFireworkRocket.class
      .isAssignableFrom(c) || EntityItem.class
      .isAssignableFrom(c) || EntitySquid.class
      .isAssignableFrom(c))
      return 64; 
    if (EntityMinecart.class.isAssignableFrom(c) || EntityBoat.class
      .isAssignableFrom(c) || EntityWither.class
      .isAssignableFrom(c) || EntityBat.class
      .isAssignableFrom(c) || IAnimals.class
      .isAssignableFrom(c))
      return 80; 
    if (EntityDragon.class.isAssignableFrom(c) || EntityTNTPrimed.class
      .isAssignableFrom(c) || EntityFallingBlock.class
      .isAssignableFrom(c) || EntityHanging.class
      .isAssignableFrom(c) || EntityArmorStand.class
      .isAssignableFrom(c) || EntityXPOrb.class
      .isAssignableFrom(c))
      return 160; 
    if (EntityEnderCrystal.class.isAssignableFrom(c))
      return 256; 
    return -1;
  }
  
  public static int getDefaultSpigotEntityRange(Class<?> c) {
    int monsterRange = 48;
    int animalRange = 48;
    int miscRange = 32;
    int otherRange = 64;
    if (EntityMob.class.isAssignableFrom(c) || EntitySlime.class
      .isAssignableFrom(c))
      return 48; 
    if (EntityCreature.class.isAssignableFrom(c) || EntityAmbientCreature.class
      .isAssignableFrom(c))
      return 48; 
    if (EntityItemFrame.class.isAssignableFrom(c) || EntityPainting.class
      .isAssignableFrom(c) || EntityItem.class
      .isAssignableFrom(c) || EntityXPOrb.class
      .isAssignableFrom(c))
      return 32; 
    return 64;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\EntityUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */