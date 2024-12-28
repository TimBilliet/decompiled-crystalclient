package mchorse.mclib.utils.files.entries;

import mchorse.mclib.utils.files.FileTree;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FolderEntry extends AbstractEntry {
  public FolderEntry parent;
  
  public FolderEntry top;
  
  protected List<AbstractEntry> entries = new ArrayList<>();
  
  private long lastModified;
  
  public FolderEntry(String title, File file, FolderEntry parent) {
    super(title, file);
    this.parent = parent;
  }
  
  protected String getPrefix() {
    List<String> joiner = new ArrayList<>();
    FolderEntry parent = this;
    while (parent != null && parent.parent != null) {
      joiner.add(parent.title);
      parent = parent.parent;
    } 
    Collections.reverse(joiner);
    return String.join("/", (Iterable)joiner).replaceFirst("/", ":");
  }
  
  public List<AbstractEntry> getEntries() {
    if (this.top != null)
      return this.top.getEntries(); 
    if (this.file != null) {
      if (hasChanged())
        populateEntries(); 
      this.lastModified = System.currentTimeMillis();
    } 
    return this.entries;
  }
  
  public List<AbstractEntry> getRawEntries() {
    return this.entries;
  }
  
  protected void populateEntries() {
    Collections.sort(this.entries, FileTree.SORTER);
    if (getEntry("../") == null)
      FileTree.addBackEntry(this); 
  }
  
  protected AbstractEntry getEntry(String title) {
    for (AbstractEntry entry : this.entries) {
      if (entry.title.equals(title))
        return entry; 
    } 
    return null;
  }
  
  public void setTop(FolderEntry top) {
    this.top = top;
  }
  
  public boolean hasChanged() {
    if (this.top != null)
      return false; 
    if (this.file != null && this.file.lastModified() > this.lastModified)
      return true; 
    for (AbstractEntry entry : this.entries) {
      if (entry.isFolder()) {
        FolderEntry folder = (FolderEntry)entry;
        if (folder.hasChanged())
          return true; 
      } 
    } 
    return false;
  }
  
  public boolean isEmpty() {
    if (this.entries.size() == 1) {
      AbstractEntry entry = this.entries.get(0);
      if (entry.isFolder() && ((FolderEntry)entry).isTop())
        return true; 
    } 
    return this.entries.isEmpty();
  }
  
  public boolean isTop() {
    return (this.top != null);
  }
  
  public boolean equals(Object obj) {
    boolean result = super.equals(obj);
    if (obj instanceof FolderEntry)
      result = (result && Objects.equals(this.parent, ((FolderEntry)obj).parent)); 
    return result;
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\files\entries\FolderEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */