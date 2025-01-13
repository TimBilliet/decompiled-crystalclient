package co.crystaldev.client.patcher.enhancement.hash;

import co.crystaldev.client.patcher.enhancement.hash.impl.AbstractHash;

public class StringHash extends AbstractHash {
    public StringHash(String text, float red, float green, float blue, float alpha, boolean shadow) {
        super(new Object[]{text, Float.valueOf(red), Float.valueOf(green), Float.valueOf(blue), Float.valueOf(alpha), Boolean.valueOf(shadow)});
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\patcher\enhancement\hash\StringHash.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */