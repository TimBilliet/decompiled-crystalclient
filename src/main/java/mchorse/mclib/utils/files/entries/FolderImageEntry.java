package mchorse.mclib.utils.files.entries;

import mchorse.mclib.utils.resources.RLUtils;

import java.io.File;
import java.util.Iterator;
import java.util.Objects;

public class FolderImageEntry extends FolderEntry {
  public FolderImageEntry(String title, File file, FolderEntry parent) {
    super(title, file, parent);
  }
  
  protected void populateEntries() {
    String prefix = getPrefix();
    for (File file : this.file.listFiles()) {
      AbstractEntry entry = null;
      String name = file.getName();
      String lowercase = name.toLowerCase();
      if (file.isDirectory()) {
        entry = new FolderImageEntry(name, file, this);
      } else if (file.isFile()) {
        if (lowercase.endsWith(".png") || lowercase.endsWith(".jpg") || lowercase.endsWith(".jpeg") || lowercase.endsWith(".gif")) {
          String path = prefix + (prefix.contains(":") ? "/" : ":") + name;
          entry = new FileEntry(name, file, RLUtils.create(path));
        } 
      } 
      if (!Objects.equals(getEntry(name), entry))
        if (entry != null)
          this.entries.add(entry);  
    } 
    Iterator<AbstractEntry> it = this.entries.iterator();
    while (it.hasNext()) {
      AbstractEntry entry = it.next();
      if (!entry.exists())
        it.remove(); 
    } 
    super.populateEntries();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\files\entries\FolderImageEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */