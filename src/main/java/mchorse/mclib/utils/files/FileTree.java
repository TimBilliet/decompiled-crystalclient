package mchorse.mclib.utils.files;

import mchorse.mclib.utils.files.entries.AbstractEntry;
import mchorse.mclib.utils.files.entries.FolderEntry;

import java.util.Comparator;

public abstract class FileTree {
  public static Comparator<AbstractEntry> SORTER = new EntrySorter();
  
  public FolderEntry root = new FolderEntry("root", null, null);
  
  public static void addBackEntry(FolderEntry entry) {
    if (entry.parent == null)
      return; 
    FolderEntry top = new FolderEntry("../", (entry.parent != null) ? entry.parent.file : null, entry);
    top.setTop(entry.parent);
    entry.getRawEntries().add(0, top);
  }
  
  public FolderEntry getEntryForName(String name) {
    for (AbstractEntry entry : this.root.getEntries()) {
      if (entry instanceof FolderEntry && entry.title.equalsIgnoreCase(name))
        return (FolderEntry)entry; 
    } 
    return this.root;
  }
  
  public FolderEntry getByPath(String path) {
    return getByPath(path, this.root);
  }
  
  public FolderEntry getByPath(String path, FolderEntry orDefault) {
    FolderEntry entry = this.root;
    String[] segments = path.trim().split("/");
    for (String segment : segments) {
      for (AbstractEntry folder : entry.getEntries()) {
        if (folder.isFolder() && folder.title.equalsIgnoreCase(segment))
          entry = (FolderEntry)folder; 
      } 
    } 
    return (this.root == entry) ? orDefault : entry;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\files\FileTree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */