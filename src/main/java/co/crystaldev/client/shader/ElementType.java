package co.crystaldev.client.shader;

public enum ElementType {
    FLOAT(4, 5126, false),
    UNSIGNED_BYTE(1, 5121, true);

    private final int size;

    private final int glType;

    private final boolean normalize;

    ElementType(int size, int glType, boolean normalize) {
        this.size = size;
        this.glType = glType;
        this.normalize = normalize;
    }

    public int getSize() {
        return this.size;
    }

    public int getGlType() {
        return this.glType;
    }

    public boolean isNormalize() {
        return this.normalize;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\shader\ElementType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */