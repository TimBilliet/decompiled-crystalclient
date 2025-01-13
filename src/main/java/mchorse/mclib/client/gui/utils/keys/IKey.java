package mchorse.mclib.client.gui.utils.keys;

public interface IKey {
    public static final IKey EMPTY = new StringKey("");

    static IKey lang(String key) {
        return new LangKey(key);
    }

    static IKey format(String key, Object... args) {
        return (new LangKey(key)).args(args);
    }

    static IKey str(String key) {
        return new StringKey(key);
    }

    static IKey comp(IKey... keys) {
        return new CompoundKey(keys);
    }

    String get();

    void set(String paramString);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mclib\client\gu\\utils\keys\IKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */