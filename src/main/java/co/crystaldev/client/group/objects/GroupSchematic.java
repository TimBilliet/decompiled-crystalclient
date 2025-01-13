package co.crystaldev.client.group.objects;

import com.google.gson.annotations.SerializedName;

public class GroupSchematic {
    @SerializedName("dir")
    private String dir;

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    public String getDir() {
        return this.dir;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public GroupSchematic(String name, String dir, String id) {
        this.name = name;
        this.dir = dir;
        this.id = id;
    }

    public GroupSchematic() {
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\group\objects\GroupSchematic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */