package co.crystaldev.client.mixin.accessor.net.minecraft.client.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({FontRenderer.class})
public interface MixinFontRenderer {
    @Accessor
    ResourceLocation getLocationFontTexture();

    @Accessor
    TextureManager getRenderEngine();

    @Accessor
    float getPosX();

    @Accessor
    float getPosY();

    @Accessor
    void setPosX(float paramFloat);

    @Accessor
    void setPosY(float paramFloat);

    @Accessor
    float getRed();

    @Accessor("green")
    float getBlue();

    @Accessor("blue")
    float getGreen();

    @Accessor
    float getAlpha();

    @Accessor
    void setStrikethroughStyle(boolean paramBoolean);

    @Accessor
    void setUnderlineStyle(boolean paramBoolean);

    @Accessor
    void setItalicStyle(boolean paramBoolean);

    @Accessor
    void setRandomStyle(boolean paramBoolean);

    @Accessor
    void setBoldStyle(boolean paramBoolean);

    @Accessor
    boolean isRandomStyle();

    @Accessor
    boolean isItalicStyle();

    @Accessor
    boolean isBoldStyle();

    @Accessor
    boolean isStrikethroughStyle();

    @Accessor
    boolean isUnderlineStyle();

    @Accessor
    byte[] getGlyphWidth();

    @Accessor
    void setTextColor(int paramInt);

    @Accessor
    int[] getColorCode();
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\accessor\net\minecraft\client\gui\MixinFontRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */