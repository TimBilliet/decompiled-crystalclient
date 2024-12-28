package co.crystaldev.client.font;

import java.awt.Font;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;


public final class FontRenderer
        implements IFontRenderer {
    private int kerning = 0;

    public int getKerning() {
        return this.kerning;
    }

    public void setKerning(int kerning) {
        this.kerning = kerning;
    }


    private final FontData fontData = new FontData();

    public FontData getFontData() {
        return this.fontData;
    }

    private final FontData boldFont = new FontData();
    private final FontData italicFont = new FontData();
    private final FontData boldItalicFont = new FontData();

    private final int[] colorCode = new int[32];

    private final String colors = "0123456789abcdefklmnor";

    private int fontHeight = -1;

    public FontRenderer() {
        for (int index = 0; index < 32; index++) {
            int noClue = (index >> 3 & 0x1) * 85;
            int red = (index >> 2 & 0x1) * 170 + noClue;
            int green = (index >> 1 & 0x1) * 170 + noClue;
            int blue = (index & 0x1) * 170 + noClue;

            if (index == 6) {
                red += 85;
            }

            if (index >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }

            this.colorCode[index] = (red & 0xFF) << 16 | (green & 0xFF) << 8 | blue & 0xFF;
        }
    }


    public int drawString(FontData fontData, String text, int x, int y, int color) {
        if (!fontData.hasFont()) {
            return 0;
        }
        boolean wasBlend = GL11.glGetBoolean(3042);

        GL11.glPushMatrix();
        GL11.glEnable(3042);
        fontData.bind();
        glColor(color);
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if (fontData.hasBounds(character)) {
                CharacterData area = fontData.getCharacterBounds(character);
                drawTextureRect(x, y, area.width, area.height, (float) area.x / fontData
                        .getTextureWidth(), (float) area.y / fontData
                        .getTextureHeight(), (float) (area.x + area.width) / fontData
                        .getTextureWidth(), (float) (area.y + area.height) / fontData
                        .getTextureHeight());
                x += area.width + this.kerning;
            }
        }

        if (!wasBlend) GL11.glDisable(3042);
        GL11.glPopMatrix();
        return x;
    }


    public int drawString(String text, int x, int y, int color) {
        return drawString(text, x, y, color, false);
    }

    public int drawStringWithShadow(String text, int x, int y, int color) {
        return Math.max(drawString(text, x + 1, y + 1, color, true), drawString(text, x, y, color, false));
    }

    public void drawCenteredStringWithShadow(String text, int x, int y, int color) {
        drawStringWithShadow(text, x - getStringWidth(text) / 2, y - getStringHeight(text) / 2, color);
    }

    public void drawCenteredString(String text, int x, int y, int color) {
        drawString(text, x - getStringWidth(text) / 2, y - getStringHeight(text) / 2, color);
    }

    public float drawString(String text, float x, float y, int color) {
        return drawString(text, x, y, color, false);
    }

    public float drawStringWithShadow(String text, float x, float y, int color) {
        return Math.max(drawString(text, x + 1.0F, y + 1.0F, color, true), drawString(text, x, y, color, false));
    }

    public void drawCenteredStringWithShadow(String text, float x, float y, int color) {
        drawStringWithShadow(text, x - getStringWidth(text) / 2.0F, y - getStringHeight(text) / 2.0F, color);
    }

    public void drawCenteredString(String text, float x, float y, int color) {
        drawString(text, x - getStringWidth(text) / 2.0F, y - getStringHeight(text) / 2.0F, color);
    }

    public int drawString(String text, int x, int y, int color, boolean shadow) {
        if (text == null)
            return 0;
        if (color == 553648127) {
            color = 16777215;
        }
        if ((color & 0xFC000000) == 0) {
            color |= 0xFF000000;
        }

        if (shadow) {
            color = (color & 0xFCFCFC) >> 2 | color & 0xFF000000;
        }

        FontData currentFont = this.fontData;
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        boolean randomCase = false, bold = false;
        boolean italic = false, strikethrough = false;
        boolean underline = false;

        x = (int) (x * 2.0F);
        y = (int) (y * 2.0F);
        GL11.glPushMatrix();
        GL11.glScalef(0.5F, 0.5F, 0.5F);

        boolean alphaState = GL11.glGetBoolean(3008);
        boolean blendState = GL11.glGetBoolean(3042);

        GL11.glEnable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, alpha);


        int size = text.length();
        currentFont.bind();
        for (int i = 0; i < size; i++) {
            char character = text.charAt(i);
            if (character == '§' && i + 1 < size) {
                int colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                    randomCase = false;
                    underline = false;
                    strikethrough = false;
                    currentFont = this.fontData;
                    currentFont.bind();

                    if (colorIndex < 0) {
                        colorIndex = 15;
                    }

                    if (shadow) {
                        colorIndex += 16;
                    }

                    int colorcode = this.colorCode[colorIndex];
                    GL11.glColor4f((colorcode >> 16 & 0xFF) / 255.0F, (colorcode >> 8 & 0xFF) / 255.0F, (colorcode & 0xFF) / 255.0F, alpha);
                } else if (colorIndex == 16) {
                    randomCase = true;
                } else if (colorIndex == 17) {
                    bold = true;
                    if (italic) {
                        currentFont = this.boldItalicFont;
                    } else {
                        currentFont = this.boldFont;
                    }
                    currentFont.bind();
                } else if (colorIndex == 18) {
                    strikethrough = true;
                } else if (colorIndex == 19) {
                    underline = true;
                } else if (colorIndex == 20) {

                    italic = true;
                    if (bold) {
                        currentFont = this.boldItalicFont;
                    } else {
                        currentFont = this.italicFont;
                    }
                    currentFont.bind();
                } else {
                    bold = false;
                    italic = false;
                    randomCase = false;
                    underline = false;
                    strikethrough = false;
                    GL11.glColor4f((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, alpha);
                    currentFont = this.fontData;
                    currentFont.bind();
                }
                i++;
            } else if (currentFont.hasBounds(character)) {
                if (randomCase) {
                    char newChar = Character.MIN_VALUE;
                    while ((currentFont.getCharacterBounds(newChar)).width != (currentFont.getCharacterBounds(character)).width)
                        newChar = (char) (int) (Math.random() * 256.0D);
                    character = newChar;
                }
                CharacterData area = currentFont.getCharacterBounds(character);
                drawTextureRect(x, y, area.width, area.height, (float) area.x / currentFont
                        .getTextureWidth(), (float) area.y / currentFont
                        .getTextureHeight(), (float) (area.x + area.width) / currentFont
                        .getTextureWidth(), (float) (area.y + area.height) / currentFont
                        .getTextureHeight());
                if (strikethrough)
                    drawLine(x, y + area.height / 4.0F + 2.0F, x + area.width / 2.0F, y + area.height / 4.0F + 2.0F, 1.0F);
                if (underline)
                    drawLine(x, y + area.height / 2.0F, x + area.width / 2.0F, y + area.height / 2.0F, 1.0F);
                x += area.width + this.kerning;
            }
        }


        if (!alphaState) GL11.glDisable(3008);
        if (!blendState) GL11.glDisable(3042);
        GL11.glPopMatrix();
        return x;
    }

    public float drawString(String text, float x, float y, int color, boolean shadow) {
        if (text == null)
            return 0.0F;
        if (color == 553648127) {
            color = 16777215;
        }
        if ((color & 0xFC000000) == 0) {
            color |= 0xFF000000;
        }

        if (shadow) {
            color = (color & 0xFCFCFC) >> 2 | color & 0xFF000000;
        }

        FontData currentFont = this.fontData;
        float alpha = (color >> 24 & 0xFF) / 255.0F;
        boolean randomCase = false, bold = false;
        boolean italic = false, strikethrough = false;
        boolean underline = false;

        x *= 2.0F;
        y *= 2.0F;
        GL11.glPushMatrix();
        GL11.glScalef(0.5F, 0.5F, 0.5F);

        boolean alphaState = GL11.glGetBoolean(3008);
        boolean blendState = GL11.glGetBoolean(3042);

        GL11.glEnable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, alpha);


        int size = text.length();
        currentFont.bind();
        for (int i = 0; i < size; i++) {
            char character = text.charAt(i);
            if (character == '§' && i + 1 < size) {
                int colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                    randomCase = false;
                    underline = false;
                    strikethrough = false;
                    currentFont = this.fontData;
                    currentFont.bind();

                    if (colorIndex < 0) {
                        colorIndex = 15;
                    }

                    if (shadow) {
                        colorIndex += 16;
                    }

                    int colorcode = this.colorCode[colorIndex];
                    GL11.glColor4f((colorcode >> 16 & 0xFF) / 255.0F, (colorcode >> 8 & 0xFF) / 255.0F, (colorcode & 0xFF) / 255.0F, alpha);
                } else if (colorIndex == 16) {
                    randomCase = true;
                } else if (colorIndex == 17) {
                    bold = true;
                    if (italic) {
                        currentFont = this.boldItalicFont;
                    } else {
                        currentFont = this.boldFont;
                    }
                    currentFont.bind();
                } else if (colorIndex == 18) {
                    strikethrough = true;
                } else if (colorIndex == 19) {
                    underline = true;
                } else if (colorIndex == 20) {

                    italic = true;
                    if (bold) {
                        currentFont = this.boldItalicFont;
                    } else {
                        currentFont = this.italicFont;
                    }
                    currentFont.bind();
                } else {
                    bold = false;
                    italic = false;
                    randomCase = false;
                    underline = false;
                    strikethrough = false;
                    GL11.glColor4f((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, alpha);
                    currentFont = this.fontData;
                    currentFont.bind();
                }
                i++;
            } else if (currentFont.hasBounds(character)) {
                if (randomCase) {
                    char newChar = Character.MIN_VALUE;
                    while ((currentFont.getCharacterBounds(newChar)).width != (currentFont.getCharacterBounds(character)).width)
                        newChar = (char) (int) (Math.random() * 256.0D);
                    character = newChar;
                }
                CharacterData area = currentFont.getCharacterBounds(character);
                drawTextureRect(x, y, area.width, area.height, (float) area.x / currentFont
                        .getTextureWidth(), (float) area.y / currentFont
                        .getTextureHeight(), (float) (area.x + area.width) / currentFont
                        .getTextureWidth(), (float) (area.y + area.height) / currentFont
                        .getTextureHeight());
                if (strikethrough)
                    drawLine(x, y + area.height / 4.0F + 2.0F, x + area.width / 2.0F, y + area.height / 4.0F + 2.0F, 1.0F);
                if (underline)
                    drawLine(x, y + area.height / 2.0F, x + area.width / 2.0F, y + area.height / 2.0F, 1.0F);
                x += (area.width + this.kerning);
            }
        }


        if (!alphaState) GL11.glDisable(3008);
        if (!blendState) GL11.glDisable(3042);
        GL11.glPopMatrix();
        return x;
    }

    public int getStringHeight() {
        if (this.fontHeight == -1) {
            return this.fontHeight = getStringHeight("ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789!@#$%^&*()");
        }
        return this.fontHeight;
    }

    public int getStringHeight(String... text) {
        int height = 0;

        for (String s : text) {
            height += getStringHeight(s);
        }

        return height;
    }

    public int getStringHeight(String text) {
        if (text == null)
            return 0;
        int height = 0;
        FontData currentFont = this.fontData;
        boolean bold = false, italic = false;
        int size = text.length();

        for (int i = 0; i < size; i++) {
            char character = text.charAt(i);
            if (character == '§') {
                int colorIndex = "0123456789abcdefklmnor".indexOf(character);
                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                } else if (colorIndex == 17) {
                    bold = true;
                    if (italic) {
                        currentFont = this.boldItalicFont;
                    } else {
                        currentFont = this.boldFont;
                    }
                } else if (colorIndex == 20) {
                    italic = true;
                    if (bold) {
                        currentFont = this.boldItalicFont;
                    } else {
                        currentFont = this.italicFont;
                    }
                } else if (colorIndex == 21) {
                    bold = false;
                    italic = false;
                    currentFont = this.fontData;
                }
                i++;
            } else if (currentFont.hasBounds(character) &&
                    (currentFont.getCharacterBounds(character)).height > height) {
                height = (currentFont.getCharacterBounds(character)).height;
            }
        }

        return height / 2;
    }

    public int getStringWidth(String text) {
        if (text == null)
            return 0;
        int width = 0;
        FontData currentFont = this.fontData;
        boolean bold = false, italic = false;
        int size = text.length();

        for (int i = 0; i < size; i++) {
            char character = text.charAt(i);
            if (character == '§') {
                int colorIndex = "0123456789abcdefklmnor".indexOf(character);
                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                } else if (colorIndex == 17) {
                    bold = true;
                    if (italic) {
                        currentFont = this.boldItalicFont;
                    } else {
                        currentFont = this.boldFont;
                    }
                } else if (colorIndex == 20) {
                    italic = true;
                    if (bold) {
                        currentFont = this.boldItalicFont;
                    } else {
                        currentFont = this.italicFont;
                    }
                } else if (colorIndex == 21) {
                    bold = false;
                    italic = false;
                    currentFont = this.fontData;
                }
                i++;
            } else if (currentFont.hasBounds(character)) {
                width += (currentFont.getCharacterBounds(character)).width + this.kerning;
            }
        }

        return width / 2;
    }

    public int getMinWidth(String... strings) {
        int width = 10000;
        for (String str : strings) {
            if (str.isEmpty()) str = " ";
            width = Math.min(width, getStringWidth(str));
        }
        return (width == 10000) ? 0 : width;
    }

    public int getMaxWidth(String... strings) {
        int width = -10000;
        for (String str : strings) {
            if (str.isEmpty()) str = " ";
            width = Math.max(width, getStringWidth(str));
        }
        return (width == -10000) ? 0 : width;
    }

    public int getMinHeight(String... strings) {
        int height = 10000;
        for (String str : strings) {
            if (str.isEmpty()) str = " ";
            height = Math.min(height, getStringHeight(str));
        }
        return (height == 10000) ? 0 : height;
    }

    public int getMaxHeight(String... strings) {
        int height = -10000;
        for (String str : strings) {
            if (str.isEmpty()) str = " ";
            height = Math.max(height, getStringHeight(str));
        }
        return (height == -10000) ? 0 : height;
    }

    public String maxWidth(String... strings) {
        String current = null;
        int currentWidth = -10000;
        for (String str : strings) {
            if (str.isEmpty()) str = " ";
            int width = getStringWidth(str);
            if (current == null || width > currentWidth) {
                current = str;
                currentWidth = width;
            }
        }
        return current;
    }

    public String minWidth(String... strings) {
        String current = null;
        int currentWidth = 10000;
        for (String str : strings) {
            if (str.isEmpty()) str = " ";
            int width = getStringWidth(str);
            if (current == null || width < currentWidth) {
                current = str;
                currentWidth = width;
            }
        }
        return current;
    }

    public String maxHeight(String... strings) {
        String current = null;
        int currentHeight = -10000;
        for (String str : strings) {
            if (str.isEmpty()) str = " ";
            int height = getStringHeight(str);
            if (current == null || height > currentHeight) {
                current = str;
                currentHeight = height;
            }
        }
        return current;
    }

    public String minHeight(String... strings) {
        String current = null;
        int currentHeight = 10000;
        for (String str : strings) {
            if (str.isEmpty()) str = " ";
            int height = getStringHeight(str);
            if (current == null || height < currentHeight) {
                current = str;
                currentHeight = height;
            }
        }
        return current;
    }

    public void setFont(Font font, boolean antiAlias) {
        this.fontData.setFont(font, antiAlias);
        this.boldFont.setFont(font.deriveFont(1), antiAlias);
        this.italicFont.setFont(font.deriveFont(2), antiAlias);
        this.boldItalicFont.setFont(font.deriveFont(3), antiAlias);
    }

    public void setBoldFont(Font font, boolean antiAlias) {
        this.boldFont.setFont(font.deriveFont(1), antiAlias);
    }

    public void setItalicFont(Font font, boolean antiAlias) {
        this.italicFont.setFont(font.deriveFont(1), antiAlias);
    }

    public void setBoldItalicFont(Font font, boolean antiAlias) {
        this.boldItalicFont.setFont(font.deriveFont(3), antiAlias);
    }

    public static void drawLine(float size, float x, float y, float x1, float y1) {
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GL11.glEnable(2848);
        GL11.glLineWidth(size);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        worldRenderer.begin(1, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(x, y, 0.0D).endVertex();
        worldRenderer.pos(x1, y1, 0.0D).endVertex();
        tessellator.draw();
        GL11.glDisable(2848);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawTextureRect(float x, float y, float width, float height, float u, float v, float t, float s) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(4, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos((x + width), y, 0.0D).tex(t, v).endVertex();
        worldRenderer.pos(x, y, 0.0D).tex(u, v).endVertex();
        worldRenderer.pos(x, (y + height), 0.0D).tex(u, s).endVertex();
        worldRenderer.pos(x, (y + height), 0.0D).tex(u, s).endVertex();
        worldRenderer.pos((x + width), (y + height), 0.0D).tex(t, s).endVertex();
        worldRenderer.pos((x + width), y, 0.0D).tex(t, v).endVertex();
        tessellator.draw();
    }

    public static void glColor(int hex) {
        float alpha = (hex >> 24 & 0xFF) / 255.0F;
        float red = (hex >> 16 & 0xFF) / 255.0F;
        float green = (hex >> 8 & 0xFF) / 255.0F;
        float blue = (hex & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }
}
