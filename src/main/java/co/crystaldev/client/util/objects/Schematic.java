package co.crystaldev.client.util.objects;

import co.crystaldev.client.handler.SchematicHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import net.minecraft.util.EnumFacing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Schematic {
  @SerializedName("name")
  private final String name;

  @SerializedName("x")
  private final int x;

  @SerializedName("y")
  private final int y;

  @SerializedName("z")
  private final int z;

  @SerializedName("file")
  private final File file;

  @SerializedName("dir")
  private final String dir;

  @SerializedName("id")
  private final String id;

  @SerializedName("transformations")
  private final List<Transformation> transformations;

  public String toString() {
    return "Schematic(name=" + getName() + ", x=" + getX() + ", y=" + getY() + ", z=" + getZ() + ", file=" + getFile() + ", dir=" + getDir() + ", id=" + getId() + ", transformations=" + getTransformations() + ")";
  }

  public String getName() {
    return this.name;
  }

  public int getX() {
    return this.x;
  }

  public int getY() {
    return this.y;
  }

  public int getZ() {
    return this.z;
  }

  public File getFile() {
    return this.file;
  }

  public String getDir() {
    return this.dir;
  }

  public String getId() {
    return this.id;
  }

  public List<Transformation> getTransformations() {
    return this.transformations;
  }

  public Schematic(File file, String dir, String id, JsonObject obj) {
    this.file = file;
    this.dir = dir;
    this.id = id;
    this.name = obj.get("name").getAsString();
    this.x = obj.get("x").getAsInt();
    this.y = obj.get("y").getAsInt();
    this.z = obj.get("z").getAsInt();
    this.transformations = new ArrayList<>();
    for (JsonElement jsonElement : obj.get("transformations").getAsJsonArray()) {
      JsonObject o = jsonElement.getAsJsonObject();
      EnumFacing facing = EnumFacing.DOWN;
      String ef = o.get("direction").getAsString();
      for (EnumFacing f : EnumFacing.values()) {
        if (f.getName().equals(ef)) {
          facing = f;
          break;
        }
      }
      int x = o.has("x") ? o.get("x").getAsInt() : this.x;
      int y = o.has("y") ? o.get("y").getAsInt() : this.x;
      int z = o.has("z") ? o.get("z").getAsInt() : this.x;
      this.transformations.add(new Transformation(Transformation.Type.fromFmt(o.get("type").getAsString()), facing, x, y, z));
    }
  }

  public Schematic(String dir, String id, JsonObject obj) {
    this(new File(SchematicHandler.getInstance().getSchematicDirectory(), dir), dir, id, obj);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\Schematic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */