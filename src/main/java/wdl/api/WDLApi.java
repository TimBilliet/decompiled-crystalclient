package wdl.api;

import com.google.common.collect.ImmutableMap;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wdl.*;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WDLApi {
  private static final Logger logger = LogManager.getLogger();
  
  private static final Map<String, ModInfo<?>> wdlMods = new HashMap<>();
  
  public static void saveTileEntity(BlockPos pos, TileEntity te) {
    WDL.saveTileEntity(pos, te);
  }
  
  public static void addWDLMod(String id, String version, IWDLMod mod) {
    if (id == null)
      throw new IllegalArgumentException("id must not be null!  (mod=" + mod + ", version=" + version + ")"); 
    if (version == null)
      throw new IllegalArgumentException("version must not be null!  (mod=" + mod + ", id=" + version + ")"); 
    if (mod == null)
      throw new IllegalArgumentException("mod must not be null!  (id=" + id + ", version=" + version + ")"); 
    ModInfo<IWDLMod> info = new ModInfo<>(id, version, mod);
    if (wdlMods.containsKey(id))
      throw new IllegalArgumentException("A mod by the name of '" + id + "' is already registered by " + wdlMods
          
          .get(id) + " (tried to register " + info + " over it)"); 
    if (!mod.isValidEnvironment("1.8.9a-beta2")) {
      String errorMessage = mod.getEnvironmentErrorMessage("1.8.9a-beta2");
      if (errorMessage != null)
        throw new IllegalArgumentException(errorMessage); 
      throw new IllegalArgumentException("Environment for " + info + " is incorrect!  Perhaps it is for a different version of WDL?  You are running " + "1.8.9a-beta2" + ".");
    } 
    wdlMods.put(id, info);
    if (mod instanceof IMessageTypeAdder) {
      Map<String, IWDLMessageType> types = ((IMessageTypeAdder)mod).getMessageTypes();
      ModMessageTypeCategory category = new ModMessageTypeCategory(info);
      for (Map.Entry<String, IWDLMessageType> e : types.entrySet())
        WDLMessages.registerMessage(e.getKey(), e.getValue(), category); 
    } 
  }
  
  public static <T extends IWDLMod> List<ModInfo<T>> getImplementingExtensions(Class<T> clazz) {
    if (clazz == null)
      throw new IllegalArgumentException("clazz must not be null!"); 
    List<ModInfo<T>> returned = new ArrayList<>();
    for (ModInfo<?> info : wdlMods.values()) {
      if (!info.isEnabled())
        continue; 
      if (clazz.isAssignableFrom(info.mod.getClass())) {
        ModInfo<T> infoCasted = (ModInfo)info;
        returned.add(infoCasted);
      } 
    } 
    return returned;
  }
  
  public static <T extends IWDLMod> List<ModInfo<T>> getAllImplementingExtensions(Class<T> clazz) {
    if (clazz == null)
      throw new IllegalArgumentException("clazz must not be null!"); 
    List<ModInfo<T>> returned = new ArrayList<>();
    for (ModInfo<?> info : wdlMods.values()) {
      if (clazz.isAssignableFrom(info.mod.getClass())) {
        ModInfo<T> infoCasted = (ModInfo)info;
        returned.add(infoCasted);
      } 
    } 
    return returned;
  }
  
  public static Map<String, ModInfo<?>> getWDLMods() {
    return (Map<String, ModInfo<?>>)ImmutableMap.copyOf(wdlMods);
  }
  
  public static String getModInfo(String name) {
    if (!wdlMods.containsKey(name))
      return null; 
    return ((ModInfo)wdlMods.get(name)).getInfo();
  }
  
  private static void logStackTrace() {
    StackTraceElement[] elements = Thread.currentThread().getStackTrace();
    for (StackTraceElement e : elements)
      logger.warn(e.toString()); 
  }
  
  private static class ModMessageTypeCategory extends MessageTypeCategory {
    private ModInfo<?> mod;

    public ModMessageTypeCategory(ModInfo<?> mod) {
      super(mod.id);
    }
    
    public String getDisplayName() {
      return this.mod.getDisplayName();
    }
  }
  
  public static class ModInfo<T extends IWDLMod> {
    public final String id;
    
    public final String version;
    
    public final T mod;
    
    private ModInfo(String id, String version, T mod) {
      this.id = id;
      this.version = version;
      this.mod = mod;
    }
    
    public String toString() {
      return this.id + "v" + this.version + " (" + this.mod.toString() + "/" + this.mod
        .getClass().getName() + ")";
    }
    
    public String getDisplayName() {
      if (this.mod instanceof IWDLModDescripted) {
        String name = ((IWDLModDescripted)this.mod).getDisplayName();
        if (name != null && !name.isEmpty())
          return name; 
      } 
      return this.id;
    }
    
    public String getInfo() {
      StringBuilder info = new StringBuilder();
      info.append("Id: ").append(this.id).append('\n');
      info.append("Version: ").append(this.version).append('\n');
      if (this.mod instanceof IWDLModDescripted) {
        IWDLModDescripted dmod = (IWDLModDescripted)this.mod;
        String displayName = dmod.getDisplayName();
        String mainAuthor = dmod.getMainAuthor();
        String[] authors = dmod.getAuthors();
        String url = dmod.getURL();
        String description = dmod.getDescription();
        if (displayName != null && !displayName.isEmpty())
          info.append("Display name: ").append(displayName).append('\n'); 
        if (mainAuthor != null && !mainAuthor.isEmpty())
          info.append("Main author: ").append(mainAuthor).append('\n'); 
        if (authors != null && authors.length > 0) {
          info.append("Authors: ");
          for (int k = 0; k < authors.length; k++) {
            if (!authors[k].equals(mainAuthor))
              if (k <= authors.length - 2) {
                info.append(", ");
              } else if (k == authors.length - 1) {
                info.append(", and ");
              } else {
                info.append('\n');
              }  
          } 
        } 
        if (url != null && !url.isEmpty())
          info.append("URL: ").append(url).append('\n'); 
        if (description != null && !description.isEmpty())
          info.append("Description: \n").append(description).append('\n'); 
      } 
      info.append("Main class: ").append(this.mod.getClass().getName()).append('\n');
      info.append("Containing file: ");
      try {
        String path = (new File(this.mod.getClass().getProtectionDomain().getCodeSource().getLocation().toURI())).getPath();
        String username = System.getProperty("user.name");
        path = path.replace(username, "<USERNAME>");
        info.append(path);
      } catch (Exception e) {
        info.append("Unknown (").append(e).append(')');
      } 
      info.append('\n');
      Class<?>[] interfaces = this.mod.getClass().getInterfaces();
      info.append("Implemented interfaces (").append(interfaces.length)
        .append(")\n");
      for (int i = 0; i < interfaces.length; i++)
        info.append(i).append(": ").append(interfaces[i].getName())
          .append('\n'); 
      info.append("Superclass: ")
        .append(this.mod.getClass().getSuperclass().getName()).append('\n');
      ClassLoader loader = this.mod.getClass().getClassLoader();
      info.append("Classloader: ").append(loader);
      if (loader != null)
        info.append(" (").append(loader.getClass().getName()).append(')'); 
      info.append('\n');
      Annotation[] annotations = this.mod.getClass().getAnnotations();
      info.append("Annotations (").append(annotations.length)
        .append(")\n");
      for (int j = 0; j < annotations.length; j++)
        info.append(j).append(": ").append(annotations[j].toString())
          .append(" (")
          .append(annotations[j].annotationType().getName())
          .append(")\n"); 
      return info.toString();
    }
    
    public boolean isEnabled() {
      return WDL.globalProps.getProperty("Extensions." + this.id + ".enabled", "true")
        .equals("true");
    }
    
    public void setEnabled(boolean enabled) {
      WDL.globalProps.setProperty("Extensions." + this.id + ".enabled", 
          Boolean.toString(enabled));
      WDL.saveGlobalProps();
    }
    
    public void toggleEnabled() {
      setEnabled(!isEnabled());
    }
  }
  
  static {
    logger.info("Loading default WDL extensions");
    addWDLMod("Hologram", "1.0", (IWDLMod)new HologramHandler());
    addWDLMod("EntityRealigner", "1.0", (IWDLMod)new EntityRealigner());
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\api\WDLApi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */