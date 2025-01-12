package co.crystaldev.client.patcher.hook;

import co.crystaldev.client.Reference;
import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.gui.MixinFontRenderer;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.renderer.MixinGlStateManager;
import co.crystaldev.client.patcher.enhancement.EnhancementManager;
import co.crystaldev.client.patcher.enhancement.hash.StringHash;
import co.crystaldev.client.patcher.enhancement.text.CachedString;
import co.crystaldev.client.patcher.enhancement.text.EnhancedFontRenderer;
import co.crystaldev.client.util.ReflectionHelper;
import co.crystaldev.client.util.type.Tuple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

public final class FontRendererHook {
    public static boolean forceRefresh = false;
    private final EnhancedFontRenderer enhancedFontRenderer = (EnhancedFontRenderer) EnhancementManager.getInstance().getEnhancement(EnhancedFontRenderer.class);
    public static final String characterDictionary = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\000\000\000\000\000\000\000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\000";
    private final FontRenderer fontRenderer;
    private final MixinFontRenderer fontRendererAccessor;
    private final Minecraft mc = Minecraft.getMinecraft();
    public int glTextureId = -1;
    private int texSheetDim = 256;
    private float fontTexHeight = (16 * this.texSheetDim + 128);
    private float fontTexWidth = (16 * this.texSheetDim);
    private int regularCharDim = 128;

    private boolean drawing = false;
    private static final String COLOR_RESET_PHRASE = "\u00A7r";
    private static final Field field_textureState = ReflectionHelper.findField(GlStateManager.class, "textureState", "field_179174_p", "p");


    private static final Field field_colorState = ReflectionHelper.findField(GlStateManager.class, "colorState", "field_179170_t", "t");


    private static Field field_TextureState$textureName;


    private static Field field_Color$red;

    private static Field field_Color$green;

    private static Field field_Color$blue;

    private static Field field_Color$alpha;


    public FontRendererHook(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
        this.fontRendererAccessor = (MixinFontRenderer) fontRenderer;
    }

    private void establishSize() {
        int regWidth = 256;
        for (int i = 0; i < 256; i++) {
            try {
                try (InputStream stream = this.mc.getResourceManager().getResource(new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", i))).getInputStream()) {
                    regWidth = ImageIO.read(stream).getWidth();
                }
                break;
            } catch (Exception exception) {
            }
        }


        this.texSheetDim = regWidth;
        int specWidth = 128;

        try (InputStream stream = this.mc.getResourceManager().getResource(this.fontRendererAccessor.getLocationFontTexture()).getInputStream()) {
            specWidth = ImageIO.read(stream).getWidth();
        } catch (IOException ex) {
            Reference.LOGGER.error("Failed to read font texture", ex);
        }

        this.regularCharDim = specWidth;
        this.fontTexHeight = (16 * this.texSheetDim + specWidth);
        this.fontTexWidth = (16 * this.texSheetDim);
    }

    public void create() {
        establishSize();
        forceRefresh = false;

        if (this.glTextureId != -1) {
            GlStateManager.deleteTexture(this.glTextureId);
        }

        BufferedImage bufferedImage = new BufferedImage((int) this.fontTexWidth, (int) this.fontTexHeight, 2);
        for (int i = 0; i < 256; i++) {
            try (InputStream stream = this.mc.getResourceManager().getResource(new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", i))).getInputStream()) {
                bufferedImage.getGraphics().drawImage(ImageIO.read(stream), i / 16 * this.texSheetDim, i % 16 * this.texSheetDim, null);
            } catch (Exception exception) {
            }
        }


        try (InputStream stream = this.mc.getResourceManager().getResource(this.fontRendererAccessor.getLocationFontTexture()).getInputStream()) {
            bufferedImage.getGraphics().drawImage(ImageIO.read(stream), 0, 16 * this.texSheetDim, null);
        } catch (IOException ex) {
            Reference.LOGGER.error("Exception raised while rendering texture sheet", ex);
        }

        this.glTextureId = (new DynamicTexture(bufferedImage)).getGlTextureId();
    }

    private void deleteTextureId() {
        if (this.glTextureId != -1) {
            GlStateManager.deleteTexture(this.glTextureId);
            this.glTextureId = -1;
        }
    }

    public static String clearColorReset(String text) {
        int startIndex = 0;
        int endIndex = text.length();

        while (text.indexOf("\u00A7r", startIndex) == startIndex) {
            startIndex += 2;
        }
        int e;
        while ((e = text.lastIndexOf("\u00A7r", endIndex - 1)) == endIndex - 2 && e != -1) {
            endIndex -= 2;
        }

        if (endIndex < startIndex) endIndex = startIndex;

        return text.substring(startIndex, endIndex);
    }


    public boolean renderStringAtPos(String text, boolean shadow) {
        if (this.fontRendererAccessor.getRenderEngine() == null || !(ClientOptions.getInstance()).optimizedFontRenderer) {
            deleteTextureId();
            return false;
        }

        if (this.glTextureId == -1 || forceRefresh) {
            create();
        }

        text = clearColorReset(text);

        if (text.isEmpty()) {
            return false;
        }

        float posX = this.fontRendererAccessor.getPosX();
        float posY = this.fontRendererAccessor.getPosY();
        this.fontRendererAccessor.setPosY(0.0F);
        this.fontRendererAccessor.setPosX(0.0F);

        float red = this.fontRendererAccessor.getRed();

        float green = this.fontRendererAccessor.getGreen();
        float blue = this.fontRendererAccessor.getBlue();
        float alpha = this.fontRendererAccessor.getAlpha();

        GlStateManager.bindTexture(this.glTextureId);
        GlStateManager.translate(posX, posY, 0.0F);

        Object[] textureStates = (Object[]) getField(field_textureState);
        Object textureState = textureStates[MixinGlStateManager.getActiveTextureUnit()];

        if (field_TextureState$textureName == null) {
            field_TextureState$textureName = ReflectionHelper.findField(textureState.getClass(), "textureName", "field_179059_b", "b");
        }


        StringHash hash = new StringHash(text, red, green, blue, alpha, shadow);
        CachedString cachedString = this.enhancedFontRenderer.get(hash);

        if (cachedString != null) {
            GlStateManager.color(red, green, blue, alpha);
            GlStateManager.callList(cachedString.getListId());


            setField(field_TextureState$textureName, textureState, this.glTextureId);


            Object colorState = getField(field_colorState);

            if (field_Color$red == null) {
                field_Color$red = ReflectionHelper.findField(colorState.getClass(), new String[]{"red", "field_179195_a", "a"});


                field_Color$green = ReflectionHelper.findField(colorState.getClass(), new String[]{"green", "field_179193_b", "b"});


                field_Color$blue = ReflectionHelper.findField(colorState.getClass(), new String[]{"blue", "field_179194_c", "c"});


                field_Color$alpha = ReflectionHelper.findField(colorState.getClass(), new String[]{"alpha", "field_179192_d", "d"});
            }


            setField(field_Color$red, colorState, cachedString.getLastRed());
            setField(field_Color$green, colorState, cachedString.getLastGreen());
            setField(field_Color$blue, colorState, cachedString.getLastBlue());
            setField(field_Color$alpha, colorState, cachedString.getLastAlpha());
            GlStateManager.translate(-posX, -posY, 0.0F);
            GlStateManager.resetColor();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            this.fontRendererAccessor.setPosX(posX + cachedString.getWidth());
            this.fontRendererAccessor.setPosY(posY + cachedString.getHeight());
            return true;
        }

        setField(field_TextureState$textureName, textureState, this.glTextureId);
        GlStateManager.resetColor();

        int list = this.enhancedFontRenderer.getGlList();
        GL11.glNewList(list, 4865);

        boolean obfuscated = false;
        CachedString value = new CachedString(text, list, this.fontRendererAccessor.getPosX() - posX, this.fontRendererAccessor.getPosY() - posY);
        Deque<RenderPair> underline = new LinkedList<>();
        Deque<RenderPair> strikethrough = new LinkedList<>();

        value.setLastRed(red);
        value.setLastGreen(green);
        value.setLastBlue(blue);
        value.setLastAlpha(alpha);

        for (int messageChar = 0; messageChar < text.length(); messageChar++) {
            char letter = text.charAt(messageChar);

            if (letter == '\u00A7' && messageChar + 1 < text.length()) {
                int styleIndex = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(messageChar + 1));

                if (styleIndex < 16) {
                    this.fontRendererAccessor.setStrikethroughStyle(false);
                    this.fontRendererAccessor.setUnderlineStyle(false);
                    this.fontRendererAccessor.setItalicStyle(false);
                    this.fontRendererAccessor.setRandomStyle(false);
                    this.fontRendererAccessor.setBoldStyle(false);

                    if (styleIndex < 0) {
                        styleIndex = 15;
                    }

                    if (shadow) {
                        styleIndex += 16;
                    }

                    int currentColorIndex = this.fontRendererAccessor.getColorCode()[styleIndex];
                    this.fontRendererAccessor.setTextColor(currentColorIndex);

                    float colorRed = (currentColorIndex >> 16) / 255.0F;
                    float colorGreen = (currentColorIndex >> 8 & 0xFF) / 255.0F;
                    float colorBlue = (currentColorIndex & 0xFF) / 255.0F;

                    GlStateManager.color(colorRed, colorGreen, colorBlue, alpha);

                    value.setLastAlpha(alpha);
                    value.setLastGreen(colorGreen);
                    value.setLastBlue(colorBlue);
                    value.setLastRed(colorRed);
                } else if (styleIndex == 16) {
                    this.fontRendererAccessor.setRandomStyle(true);
                    obfuscated = true;
                } else if (styleIndex == 17) {
                    this.fontRendererAccessor.setBoldStyle(true);
                } else if (styleIndex == 18) {
                    this.fontRendererAccessor.setStrikethroughStyle(true);
                } else if (styleIndex == 19) {
                    this.fontRendererAccessor.setUnderlineStyle(true);
                } else if (styleIndex == 20) {
                    this.fontRendererAccessor.setItalicStyle(true);
                } else {
                    this.fontRendererAccessor.setRandomStyle(false);
                    this.fontRendererAccessor.setBoldStyle(false);
                    this.fontRendererAccessor.setStrikethroughStyle(false);
                    this.fontRendererAccessor.setUnderlineStyle(false);
                    this.fontRendererAccessor.setItalicStyle(false);
                    GlStateManager.color(red, green, blue, alpha);

                    value.setLastGreen(green);
                    value.setLastAlpha(alpha);
                    value.setLastBlue(blue);
                    value.setLastRed(red);
                }

                messageChar++;
            } else {
                int obfuscationIndex = (shadow || this.fontRendererAccessor.isRandomStyle()) ? "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\000\000\000\000\000\000\000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\000".indexOf(letter) : 0;

                if (this.fontRendererAccessor.isRandomStyle() && obfuscationIndex != -1) {
                    char charIndex;
                    float charWidthFloat = getCharWidthFloat(letter);


                    do {
                        obfuscationIndex = this.fontRenderer.fontRandom.nextInt("ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\000\000\000\000\000\000\000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\000".length());
                        charIndex = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\000\000\000\000\000\000\000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\000".charAt(obfuscationIndex);
                    } while (charWidthFloat != getCharWidthFloat(charIndex));

                    letter = charIndex;
                }

                boolean unicode = this.fontRenderer.getUnicodeFlag();
                float boldWidth = getBoldOffset(obfuscationIndex);
                boolean small = ((letter == '\000' || obfuscationIndex == -1 || unicode) && shadow);

                if (small) {
                    this.fontRendererAccessor.setPosX(this.fontRendererAccessor.getPosX() - boldWidth);
                    this.fontRendererAccessor.setPosY(this.fontRendererAccessor.getPosY() - boldWidth);
                }

                float effectiveWidth = renderChar(letter, this.fontRendererAccessor.isItalicStyle());
                if (small) {
                    this.fontRendererAccessor.setPosX(this.fontRendererAccessor.getPosX() + boldWidth);
                    this.fontRendererAccessor.setPosY(this.fontRendererAccessor.getPosY() + boldWidth);
                }

                if (this.fontRendererAccessor.isBoldStyle()) {
                    this.fontRendererAccessor.setPosX(this.fontRendererAccessor.getPosX() + boldWidth);

                    if (small) {
                        this.fontRendererAccessor.setPosX(this.fontRendererAccessor.getPosX() - boldWidth);
                        this.fontRendererAccessor.setPosY(this.fontRendererAccessor.getPosY() - boldWidth);
                    }

                    renderChar(letter, this.fontRendererAccessor.isItalicStyle());
                    this.fontRendererAccessor.setPosX(this.fontRendererAccessor.getPosX() - boldWidth);

                    if (small) {
                        this.fontRendererAccessor.setPosX(this.fontRendererAccessor.getPosX() + boldWidth);
                        this.fontRendererAccessor.setPosY(this.fontRendererAccessor.getPosY() + boldWidth);
                    }

                    effectiveWidth++;
                }

                if (this.fontRendererAccessor.isStrikethroughStyle()) {
                    adjustOrAppend(strikethrough, this.fontRendererAccessor.getPosX(), effectiveWidth, value.getLastRed(), value.getLastGreen(), value.getLastBlue(), value.getLastAlpha());
                }

                if (this.fontRendererAccessor.isUnderlineStyle()) {
                    adjustOrAppend(underline, this.fontRendererAccessor.getPosX(), effectiveWidth, value.getLastRed(), value.getLastGreen(), value.getLastBlue(), value.getLastAlpha());
                }


                this.fontRendererAccessor.setPosX(this.fontRendererAccessor.getPosX() + (int) effectiveWidth);
            }
        }

        endDrawing();
        boolean hasStyle = (underline.size() > 0 || strikethrough.size() > 0);

        if (hasStyle) {
            GlStateManager.disableTexture2D();
            GL11.glBegin(7);

            for (RenderPair renderPair : strikethrough) {
                GlStateManager.color(renderPair.red, renderPair.green, renderPair.blue, renderPair.alpha);
                GL11.glVertex2f(renderPair.posX, this.fontRendererAccessor.getPosY() + 4.0F);
                GL11.glVertex2f(renderPair.posX + renderPair.width, this.fontRendererAccessor.getPosY() + 4.0F);
                GL11.glVertex2f(renderPair.posX + renderPair.width, this.fontRendererAccessor.getPosY() + 3.0F);
                GL11.glVertex2f(renderPair.posX, this.fontRendererAccessor.getPosY() + 3.0F);
            }

            for (RenderPair renderPair : underline) {
                GlStateManager.color(renderPair.red, renderPair.green, renderPair.blue, renderPair.alpha);
                GL11.glVertex2f(renderPair.posX - 1.0F, this.fontRendererAccessor.getPosY() + 9.0F);
                GL11.glVertex2f(renderPair.posX + renderPair.width, this.fontRendererAccessor.getPosY() + 9.0F);
                GL11.glVertex2f(renderPair.posX + renderPair.width, this.fontRendererAccessor.getPosY() + 9.0F - 1.0F);
                GL11.glVertex2f(renderPair.posX - 1.0F, this.fontRendererAccessor.getPosY() + 9.0F - 1.0F);
            }

            GL11.glEnd();
            GlStateManager.enableTexture2D();
        }

        GL11.glEndList();


        if (!obfuscated) {
            this.enhancedFontRenderer.cache(hash, value);
        }
        value.setWidth(this.fontRendererAccessor.getPosX());

        this.fontRendererAccessor.setPosY(posY + value.getHeight());
        this.fontRendererAccessor.setPosX(posX + value.getWidth());


        GlStateManager.translate(-posX, -posY, 0.0F);
        return true;
    }

    private void adjustOrAppend(Deque<RenderPair> style, float posX, float effectiveWidth, float lastRed, float lastGreen, float lastBlue, float lastAlpha) {
        RenderPair lastStart = style.peekLast();
        if (lastStart != null && lastStart.red == lastRed && lastStart.green == lastGreen && lastStart.blue == lastBlue && lastStart.alpha == lastAlpha &&
                lastStart.posX + lastStart.width >= posX - 1.0F) {
            lastStart.width = posX + effectiveWidth - lastStart.posX;

            return;
        }
        style.add(new RenderPair(posX, effectiveWidth, lastRed, lastGreen, lastBlue, lastAlpha));
    }

    private float getBoldOffset(int width) {
        return (width == -1 || this.fontRenderer.getUnicodeFlag()) ? 0.5F : getOptifineBoldOffset();
    }

    private float getOptifineBoldOffset() {
        return 1.0F;
    }

    public float renderChar(char ch, boolean italic) {
        if (ch == ' ' || ch == ' ') {
            return this.fontRenderer.getUnicodeFlag() ? 4.0F : getCharWidthFloat(ch);
        }
        int charIndex = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\000\000\000\000\000\000\000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\000".indexOf(ch);
        return (charIndex != -1 && !this.fontRenderer.getUnicodeFlag()) ? renderDefaultChar(charIndex, italic, ch) : renderUnicodeChar(ch, italic);
    }


    private float renderDefaultChar(int characterIndex, boolean italic, char ch) {
        float characterX = (characterIndex % 16 * 8 * this.regularCharDim >> 7) + 0.01F;
        float characterY = (((characterIndex >> 4) * 8 * this.regularCharDim >> 7) + 16 * this.texSheetDim) + 0.01F;

        int italicStyle = italic ? 1 : 0;
        float charWidth = getCharWidthFloat(ch);
        float smallCharWidth = charWidth - 0.01F;

        startDrawing();
        float uvHeight = 7.99F * this.regularCharDim / 128.0F;
        float uvWidth = smallCharWidth * this.regularCharDim / 128.0F;

        GL11.glTexCoord2f(characterX / this.fontTexWidth, characterY / this.fontTexHeight);
        GL11.glVertex2f(this.fontRendererAccessor.getPosX() + italicStyle, this.fontRendererAccessor.getPosY());

        GL11.glTexCoord2f(characterX / this.fontTexWidth, (characterY + uvHeight) / this.fontTexHeight);
        GL11.glVertex2f(this.fontRendererAccessor.getPosX() - italicStyle, this.fontRendererAccessor.getPosY() + 7.99F);

        int offset = this.regularCharDim / 128;
        GL11.glTexCoord2f((characterX + uvWidth - offset) / this.fontTexWidth, (characterY + uvHeight) / this.fontTexHeight);
        GL11.glVertex2f(this.fontRendererAccessor.getPosX() + smallCharWidth - 1.0F - italicStyle, this.fontRendererAccessor.getPosY() + 7.99F);

        GL11.glTexCoord2f((characterX + uvWidth - offset) / this.fontTexWidth, characterY / this.fontTexHeight);
        GL11.glVertex2f(this.fontRendererAccessor.getPosX() + smallCharWidth - 1.0F + italicStyle, this.fontRendererAccessor.getPosY());

        return charWidth;
    }

    private void startDrawing() {
        if (!this.drawing) {
            this.drawing = true;
            GL11.glBegin(7);
        }
    }

    private void endDrawing() {
        if (this.drawing) {
            this.drawing = false;
            GL11.glEnd();
        }
    }

    private Tuple<Float, Float> getUV(char characterIndex) {
        int page = characterIndex / 256;
        int row = page >> 4;
        int column = page % 16;
        int glyphWidth = this.fontRendererAccessor.getGlyphWidth()[characterIndex] >>> 4;
        float charX = (characterIndex % 16 << 4) + glyphWidth + 0.05F * page / 39.0F;
        float charY = (((characterIndex & 0xFF) >> 4) * 16) + 0.05F * page / 39.0F;
        return new Tuple(Float.valueOf(((row * this.texSheetDim) + charX) / this.fontTexWidth), Float.valueOf(((column * this.texSheetDim) + charY) / this.fontTexHeight));
    }


    private float renderUnicodeChar(char ch, boolean italic) {
        if (this.fontRendererAccessor.getGlyphWidth()[ch] == 0) {
            return 0.0F;
        }
        Tuple<Float, Float> uv = getUV(ch);
        int glyphX = this.fontRendererAccessor.getGlyphWidth()[ch] >>> 4;
        int glyphY = this.fontRendererAccessor.getGlyphWidth()[ch] & 0xF;
        float floatGlyphX = glyphX;
        float modifiedY = glyphY + 1.0F;
        float combinedGlyphSize = modifiedY - floatGlyphX - 0.02F;
        float italicStyle = italic ? 1.0F : 0.0F;
        startDrawing();

        float v = 15.98F * this.texSheetDim / 256.0F;
        GL11.glTexCoord2f(((Float) uv.getItem1()).floatValue(), ((Float) uv.getItem2()).floatValue());
        GL11.glVertex2f(this.fontRendererAccessor.getPosX() + italicStyle, this.fontRendererAccessor.getPosY());

        GL11.glTexCoord2f(((Float) uv.getItem1()).floatValue(), ((Float) uv.getItem2()).floatValue() + v / this.fontTexHeight);
        GL11.glVertex2f(this.fontRendererAccessor.getPosX() - italicStyle, this.fontRendererAccessor.getPosY() + 7.99F);

        float texAdj = combinedGlyphSize + 0.5F;
        GL11.glTexCoord2f(((Float) uv.getItem1()).floatValue() + texAdj / this.fontTexHeight, ((Float) uv.getItem2()).floatValue() + v / this.fontTexHeight);
        GL11.glVertex2f(this.fontRendererAccessor.getPosX() + combinedGlyphSize / 2.0F - italicStyle, this.fontRendererAccessor.getPosY() + 7.99F);

        GL11.glTexCoord2f(((Float) uv.getItem1()).floatValue() + texAdj / this.fontTexHeight, ((Float) uv.getItem2()).floatValue());
        GL11.glVertex2f(this.fontRendererAccessor.getPosX() + combinedGlyphSize / 2.0F + italicStyle, this.fontRendererAccessor.getPosY());

        return (modifiedY - floatGlyphX) / 2.0F + 1.0F;
    }


    private float getCharWidthFloat(char c) {
        return this.fontRenderer.getCharWidth(c);
    }


    public int getStringWidth(String text) {
        if (text == null) {
            return 0;
        }

        Map<String, Integer> stringWidthCache = this.enhancedFontRenderer.getStringWidthCache();
        if (stringWidthCache.size() > 5000) {
            stringWidthCache.clear();
        }

        return ((Integer) stringWidthCache.computeIfAbsent(text, width -> Integer.valueOf(getUncachedWidth(text)))).intValue();
    }

    private int getUncachedWidth(String text) {
        if (text == null) {
            return 0;
        }
        float width = 0.0F;
        boolean bold = false;

        for (int messageChar = 0; messageChar < text.length(); messageChar++) {
            char character = text.charAt(messageChar);
            float characterWidth = getCharWidthFloat(character);

            if (characterWidth < 0.0F && messageChar < text.length() - 1) {
                messageChar++;
                character = text.charAt(messageChar);

                if (character != 'l' && character != 'L') {
                    if (character == 'r' || character == 'R') {
                        bold = false;
                    }
                } else {
                    bold = true;
                }

                characterWidth = 0.0F;
            }

            width += characterWidth;

            if (bold && characterWidth > 0.0F) {
                width += getBoldOffset("ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\000\000\000\000\000\000\000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\000".indexOf(character));
            }
        }

        return (int) width;
    }


    private static void setField(Field field, Object obj, Object value) {
        try {
            field.set(obj, value);
        } catch (IllegalAccessException ex) {
            Reference.LOGGER.error("Unable to set field value for field {}", new Object[]{field.getName(), ex});
        }
    }

    private static Object getField(Field field) {
        try {
            return field.get(null);
        } catch (IllegalAccessException ex) {
            Reference.LOGGER.error("Unable to get field value from field {}", new Object[]{field.getName(), ex});
            return null;
        }
    }

    public EnhancedFontRenderer getEnhancedFontRenderer() {
        return this.enhancedFontRenderer;
    }

    static class RenderPair {
        private final float red;
        private final float green;
        private final float blue;
        private final float alpha;
        float posX;
        float width;

        public RenderPair(float posX, float width, float red, float green, float blue, float alpha) {
            this.posX = posX;
            this.width = width;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }
    }
}
