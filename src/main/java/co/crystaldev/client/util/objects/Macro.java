package co.crystaldev.client.util.objects;

import com.google.gson.annotations.SerializedName;
import net.minecraft.client.Minecraft;

public class Macro {
    @SerializedName("name")
    private final String name;

    @SerializedName("action")
    private String action;

    @SerializedName("keybinding")
    private int keybinding;

    public String toString() {
        return "Macro(name=" + getName() + ", action=" + getAction() + ", keybinding=" + getKeybinding() + ", enabled=" + isEnabled() + ", deleted=" + isDeleted() + ")";
    }

    public String getName() {
        return this.name;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getKeybinding() {
        return this.keybinding;
    }

    public void setKeybinding(int keybinding) {
        this.keybinding = keybinding;
    }

    @SerializedName("enabled")
    private boolean enabled = true;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private transient boolean deleted = false;

    public boolean isDeleted() {
        return this.deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isEnabled() {
        return (this.enabled && !this.deleted);
    }

    public Macro(String name, String action, int keybinding) {
        this.name = name;
        this.action = action;
        this.keybinding = keybinding;
    }

    public void execute() {
        if (this.action != null)
            (Minecraft.getMinecraft()).thePlayer.sendChatMessage(this.action);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\Macro.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */