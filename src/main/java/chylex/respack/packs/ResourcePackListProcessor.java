package chylex.respack.packs;

import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.resources.ResourcePackListEntryFound;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class ResourcePackListProcessor {
  public static final Comparator<ResourcePackListEntry> sortAZ;
  
  public static final Comparator<ResourcePackListEntry> sortZA;
  
  private final List<ResourcePackListEntry> sourceList;
  
  private final List<ResourcePackListEntry> targetList;
  
  private Comparator<ResourcePackListEntry> sorter;
  
  private Pattern textFilter;
  
  private static String name(ResourcePackListEntry entry) {
    if (entry instanceof ResourcePackListEntryCustom)
      return ((ResourcePackListEntryCustom)entry).getResourcePackName(); 
    if (entry instanceof ResourcePackListEntryFound)
      return ((ResourcePackListEntryFound)entry).func_148318_i().getResourcePackName();
//      return ((ResourcePackListEntryFound)entry).getResourcePackEntry().getResourcePackName();
    return "<INVALID>";
  }
  
  private static String nameSort(ResourcePackListEntry entry, boolean reverse) {
    String pfx1 = !reverse ? "a" : "z";
    String pfx2 = !reverse ? "b" : "z";
    String pfx3 = !reverse ? "z" : "a";
    if (entry instanceof ResourcePackListEntryFolder) {
      ResourcePackListEntryFolder folder = (ResourcePackListEntryFolder)entry;
      return folder.isUp ? (pfx1 + folder.folderName) : (pfx2 + folder.folderName);
    } 
    if (entry instanceof ResourcePackListEntryCustom)
      return pfx3 + ((ResourcePackListEntryCustom)entry).getResourcePackName(); 
    if (entry instanceof ResourcePackListEntryFound)
      return pfx3 + ((ResourcePackListEntryFound)entry).func_148318_i().getResourcePackName();
    return pfx3 + "<INVALID>";
  }
  
  private static String description(ResourcePackListEntry entry) {
    if (entry instanceof ResourcePackListEntryCustom)
      return ((ResourcePackListEntryCustom)entry).getResourcePackDescription(); 
    if (entry instanceof ResourcePackListEntryFound)
      return ((ResourcePackListEntryFound)entry).func_148318_i().getTexturePackDescription();
    return "<INVALID>";
  }
  
  static {
    sortAZ = ((entry1, entry2) -> String.CASE_INSENSITIVE_ORDER.compare(nameSort(entry1, false), nameSort(entry2, false)));
//    sortZA = ((entry1, entry2) -> -String.CASE_INSENSITIVE_ORDER.compare((T)nameSort(entry1, true), (T)nameSort(entry2, true)));
    sortZA = ((entry1, entry2) -> -String.CASE_INSENSITIVE_ORDER.compare(nameSort(entry1, true), nameSort(entry2, true)));
  }
  
  public ResourcePackListProcessor(List<ResourcePackListEntry> sourceList, List<ResourcePackListEntry> targetList) {
    this.sourceList = sourceList;
    this.targetList = targetList;
    refresh();
  }
  
  public void setSorter(Comparator<ResourcePackListEntry> comparator) {
    this.sorter = comparator;
    refresh();
  }
  
  public void setFilter(String text) {
    if (text == null || text.isEmpty()) {
      this.textFilter = null;
    } else {
      this.textFilter = Pattern.compile("\\Q" + text.replace("*", "\\E.*\\Q") + "\\E", 2);
    } 
    refresh();
  }
  
  public void refresh() {
    this.targetList.clear();
    for (ResourcePackListEntry entry : this.sourceList) {
      if (checkFilter(name(entry)) || checkFilter(description(entry)))
        this.targetList.add(entry); 
    } 
    if (this.sorter != null)
      this.targetList.sort(this.sorter); 
  }
  
  private boolean checkFilter(String entryText) {
    return (this.textFilter == null || this.textFilter.matcher(entryText.toLowerCase(Locale.ENGLISH)).find());
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\chylex\chylex.respack\packs\ResourcePackListProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */