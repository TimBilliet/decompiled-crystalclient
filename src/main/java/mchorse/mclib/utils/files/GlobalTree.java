package mchorse.mclib.utils.files;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GlobalTree extends FileTree {
  public static final GlobalTree TREE = new GlobalTree();
  
  protected List<FileTree> trees = new ArrayList<>();
  
  public void register(FileTree tree) {
    this.trees.add(tree);
    this.root.getEntries().add(tree.root);
    tree.root.parent = this.root;
  }
  
  public List<FileTree> getTrees() {
    return Collections.unmodifiableList(this.trees);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\files\GlobalTree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */