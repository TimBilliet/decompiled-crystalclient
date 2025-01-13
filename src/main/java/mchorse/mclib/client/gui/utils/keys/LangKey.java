package mchorse.mclib.client.gui.utils.keys;

import net.minecraft.client.resources.I18n;

public class LangKey implements IKey {
    public static long lastTime;

    public String key;

    public String string;

    public long time = -1L;

    public Object[] args = new Object[0];

    public LangKey(String key) {
        this.key = key;
    }

    public LangKey args(Object... args) {
        this.args = args;
        return this;
    }

    public String update() {
        this.time = -1L;
        return get();
    }

    public String get() {
        if (lastTime > this.time) {
            this.time = lastTime;
            this.string = I18n.format(this.key, this.args);
        }
        return this.string;
    }

    public void set(String string) {
        this.key = string;
        this.string = I18n.format(this.key, new Object[0]);
    }

    public String toString() {
        return get();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mclib\client\gu\\utils\keys\LangKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */