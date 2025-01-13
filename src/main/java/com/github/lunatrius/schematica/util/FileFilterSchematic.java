package com.github.lunatrius.schematica.util;

import java.io.File;
import java.io.FileFilter;
import java.util.Locale;

public class FileFilterSchematic implements FileFilter {
    private final boolean directory;

    public FileFilterSchematic(boolean dir) {
        this.directory = dir;
    }

    public boolean accept(File file) {
        if (this.directory)
            return file.isDirectory();
        return file.getName().toLowerCase(Locale.ENGLISH).endsWith(".schematic");
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematic\\util\FileFilterSchematic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */