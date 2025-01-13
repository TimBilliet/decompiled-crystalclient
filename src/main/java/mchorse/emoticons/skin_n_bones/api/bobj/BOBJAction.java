package mchorse.emoticons.skin_n_bones.api.bobj;

import java.util.HashMap;
import java.util.Map;

public class BOBJAction {
    public String name;

    public Map<String, BOBJGroup> groups = new HashMap<>();

    public BOBJAction(String name) {
        this.name = name;
    }

    public int getDuration() {
        int max = 0;
        for (BOBJGroup group : this.groups.values())
            max = Math.max(max, group.getDuration());
        return max;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\bobj\BOBJAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */