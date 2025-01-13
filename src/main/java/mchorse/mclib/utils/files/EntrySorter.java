package mchorse.mclib.utils.files;

import mchorse.mclib.utils.files.entries.AbstractEntry;

import java.util.Comparator;

public class EntrySorter implements Comparator<AbstractEntry> {
    public int compare(AbstractEntry o1, AbstractEntry o2) {
        if (o1 instanceof mchorse.mclib.utils.files.entries.FolderEntry && o2 instanceof mchorse.mclib.utils.files.entries.FileEntry)
            return -1;
        if (o1 instanceof mchorse.mclib.utils.files.entries.FileEntry && o2 instanceof mchorse.mclib.utils.files.entries.FolderEntry)
            return 1;
        return o1.title.compareToIgnoreCase(o2.title);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\files\EntrySorter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */