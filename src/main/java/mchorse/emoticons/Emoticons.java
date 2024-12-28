package mchorse.emoticons;

import co.crystaldev.client.Client;
import co.crystaldev.client.Reference;
import co.crystaldev.client.event.EventBus;
import mchorse.emoticons.client.EntityModelHandler;
import mchorse.emoticons.client.RenderLightmap;
import mchorse.emoticons.client.UserUpdateHandler;
import mchorse.emoticons.common.emotes.Emotes;
import mchorse.emoticons.skin_n_bones.api.animation.Animation;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationManager;
import mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorConfig;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJAction;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJLoader;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class Emoticons {
  private static final File configFolder = new File(Client.getClientRunDirectory(), "emotes");
  
  public static Map<String, BOBJAction> actionMap = new HashMap<>();
  
  public static BOBJLoader.BOBJData ragdoll;
  
  public Emoticons() {
    init();
  }
  
  public static void reloadActions() {
    try {
      BOBJLoader.BOBJData actions = BOBJLoader.readData(Emoticons.class.getResourceAsStream("/assets/emoticons/models/entity/actions.bobj"));
      actionMap.clear();
      actionMap.putAll(actions.actions);
      actionMap.putAll(ragdoll.actions);
    } catch (Exception ex) {
      Reference.LOGGER.error("Unable to reload actions", ex);
    } 
  }
  
  public void init() {
    Emotes.register();
    EventBus.register(new UserUpdateHandler());
    EventBus.register(new EntityModelHandler());
    RenderLightmap.create();
    try {
      Class<?> loader = getClass();
      AnimationManager manager = AnimationManager.INSTANCE;
      ragdoll = BOBJLoader.readData(loader.getResourceAsStream("/assets/emoticons/models/entity/ragdoll.bobj"));
      ragdoll.initiateArmatures();
      BOBJLoader.BOBJData propData = BOBJLoader.readData(loader.getResourceAsStream("/assets/emoticons/models/entity/props.bobj"));
      BOBJLoader.BOBJData propSimpleData = BOBJLoader.readData(loader.getResourceAsStream("/assets/emoticons/models/entity/props_simple.bobj"));
      BOBJLoader.BOBJData steveData = BOBJLoader.readData(loader.getResourceAsStream("/assets/emoticons/models/entity/default.bobj"));
      BOBJLoader.BOBJData steve3DData = BOBJLoader.readData(loader.getResourceAsStream("/assets/emoticons/models/entity/default_3d.bobj"));
      BOBJLoader.BOBJData alexData = BOBJLoader.readData(loader.getResourceAsStream("/assets/emoticons/models/entity/slim.bobj"));
      BOBJLoader.BOBJData alex3DData = BOBJLoader.readData(loader.getResourceAsStream("/assets/emoticons/models/entity/slim_3d.bobj"));
      BOBJLoader.BOBJData steveSimpleData = BOBJLoader.readData(loader.getResourceAsStream("/assets/emoticons/models/entity/default_simple.bobj"));
      BOBJLoader.BOBJData alexSimpleData = BOBJLoader.readData(loader.getResourceAsStream("/assets/emoticons/models/entity/slim_simple.bobj"));
      reloadActions();
      steveData.actions = actionMap;
      steve3DData.actions = actionMap;
      alexData.actions = actionMap;
      alex3DData.actions = actionMap;
      steveSimpleData.actions = actionMap;
      alexSimpleData.actions = actionMap;
      BOBJLoader.merge(propData, ragdoll);
      BOBJLoader.merge(propSimpleData, ragdoll);
      BOBJLoader.merge(steveData, propData);
      BOBJLoader.merge(steve3DData, propData);
      BOBJLoader.merge(alexData, propData);
      BOBJLoader.merge(alex3DData, propData);
      BOBJLoader.merge(steveSimpleData, propSimpleData);
      BOBJLoader.merge(alexSimpleData, propSimpleData);
      ((BOBJArmature)ragdoll.armatures.get("ArmatureRagdoll")).copyOrder((BOBJArmature)steveData.armatures.get("Armature"));
      Animation steve = new Animation("default", steveData);
      Animation steve3d = new Animation("default_3d", steve3DData);
      Animation alex = new Animation("slim", alexData);
      Animation alex3d = new Animation("slim_3d", alex3DData);
      Animation steveSimple = new Animation("default_simple", steveSimpleData);
      Animation alexSimple = new Animation("slim_simple", alexSimpleData);
      steve.init();
      steve3d.init();
      alex.init();
      alex3d.init();
      steveSimple.init();
      alexSimple.init();
      manager.animations.put("default", new AnimationManager.AnimationEntry(steve, configFolder, 1L));
      manager.animations.put("default_3d", new AnimationManager.AnimationEntry(steve3d, configFolder, 1L));
      manager.animations.put("slim", new AnimationManager.AnimationEntry(alex, configFolder, 1L));
      manager.animations.put("slim_3d", new AnimationManager.AnimationEntry(alex3d, configFolder, 1L));
      manager.animations.put("default_simple", new AnimationManager.AnimationEntry(steveSimple, configFolder, 1L));
      manager.animations.put("slim_simple", new AnimationManager.AnimationEntry(alexSimple, configFolder, 1L));
      AnimatorConfig steveConfig = (AnimatorConfig)manager.gson.fromJson(IOUtils.toString(loader.getResourceAsStream("/assets/emoticons/models/entity/default.json"), Charset.defaultCharset()), AnimatorConfig.class);
      AnimatorConfig alexConfig = (AnimatorConfig)manager.gson.fromJson(IOUtils.toString(loader.getResourceAsStream("/assets/emoticons/models/entity/slim.json"), Charset.defaultCharset()), AnimatorConfig.class);
      AnimatorConfig steveSimpleConfig = (AnimatorConfig)manager.gson.fromJson(IOUtils.toString(loader.getResourceAsStream("/assets/emoticons/models/entity/default_simple.json"), Charset.defaultCharset()), AnimatorConfig.class);
      AnimatorConfig alexSimpleConfig = (AnimatorConfig)manager.gson.fromJson(IOUtils.toString(loader.getResourceAsStream("/assets/emoticons/models/entity/slim_simple.json"), Charset.defaultCharset()), AnimatorConfig.class);
      manager.configs.put("default", new AnimatorConfig.AnimatorConfigEntry(steveConfig, 1L));
      manager.configs.put("default_3d", new AnimatorConfig.AnimatorConfigEntry(steveConfig, 1L));
      manager.configs.put("slim", new AnimatorConfig.AnimatorConfigEntry(alexConfig, 1L));
      manager.configs.put("slim_3d", new AnimatorConfig.AnimatorConfigEntry(alexConfig, 1L));
      manager.configs.put("default_simple", new AnimatorConfig.AnimatorConfigEntry(steveSimpleConfig, 1L));
      manager.configs.put("slim_simple", new AnimatorConfig.AnimatorConfigEntry(alexSimpleConfig, 1L));
    } catch (Exception ex) {
      Reference.LOGGER.error("Exception thrown while initializing Emoticons", ex);
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\Emoticons.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */