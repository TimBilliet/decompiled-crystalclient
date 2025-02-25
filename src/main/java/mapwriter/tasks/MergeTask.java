package mapwriter.tasks;

import mapwriter.region.MergeToImage;
import mapwriter.region.RegionManager;
import mapwriter.util.Utils;
import net.minecraft.client.resources.I18n;

import java.io.File;

public class MergeTask extends Task {
    final RegionManager regionManager;

    final File outputDir;

    final String basename;

    final int x;

    final int z;

    final int w;

    final int h;

    final int dimension;

    String msg = "";

    public MergeTask(RegionManager regionManager, int x, int z, int w, int h, int dimension, File outputDir, String basename) {
        this.regionManager = regionManager;
        this.x = x;
        this.z = z;
        this.w = w;
        this.h = h;
        this.dimension = dimension;
        this.outputDir = outputDir;
        this.basename = basename;
    }

    public void run() {
        int count = MergeToImage.merge(this.regionManager, this.x, this.z, this.w, this.h, this.dimension, this.outputDir, this.basename);
        if (count > 0) {
            this.msg = I18n.format("mw.task.mergetask.chatmsg.merge.done", new Object[]{this.outputDir});
        } else {
            this.msg = I18n.format("mw.task.mergetask.chatmsg.merge.error", new Object[]{this.outputDir});
        }
    }

    public void onComplete() {
        Utils.printBoth(this.msg);
    }

    public boolean CheckForDuplicate() {
        return false;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mapwriter\tasks\MergeTask.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */