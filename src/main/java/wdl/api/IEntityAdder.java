package wdl.api;

import java.util.List;

public interface IEntityAdder extends IWDLMod {
    List<String> getModEntities();

    int getDefaultEntityTrackDistance(String paramString);

    String getEntityCategory(String paramString);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\api\IEntityAdder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */