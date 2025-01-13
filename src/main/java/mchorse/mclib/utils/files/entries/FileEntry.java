package mchorse.mclib.utils.files.entries;

import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.util.Objects;

public class FileEntry extends AbstractEntry {
    public ResourceLocation resource;

    public FileEntry(String title, File file, ResourceLocation resource) {
        super(title, file);
        this.resource = resource;
    }

    public boolean equals(Object obj) {
        boolean result = super.equals(obj);
        if (obj instanceof FileEntry)
            result = (result && Objects.equals(this.resource, ((FileEntry) obj).resource));
        return result;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\files\entries\FileEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */