package mchorse.mclib.utils.resources;

import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;

public class TextureLocation extends ResourceLocation {
  public TextureLocation(String domain, String path) {
    super(domain, path);
    set(domain, path);
  }
  
  public TextureLocation(String string) {
    super(string);
    set(string);
  }
  
  public void set(String location) {
    String[] split = location.split(":");
    String domain = (split.length > 0) ? split[0] : "minecraft";
    String path = (split.length > 1) ? split[1] : "";
    set(domain, path);
  }
  
  public void set(String domain, String path) {
    Field[] fields = ResourceLocation.class.getDeclaredFields();
    for (Field field : fields) {
      try {
        unlockField(field);
      } catch (Exception e) {
        e.printStackTrace();
      } 
    } 
    try {
      fields[0].set(this, domain);
      fields[1].set(this, path);
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  protected void unlockField(Field field) throws Exception {
    field.setAccessible(true);
    Field modifiers = Field.class.getDeclaredField("modifiers");
    modifiers.setAccessible(true);
    modifiers.setInt(field, field.getModifiers() & 0xFFFFFFEF);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\resources\TextureLocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */