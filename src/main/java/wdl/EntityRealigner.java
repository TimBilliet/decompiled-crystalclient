package wdl;

import net.minecraft.entity.Entity;
import wdl.api.IEntityEditor;
import wdl.api.IWDLModDescripted;

public class EntityRealigner implements IEntityEditor, IWDLModDescripted {
  public boolean isValidEnvironment(String version) {
    return true;
  }
  
  public String getEnvironmentErrorMessage(String version) {
    return null;
  }
  
  public String getDisplayName() {
    return "Entity realigner";
  }
  
  public String getMainAuthor() {
    return "Pokechu22";
  }
  
  public String[] getAuthors() {
    return null;
  }
  
  public String getURL() {
    return null;
  }
  
  public String getDescription() {
    return "Realigns entities to their serverside position to deal with entities that drift clientside (for example, boats).";
  }
  
  public boolean shouldEdit(Entity e) {
    return (e.serverPosX != 0 || e.serverPosY != 0 || e.serverPosZ != 0);
  }
  
  public void editEntity(Entity e) {
    System.out.println("Realigning " + e);
    e.posX = convertServerPos(e.serverPosX);
    e.posY = convertServerPos(e.serverPosY);
    e.posZ = convertServerPos(e.serverPosZ);
    System.out.println("Realigned " + e);
  }
  
  private static double convertServerPos(int serverPos) {
    return serverPos / 32.0D;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\EntityRealigner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */