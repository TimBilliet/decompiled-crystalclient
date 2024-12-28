package mapwriter.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WorldConfig {
  private static WorldConfig instance = null;
  
  public List<Integer> dimensionList = new ArrayList<>();
  
  private WorldConfig() {
    InitDimensionList();
  }
  
  public static WorldConfig getInstance() {
    if (instance == null)
      synchronized (WorldConfig.class) {
        if (instance == null)
          instance = new WorldConfig(); 
      }  
    return instance;
  }
  
  public void InitDimensionList() {
    this.dimensionList.clear();
    addDimension(0);
    cleanDimensionList();
  }
  
  public void addDimension(int dimension) {
    int i = this.dimensionList.indexOf(dimension);
    if (i < 0)
      this.dimensionList.add(dimension);
  }
  
  public void cleanDimensionList() {
    List<Integer> dimensionListCopy = new ArrayList<>(this.dimensionList);
    this.dimensionList.clear();
    for (Iterator<Integer> iterator = dimensionListCopy.iterator(); iterator.hasNext(); ) {
      int dimension = (Integer) iterator.next();
      addDimension(dimension);
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\config\WorldConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */