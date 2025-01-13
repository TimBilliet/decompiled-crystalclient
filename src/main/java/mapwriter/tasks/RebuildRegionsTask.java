package mapwriter.tasks;

import mapwriter.MapWriterMod;
import mapwriter.region.BlockColors;
import mapwriter.region.RegionManager;
import mapwriter.util.Utils;
import net.minecraft.client.resources.I18n;

public class RebuildRegionsTask extends Task {
    final RegionManager regionManager;

    final BlockColors blockColours;

    final int x;

    final int z;

    final int w;

    final int h;

    final int dimension;

    public RebuildRegionsTask(MapWriterMod mapWriterMod, int x, int z, int w, int h, int dimension) {
        this.regionManager = mapWriterMod.regionManager;
        this.blockColours = mapWriterMod.blockColours;
        this.x = x;
        this.z = z;
        this.w = w;
        this.h = h;
        this.dimension = dimension;
    }

    public void run() {
        this.regionManager.blockColours = this.blockColours;
        this.regionManager.rebuildRegions(this.x, this.z, this.w, this.h, this.dimension);
    }

    public void onComplete() {
        Utils.printBoth(I18n.format("mw.task.rebuildregionstask.chatmsg.rebuild.complete", new Object[0]));
    }

    public boolean CheckForDuplicate() {
        return false;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\tasks\RebuildRegionsTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */