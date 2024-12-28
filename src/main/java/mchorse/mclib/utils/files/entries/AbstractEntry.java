package mchorse.mclib.utils.files.entries;

import java.io.File;
import java.util.Objects;

public abstract class AbstractEntry {
  public String title;
  
  public File file;
  
  public AbstractEntry(String title, File file) {
    this.title = title;
    this.file = file;
  }
  
  public boolean isFolder() {
    return this instanceof FolderEntry;
  }
  
  public boolean exists() {
    return (this.file == null || this.file.exists());
  }
  
  public boolean equals(Object obj) {
    if (obj instanceof AbstractEntry) {
      AbstractEntry entry = (AbstractEntry)obj;
      return (Objects.equals(this.title, entry.title) && Objects.equals(this.file, entry.file));
    } 
    return super.equals(obj);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\files\entries\AbstractEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */