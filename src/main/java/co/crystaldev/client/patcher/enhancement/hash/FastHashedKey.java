package co.crystaldev.client.patcher.enhancement.hash;

public class FastHashedKey {
    public static int mix64(long input) {
        input = (input ^ input >> 30L) * -4658895280553007687L;
        input = (input ^ input >> 27L) * -7723592293110705685L;
        input ^= input >> 31L;
        return Long.hashCode(input);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\patcher\enhancement\hash\FastHashedKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */