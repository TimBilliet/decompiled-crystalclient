package wdl.api;

import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;

public interface ISpecialEntityHandler extends IWDLMod {
    Multimap<String, String> getSpecialEntities();

    String getSpecialEntityName(Entity paramEntity);

    String getSpecialEntityCategory(String paramString);

    int getSpecialEntityTrackDistance(String paramString);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\api\ISpecialEntityHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */