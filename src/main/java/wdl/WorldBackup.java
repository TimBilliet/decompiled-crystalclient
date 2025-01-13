package wdl;

import net.minecraft.client.resources.I18n;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class WorldBackup {
    public enum WorldBackupType {
        NONE("wdl.backup.none", ""),
        FOLDER("wdl.backup.folder", "wdl.saveProgress.backingUp.title.folder"),
        ZIP("wdl.backup.zip", "wdl.saveProgress.backingUp.title.zip");

        public final String descriptionKey;

        public final String titleKey;

        WorldBackupType(String descriptionKey, String titleKey) {
            this.descriptionKey = descriptionKey;
            this.titleKey = titleKey;
        }

        public String getDescription() {
            return I18n.format(this.descriptionKey, new Object[0]);
        }

        public String getTitle() {
            return I18n.format(this.titleKey, new Object[0]);
        }

        public static WorldBackupType match(String name) {
            for (WorldBackupType type : values()) {
                if (type.name().equalsIgnoreCase(name))
                    return type;
            }
            return NONE;
        }
    }

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    public static void backupWorld(File worldFolder, String worldName, WorldBackupType type, IBackupProgressMonitor monitor) throws IOException {
        File destination;
        String newWorldName = worldName + "_" + DATE_FORMAT.format(new Date());
        switch (type) {
            case NONE:
                return;
            case FOLDER:
                destination = new File(worldFolder.getParentFile(), newWorldName);
                if (destination.exists())
                    throw new IOException("Backup folder (" + destination + ") already exists!");
                copyDirectory(worldFolder, destination, monitor);
                return;
            case ZIP:
                destination = new File(worldFolder.getParentFile(), newWorldName + ".zip");
                if (destination.exists())
                    throw new IOException("Backup file (" + destination + ") already exists!");
                zipDirectory(worldFolder, destination, monitor);
                return;
        }
    }

    public static interface IBackupProgressMonitor {
        void setNumberOfFiles(int param1Int);

        void onNextFile(String param1String);
    }

    public static void copyDirectory(File src, File destination, IBackupProgressMonitor monitor) throws IOException {
        monitor.setNumberOfFiles(countFilesInFolder(src));
        copy(src, destination, src.getPath().length() + 1, monitor);
    }

    public static void zipDirectory(File src, File destination, IBackupProgressMonitor monitor) throws IOException {
        monitor.setNumberOfFiles(countFilesInFolder(src));
        FileOutputStream outStream = null;
        ZipOutputStream stream = null;
        try {
            outStream = new FileOutputStream(destination);
            try {
                stream = new ZipOutputStream(outStream);
                zipFolder(src, stream, src.getPath().length() + 1, monitor);
            } finally {
                stream.close();
            }
        } finally {
            outStream.close();
        }
    }

    private static void zipFolder(File folder, ZipOutputStream stream, int pathStartIndex, IBackupProgressMonitor monitor) throws IOException {
        for (File file : folder.listFiles()) {
            if (file.isFile()) {
                String name = file.getPath().substring(pathStartIndex);
                monitor.onNextFile(name);
                ZipEntry zipEntry = new ZipEntry(name);
                stream.putNextEntry(zipEntry);
                FileInputStream inputStream = new FileInputStream(file);
                try {
                    IOUtils.copy(inputStream, stream);
                } finally {
                    inputStream.close();
                }
                stream.closeEntry();
            } else if (file.isDirectory()) {
                zipFolder(file, stream, pathStartIndex, monitor);
            }
        }
    }

    private static void copy(File from, File to, int pathStartIndex, IBackupProgressMonitor monitor) throws IOException {
        if (from.isDirectory()) {
            if (!to.exists())
                to.mkdir();
            for (String fileName : from.list())
                copy(new File(from, fileName), new File(to, fileName), pathStartIndex, monitor);
        } else {
            monitor.onNextFile(to.getPath().substring(pathStartIndex));
            FileUtils.copyFile(from, to, true);
        }
    }

    private static int countFilesInFolder(File folder) {
        if (!folder.isDirectory())
            return 0;
        int count = 0;
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                count += countFilesInFolder(file);
            } else {
                count++;
            }
        }
        return count;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\wdl\WorldBackup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */