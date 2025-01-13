package co.crystaldev.client.shader;

public enum VertexFormatElement {
    POSITION(3, ElementType.FLOAT),
    TEX(2, ElementType.FLOAT),
    COLOR(4, ElementType.UNSIGNED_BYTE);

    private final int count;

    private final ElementType elementType;

    public int getTotalSize() {
        return this.count * this.elementType.getSize();
    }

    VertexFormatElement(int count, ElementType elementType) {
        this.count = count;
        this.elementType = elementType;
    }

    public int getCount() {
        return this.count;
    }

    public ElementType getElementType() {
        return this.elementType;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\shader\VertexFormatElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */