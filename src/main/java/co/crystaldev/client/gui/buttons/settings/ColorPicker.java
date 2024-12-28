package co.crystaldev.client.gui.buttons.settings;

import co.crystaldev.client.Reference;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Pane;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.gui.ScrollPane;
import co.crystaldev.client.gui.buttons.Label;
import co.crystaldev.client.gui.buttons.MenuButton;
import co.crystaldev.client.gui.buttons.TextInputField;
import co.crystaldev.client.shader.ShaderManager;
import co.crystaldev.client.shader.chroma.ChromaScreenShader;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.ColorUtils;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.lang.reflect.Field;

public class ColorPicker extends SettingButton<ColorObject> {
    private static final Tessellator tessellator = Tessellator.getInstance();

    private static final WorldRenderer renderer = tessellator.getWorldRenderer();

    private final long nanoTime = System.nanoTime();

    private final int originalHeight;

    private final int expandedAddedHeight;

    private boolean expanded = false;

    private boolean updated = false;

    private boolean shouldCollapseButtonsBelow = true;

    public void setShouldCollapseButtonsBelow(boolean shouldCollapseButtonsBelow) {
        this.shouldCollapseButtonsBelow = shouldCollapseButtonsBelow;
    }

    private boolean canBeExpanded = true;

    private final Label hexInputLabel;

    private final TextInputField hexInput;

    private final HueSubButton hue;

    private final AlphaSubButton alpha;

    private final SaturationSubButton saturation;

    private final ToggleButton chroma;

    private final MenuButton bold;

    private final MenuButton underline;

    private final MenuButton italic;

    protected final FadingColor backgroundColor;

    protected final FadingColor textColor;

    public void setCanBeExpanded(boolean canBeExpanded) {
        this.canBeExpanded = canBeExpanded;
    }

    public ColorPicker(int id, int x, int y, int width, int height, String displayText, final ColorObject currentValue, boolean textOptions) {
        super(id, x, y, width, height, displayText, currentValue);
        this.originalHeight = this.height;
        this.expandedAddedHeight = this.height * 4;
        int sbx = this.x + 5;
        int sby = this.y + this.originalHeight + 5;
        int sbw = 15;
        int sbh = this.expandedAddedHeight - 10;
        this.hue = new HueSubButton(sbx, sby, sbw, sbh);
        sbx += sbw + 5;
        this.alpha = new AlphaSubButton(sbx, sby, sbw, sbh);
        sbx += sbw + 5;
        this.saturation = new SaturationSubButton(sbx, sby, sbh * 2, sbh);
        sbx += sbh * 2 + 5;
//        int hexWidth = this.width - sbx - this.x - 5;
        int hexWidth = this.width - sbx + this.x - 5;
        this.hexInputLabel = new Label(sbx + 5, sby + 9 - Fonts.NUNITO_REGULAR_20.getStringHeight() / 2, "Hex", 16777215, Fonts.NUNITO_REGULAR_20);
        this.hexInputLabel.setCentered(false);

        this.hexInput = new TextInputField(-1, sbx + hexWidth / 2, sby, hexWidth / 2, 18, "#FFFFFF");
        this.hexInput.setValidInputPattern("^#?[a-fA-F0-9]{1,6}$");
        this.hexInput.setText(String.format("#%02x%02x%02x", this.currentValue.getRed(), this.currentValue.getGreen(), this.currentValue.getBlue()).toUpperCase());
        this.hexInput.setOnTextInput(str -> {
            if (!this.hexInput.getText().isEmpty() && !this.hexInput.getText().startsWith("#")) {
                this.hexInput.setText("#" + this.hexInput.getText());
                this.hexInput.getCaret().setLocation(this.hexInput.getCaret().getIndex() + 1);
                return;
            }
            if (this.updated) {
                this.updated = false;
                return;
            }
            try {
                Color color = Color.decode(this.hexInput.getText());
                int[] values = ColorUtils.rgbToHsb(color.getRGB());
                int hue = values[0];
                int saturation = values[1];
                int brightness = values[2];
                this.hue.hue = hue;
                this.saturation.saturation = saturation;
                this.saturation.brightness = brightness;
            } catch (NumberFormatException numberFormatException) {

            }
        });
        sby += 23;
        this.chroma = new ToggleButton(-1, sbx, sby, this.width - sbx + this.x - 5, 18, "Chroma", currentValue.isChroma());
        sby += 23;
        if (textOptions) {
            sbx += 3;
            sbw = this.chroma.width / 3 - 6;
            this.bold = new MenuButton(-1, sbx, sby, sbw, 18, "&lB", currentValue.isBold() ? this.opts.mainColor.getRGB() : -1) {

            };
            sbx += sbw + 6;
            this.italic = new MenuButton(-1, sbx, sby, sbw, 18, "&oI", currentValue.isItalic() ? this.opts.mainColor.getRGB() : -1) {

            };
            sbx += sbw + 6;
            this.underline = new MenuButton(-1, sbx, sby, sbw, 18, "&nU", currentValue.isUnderline() ? this.opts.mainColor.getRGB() : -1) {

            };
        } else {
            this.bold = this.underline = this.italic = null;
        }
        this.backgroundColor = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
        this.textColor = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
    }

    private void updateScissorPanes() {
        this.hexInputLabel.setScissorPane(this.scissorPane);
        this.hexInput.setScissorPane(this.scissorPane);
        this.hue.setScissorPane(this.scissorPane);
        this.alpha.setScissorPane(this.scissorPane);
        this.saturation.setScissorPane(this.scissorPane);
        this.chroma.setScissorPane(this.scissorPane);
        if (this.bold != null) {
            this.bold.setScissorPane(this.scissorPane);
            this.underline.setScissorPane(this.scissorPane);
            this.italic.setScissorPane(this.scissorPane);
        }
    }

    public void onUpdate() {
        this.hue.y = this.alpha.y = this.saturation.y = this.hexInput.y = this.hexInputLabel.y = this.chroma.y = this.y + this.originalHeight + 5;
        this.hexInputLabel.y = this.hexInputLabel.y + 9 - Fonts.NUNITO_REGULAR_20.getStringHeight() / 2;
        this.chroma.y += 23;
        if (this.bold != null)
            this.underline.y = this.y + this.originalHeight + 5 + 46;
        updateScissorPanes();
    }

    public void setScissorPane(Pane pane) {
        super.setScissorPane(pane);
        updateScissorPanes();
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        Screen.scissorStart(this.scissorPane);
        this.backgroundColor.fade((hovered || this.expanded));
        this.textColor.fade((hovered || this.expanded));
        RenderUtils.drawRoundedRect(this.x, this.y, (this.x + this.width), (this.y + this.height), 9.0D, this.backgroundColor.getCurrentColor().getRGB());
        this.fontRenderer.drawString(this.displayText, this.x + 4, this.y + this.originalHeight / 2 - this.fontRenderer.getStringHeight() / 2, this.textColor.getCurrentColor().getRGB());
        int boxSize = this.originalHeight - 6;
        if (this.currentValue.isChroma())
            ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
        RenderUtils.drawRoundedRect((this.x + this.width - 3 - boxSize), (this.y + 3), (this.x + this.width - 3), (this.y + this.originalHeight - 3), 6.0D, this.currentValue
                .getRGB());
        ShaderManager.getInstance().disableShader();
        String hex = String.format("#%02x%02x%02x", this.currentValue.getRed(), this.currentValue.getGreen(), this.currentValue.getBlue()).toUpperCase();
        this.fontRenderer.drawString(hex, this.x + this.width - 3 - boxSize - 5 - this.fontRenderer.getStringWidth(hex), this.y + this.originalHeight / 2 - this.fontRenderer
                .getStringHeight() / 2, this.textColor.getCurrentColor().getRGB());
        if (this.expanded) {
            this.hue.drawButton(mouseX, mouseY, (this.hue.isHovered(mouseX, mouseY) && hovered));
            this.alpha.drawButton(mouseX, mouseY, (this.alpha.isHovered(mouseX, mouseY) && hovered));
            this.saturation.drawButton(mouseX, mouseY, (this.saturation.isHovered(mouseX, mouseY) && hovered));
            this.chroma.drawButton(mouseX, mouseY, (this.chroma.isHovered(mouseX, mouseY) && hovered));
            this.hexInputLabel.drawButton(mouseX, mouseY, (this.hexInputLabel.isHovered(mouseX, mouseY) && hovered));
            this.hexInput.drawButton(mouseX, mouseY, (this.hexInput.isHovered(mouseX, mouseY) && hovered));
            if (this.bold != null && this.italic != null && this.underline != null) {
                this.bold.drawButton(mouseX, mouseY, (this.bold.isHovered(mouseX, mouseY) && hovered));
                this.italic.drawButton(mouseX, mouseY, (this.italic.isHovered(mouseX, mouseY) && hovered));
                this.underline.drawButton(mouseX, mouseY, (this.underline.isHovered(mouseX, mouseY) && hovered));
            }
            save();
        }
        Screen.scissorEnd(this.scissorPane);
    }

    public void onInteract(int mouseX, int mouseY, int mouseButton) {
        super.onInteract(mouseX, mouseY, mouseButton);
        if (this.expanded) {
            if (this.hue.isHovered(mouseX, mouseY)) {
                this.hue.onInteract(mouseX, mouseY, mouseButton);
                return;
            }
            if (this.alpha.isHovered(mouseX, mouseY)) {
                this.alpha.onInteract(mouseX, mouseY, mouseButton);
                return;
            }
            if (this.saturation.isHovered(mouseX, mouseY)) {
                this.saturation.onInteract(mouseX, mouseY, mouseButton);
                return;
            }
            if (this.chroma.isHovered(mouseX, mouseY)) {
                this.chroma.onInteract(mouseX, mouseY, mouseButton);
                return;
            }
            if (this.hexInput.isHovered(mouseX, mouseY)) {
                this.hexInput.onInteract(mouseX, mouseY, mouseButton);
                return;
            }
            if (this.bold != null && this.bold.isHovered(mouseX, mouseY)) {
                this.bold.onInteract(mouseX, mouseY, mouseButton);
                return;
            }
            if (this.underline != null && this.underline.isHovered(mouseX, mouseY)) {
                this.underline.onInteract(mouseX, mouseY, mouseButton);
                return;
            }
            if (this.italic != null && this.italic.isHovered(mouseX, mouseY)) {
                this.italic.onInteract(mouseX, mouseY, mouseButton);
                return;
            }
        }
        if (mouseY <= this.y + this.originalHeight &&
                this.canBeExpanded)
            invertExpandedState();
    }

    public void mouseDown(Screen screen, int mouseX, int mouseY, int mouseButton) {
        super.mouseDown(screen, mouseX, mouseY, mouseButton);
        this.hexInput.mouseDown(screen, mouseX, mouseY, mouseButton);
    }

    public boolean onKeyTyped(char key, int code) {
        boolean res = super.onKeyTyped(key, code);
        return (res && this.hexInput.onKeyTyped(key, code));
    }

    protected void save() {
        int color = ColorUtils.hsbToRgb(this.hue.hue, this.saturation.saturation, this.saturation.brightness);
        this.currentValue = new ColorObject(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, this.alpha.alpha, this.chroma.currentValue, (this.bold != null && this.bold.isSelected()), (this.underline != null && this.underline.isSelected()), (this.italic != null && this.italic.isSelected()));
        if (this.settingObject != null && this.settingField != null)
            try {
                this.settingField.set(this.settingObject, this.currentValue);
            } catch (IllegalAccessException ex) {
                Reference.LOGGER.error("Unable to assign field to value", ex);
            }
        updateHexInput();
    }

    public void invertExpandedState() {
        this.expanded = !this.expanded;
        this.height = this.expanded ? (this.originalHeight + this.expandedAddedHeight) : this.originalHeight;
        if (this.shouldCollapseButtonsBelow)
            updateButtonsBelowColorPicker();
    }

    private void updateButtonsBelowColorPicker() {
        if (this.mc.currentScreen instanceof Screen) {
            String attribute = this.attributes.isEmpty() ? null : this.attributes.stream().findFirst().get();
            String id = "color_picker_" + this.nanoTime;
            for (Button button : ((Screen) this.mc.currentScreen).buttons) {
                if (button.y <= this.y)
                    continue;
                if (attribute != null && !button.hasAttribute(attribute))
                    continue;
                if (button.hasAttribute(id)) {
                    button.removeAttribute(id);
                    if (!this.expanded) {
                        button.y -= this.expandedAddedHeight;
                        button.initialY -= this.expandedAddedHeight;
                    }
                    continue;
                }
                button.addAttribute(id);
                if (this.expanded) {
                    button.y += this.expandedAddedHeight;
                    button.initialY += this.expandedAddedHeight;
                }
            }
            for (Field field : this.mc.currentScreen.getClass().getFields()) {
                try {
                    if (field.getType() == ScrollPane.class) {
                        if (!field.isAccessible())
                            field.setAccessible(true);
                        ScrollPane sp = (ScrollPane) field.get(this.mc.currentScreen);
                        sp.updateMaxScroll((Screen) this.mc.currentScreen, sp.getLastMarginInc());
                    }
                } catch (IllegalAccessException | SecurityException illegalAccessException) {
                }
            }
        }
    }

    private void updateHexInput() {
        if (this.hexInput.isTyping())
            return;
        this.updated = true;
        this.hexInput.setText(String.format("#%02x%02x%02x", this.currentValue.getRed(), this.currentValue.getGreen(), this.currentValue.getBlue()).toUpperCase());
    }

    public boolean shouldOverlayBeRendered(int mouseX, int mouseY) {
        return isHovered(mouseX, mouseY);
    }

    private class SaturationSubButton extends Button {
        private int saturation;

        private int brightness;

        private boolean selecting = false;

        public SaturationSubButton(int x, int y, int width, int height) {
            super(-1, x, y, width, height);
            int[] hsb = ColorUtils.rgbToHsb(ColorPicker.this.currentValue.getRGB());
            this.saturation = hsb[1];
            this.brightness = hsb[2];
        }

        public void drawButton(int mouseX, int mouseY, boolean hovered) {
            Screen.scissorStart(this.scissorPane);
            int color = ColorUtils.hsbToRgb(ColorPicker.this.hue.hue, 100, 100);
            float r = (color >> 16 & 0xFF) / 255.0F;
            float g = (color >> 8 & 0xFF) / 255.0F;
            float b = (color & 0xFF) / 255.0F;
            boolean wasBlend = GL11.glGetBoolean(3042);
            boolean wasTexture2d = GL11.glGetBoolean(3553);
            GL11.glEnable(3042);
            GL11.glDisable(3553);
            GL11.glBlendFunc(770, 771);
            GL11.glShadeModel(7425);
            ColorPicker.renderer.begin(9, DefaultVertexFormats.POSITION_COLOR);
            ColorPicker.renderer.pos(this.x, (this.y + this.height), 0.0D)
                    .color(0.0F, 0.0F, 0.0F, 1.0F)
                    .endVertex();
            ColorPicker.renderer.pos((this.x + this.width), (this.y + this.height), 0.0D)
                    .color(0.0F, 0.0F, 0.0F, 1.0F)
                    .endVertex();
            ColorPicker.renderer.pos((this.x + this.width), this.y, 0.0D)
                    .color(r, g, b, 1.0F)
                    .endVertex();
            ColorPicker.renderer.pos(this.x, this.y, 0.0D)
                    .color(1.0F, 1.0F, 1.0F, 1.0F)
                    .endVertex();
            ColorPicker.tessellator.draw();
            GL11.glShadeModel(7424);
            if (!wasBlend)
                GL11.glDisable(3042);
            if (wasTexture2d)
                GL11.glEnable(3553);
            RenderUtils.drawCircle((this.x + (int) (this.saturation / 100.0D * this.width)), (this.y + this.height) - (this.brightness * this.height) / 100.0F, 5.0F, -1);
            RenderUtils.drawRoundedBorder(this.x - 0.5D, this.y - 0.5D, (this.x + this.width) + 0.5D, (this.y + this.height) + 0.5D, 4.0D, 1.5F, Color.WHITE
                    .getRGB());
            if (hovered && this.selecting) {
                this.saturation = MathHelper.clamp_int((int) ((float)(mouseX - this.x) / this.width * 100), 0, 100);
//                this.saturation = MathHelper.clamp_int(100 - (mouseX - this.x) * 100 / this.width, 0, 100);
//                this.brightness = MathHelper.clamp_int((this.height - mouseY - this.y) * 100 / this.height, 0, 100);
                this.brightness = MathHelper.clamp_int(100 - (mouseY - this.y) * 100 / this.height, 0, 100);
                System.out.println("heigth " + this.height + " mousey " + mouseY + " this.y " + this.y);
                System.out.println("brightness: " + this.brightness);
            }
            if (!Mouse.isButtonDown(0) && this.selecting)
                this.selecting = false;
            Screen.scissorEnd(this.scissorPane);
        }

        public void onInteract(int mouseX, int mouseY, int mouseButton) {
            super.onInteract(mouseX, mouseY, mouseButton);
            System.out.println("oninteract saturation" + mouseButton);
            if (mouseButton == 0)
                this.selecting = true;
        }
    }

    private class AlphaSubButton extends Button {
        private int alpha;

        private boolean selecting = false;

        public AlphaSubButton(int x, int y, int width, int height) {
            super(-1, x, y, width, height);
            this.alpha = ColorPicker.this.currentValue.getAlpha();
        }

        public void drawButton(int mouseX, int mouseY, boolean hovered) {
            Screen.scissorStart(this.scissorPane);
            RenderUtils.resetColor();
            RenderUtils.drawGradientRect(this.x, this.y, this.x + this.width, this.y + this.height, (ColorPicker.this.currentValue.getRGB() | 0xFF000000), 0L);
            RenderUtils.resetColor();
            RenderUtils.drawLine(1.5F, this.x, (this.y + this.height) - (this.alpha * this.height) / 255.0F, (this.x + this.width), (this.y + this.height) - (this.alpha * this.height) / 255.0F);
            RenderUtils.drawRoundedBorder(this.x - 0.5D, this.y - 0.5D, (this.x + this.width) + 0.5D, (this.y + this.height) + 0.5D, 4.0D, 1.5F, Color.WHITE.getRGB());
            if (hovered && this.selecting) {
//                this.alpha = MathHelper.clamp_int((this.height - mouseY - this.y) * 255 / this.height, 0, 255);
                this.alpha = MathHelper.clamp_int(255 - (mouseY - this.y) * 255 / this.height, 0, 255);
            }
            if (!Mouse.isButtonDown(0) && this.selecting)
                this.selecting = false;
            Screen.scissorEnd(this.scissorPane);
        }

        public void onInteract(int mouseX, int mouseY, int mouseButton) {
            super.onInteract(mouseX, mouseY, mouseButton);
            if (mouseButton == 0)
                this.selecting = true;
        }
    }

    private class HueSubButton extends Button {
        private int hue;

        private boolean selecting = false;

        public HueSubButton(int x, int y, int width, int height) {

            super(-1, x, y, width, height);
            int[] hsb = ColorUtils.rgbToHsb(ColorPicker.this.currentValue.getRGB());
            this.hue = hsb[0];
        }

        public void drawButton(int mouseX, int mouseY, boolean hovered) {
            Screen.scissorStart(this.scissorPane);
            RenderUtils.resetColor();
            int s = this.height / 7;
            for (int i = 0; i < 64; i += 8) {
                int color1 = ColorUtils.hsbToRgb(i * 360 / 64, 100, 100) | 0xFF000000;
                int color2 = ColorUtils.hsbToRgb((i + 8) * 360 / 64, 100, 100) | 0xFF000000;
                RenderUtils.drawGradientRect(this.x, this.y + i / 8 * s, this.x + this.width, Math.min(this.y + this.height, this.y + i / 8 * s + s), color1, color2);
            }
            RenderUtils.resetColor();
            RenderUtils.drawLine(1.5F, this.x, this.y + this.height * this.hue / 360.0F, (this.x + this.width), this.y + this.height * this.hue / 360.0F);
            RenderUtils.drawRoundedBorder(this.x - 0.5D, this.y - 0.5D, (this.x + this.width) + 0.5D, (this.y + this.height) + 0.5D, 4.0D, 1.5F, Color.WHITE
                    .getRGB());
            if (hovered && this.selecting)
                this.hue = MathHelper.clamp_int((mouseY - this.y) * 360 / this.height, 0, 360);
            if (!Mouse.isButtonDown(0) && this.selecting)
                this.selecting = false;
            Screen.scissorEnd(this.scissorPane);
        }

        public void onInteract(int mouseX, int mouseY, int mouseButton) {
            super.onInteract(mouseX, mouseY, mouseButton);
            if (mouseButton == 0)
                this.selecting = true;
        }
    }
}

