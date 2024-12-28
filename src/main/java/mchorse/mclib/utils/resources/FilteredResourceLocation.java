package mchorse.mclib.utils.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class FilteredResourceLocation implements IWritableLocation {
  public static final int DEFAULT_COLOR = -1;
  
  public ResourceLocation path;
  
  public int color = -1;
  
  public float scale = 1.0F;
  
  public boolean scaleToLargest;
  
  public int shiftX;
  
  public int shiftY;
  
  public int pixelate = 1;
  
  public boolean erase;
  
  public static FilteredResourceLocation from(NBTBase base) {
    try {
      FilteredResourceLocation location = new FilteredResourceLocation();
      location.fromNbt(base);
      return location;
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public static FilteredResourceLocation from(JsonElement element) {
    try {
      FilteredResourceLocation location = new FilteredResourceLocation();
      location.fromJson(element);
      return location;
    } catch (Exception exception) {
      return null;
    } 
  }
  
  public FilteredResourceLocation(ResourceLocation path) {
    this.path = path;
  }
  
  public String toString() {
    return (this.path == null) ? "" : this.path.toString();
  }
  
  public boolean equals(Object obj) {
    if (super.equals(obj))
      return true; 
    if (obj instanceof FilteredResourceLocation) {
      FilteredResourceLocation frl = (FilteredResourceLocation)obj;
      return (Objects.equals(this.path, frl.path) && this.scaleToLargest == frl.scaleToLargest && this.color == frl.color && this.scale == frl.scale && this.shiftX == frl.shiftX && this.shiftY == frl.shiftY && this.pixelate == frl.pixelate && this.erase == frl.erase);
    } 
    return false;
  }
  
  public int hashCode() {
    int hashCode = this.path.hashCode();
    hashCode = 31 * hashCode + (this.scaleToLargest ? 1 : 0);
    hashCode = 31 * hashCode + this.color;
    hashCode = 31 * hashCode + (int)(this.scale * 1000.0F);
    hashCode = 31 * hashCode + this.shiftX;
    hashCode = 31 * hashCode + this.shiftY;
    hashCode = 31 * hashCode + this.pixelate;
    hashCode = 31 * hashCode + (this.erase ? 1 : 0);
    return hashCode;
  }
  
  public boolean isDefault() {
    return (this.color == -1 && !this.scaleToLargest && this.scale == 1.0F && this.shiftX == 0 && this.shiftY == 0 && this.pixelate <= 1 && !this.erase);
  }
  
  public void fromNbt(NBTBase nbt) throws Exception {
    if (nbt instanceof net.minecraft.nbt.NBTTagString) {
      this.path = RLUtils.create(nbt);
      return;
    } 
    NBTTagCompound tag = (NBTTagCompound)nbt;
    this.path = RLUtils.create(tag.getString("Path"));
    if (tag.hasKey("Color"))
      this.color = tag.getInteger("Color"); 
    if (tag.hasKey("Scale"))
      this.scale = tag.getFloat("Scale"); 
    if (tag.hasKey("ScaleToLargest"))
      this.scaleToLargest = tag.getBoolean("ScaleToLargest"); 
    if (tag.hasKey("ShiftX"))
      this.shiftX = tag.getInteger("ShiftX"); 
    if (tag.hasKey("ShiftY"))
      this.shiftY = tag.getInteger("ShiftY"); 
    if (tag.hasKey("Pixelate"))
      this.pixelate = tag.getInteger("Pixelate"); 
    if (tag.hasKey("Erase"))
      this.erase = tag.getBoolean("Erase"); 
  }
  
  public void fromJson(JsonElement element) throws Exception {
    if (element.isJsonPrimitive()) {
      this.path = RLUtils.create(element);
      return;
    } 
    JsonObject object = element.getAsJsonObject();
    this.path = RLUtils.create(object.get("path").getAsString());
    if (object.has("color"))
      this.color = object.get("color").getAsInt(); 
    if (object.has("scale"))
      this.scale = object.get("scale").getAsFloat(); 
    if (object.has("scaleToLargest"))
      this.scaleToLargest = object.get("scaleToLargest").getAsBoolean(); 
    if (object.has("shiftX"))
      this.shiftX = object.get("shiftX").getAsInt(); 
    if (object.has("shiftY"))
      this.shiftY = object.get("shiftY").getAsInt(); 
    if (object.has("pixelate"))
      this.pixelate = object.get("pixelate").getAsInt(); 
    if (object.has("erase"))
      this.erase = object.get("erase").getAsBoolean(); 
  }
  
  public NBTBase writeNbt() {
    NBTTagCompound tag = new NBTTagCompound();
    tag.setString("Path", toString());
    if (this.color != -1)
      tag.setInteger("Color", this.color); 
    if (this.scale != 1.0F)
      tag.setFloat("Scale", this.scale); 
    if (this.scaleToLargest)
      tag.setBoolean("ScaleToLargest", this.scaleToLargest); 
    if (this.shiftX != 0)
      tag.setInteger("ShiftX", this.shiftX); 
    if (this.shiftY != 0)
      tag.setInteger("ShiftY", this.shiftY); 
    if (this.pixelate > 1)
      tag.setInteger("Pixelate", this.pixelate); 
    if (this.erase)
      tag.setBoolean("Erase", this.erase); 
    return (NBTBase)tag;
  }
  
  public JsonElement writeJson() {
    JsonObject object = new JsonObject();
    object.addProperty("path", toString());
    if (this.color != -1)
      object.addProperty("color", Integer.valueOf(this.color)); 
    if (this.scale != 1.0F)
      object.addProperty("scale", Float.valueOf(this.scale)); 
    if (this.scaleToLargest)
      object.addProperty("scaleToLargest", Boolean.valueOf(this.scaleToLargest)); 
    if (this.shiftX != 0)
      object.addProperty("shiftX", Integer.valueOf(this.shiftX)); 
    if (this.shiftY != 0)
      object.addProperty("shiftY", Integer.valueOf(this.shiftY)); 
    if (this.pixelate > 1)
      object.addProperty("pixelate", Integer.valueOf(this.pixelate)); 
    if (this.erase)
      object.addProperty("erase", Boolean.valueOf(this.erase)); 
    return (JsonElement)object;
  }
  
  public ResourceLocation clone() {
    return RLUtils.clone(this.path);
  }
  
  public FilteredResourceLocation copy() {
    return from(writeNbt());
  }
  
  public FilteredResourceLocation() {}
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\resources\FilteredResourceLocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */