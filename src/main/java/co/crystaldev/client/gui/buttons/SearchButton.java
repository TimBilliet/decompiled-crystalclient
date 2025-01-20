package co.crystaldev.client.gui.buttons;

import co.crystaldev.client.Resources;
import co.crystaldev.client.gui.Screen;
import co.crystaldev.client.util.ClientTextureManager;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class SearchButton extends TextInputField {
    private final FadingColor fadingColor;

    private final FadingColor textColor;

    private final int expandedWidth;

    private boolean selected = false;

    private boolean updated = false;

    private int currentWidth = 0;

    private int targetWidth = 0;

    private long lastStateChange = System.currentTimeMillis() - 2000L;

    public SearchButton(int x, int y, int width, int expandedWidth, int height) {
        super(-1, x, y, width, height, "");
        this.expandedWidth = expandedWidth;
        setMaxLength(10);
        setTyping(true);
        this.canMakeSelections = false;
        this.fadingColor = new FadingColor(this.opts.neutralButtonBackground, this.opts.hoveredButtonBackground);
        this.textColor = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
        ClientTextureManager.getInstance().loadTextureMipMap(Resources.SEARCH);
        Keyboard.enableRepeatEvents(true);
    }

    public void onClose() {
        Keyboard.enableRepeatEvents(false);
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
        this.fadingColor.fade(hovered);
        this.textColor.fade((hovered || this.selected));
        animate();
        RenderUtils.drawRoundedRect((this.x - this.currentWidth), this.y, (this.x + this.width), (this.y + this.height), 6.0D, this.fadingColor
                .getCurrentColor().getRGB());
        RenderUtils.glColor(this.textColor.getCurrentColor().getRGB());
        RenderUtils.drawCustomSizedResource(Resources.SEARCH, this.x + 2, this.y + 2, this.width - 4, this.height - 4);
        if (this.currentWidth > 0) {
            String display = this.text + ((System.currentTimeMillis() / 500L % 2L == 0L && this.selected) ? "|" : "");
            this.fontRenderer.drawString(display, this.x - this.currentWidth + 2, this.y + this.height / 2 - this.fontRenderer.getStringHeight() / 2, this.textColor
                    .getCurrentColor().getRGB());
        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public boolean onKeyTyped(char key, int code) {
        if (!this.selected &&
                !this.text.isEmpty()) {
            this.text = "";
            this.updated = true;
        }
        if (code == 14) {
            if (this.text.length() > 0) {
                this.text = this.text.substring(0, this.text.length() - 1);
                this.updated = true;
            }
        } else {
            if (code == 28) {
                this.selected = false;
                update();
                return true;
            }
            if (ChatAllowedCharacters.isAllowedCharacter(key) && (
                    this.maxLength == -1 || this.text.length() + 1 <= this.maxLength)) {
                this.text += Character.toString(key);
                this.updated = true;
            }
        }
        if (this.text.length() > 0) {
            this.selected = true;
        } else if (this.selected) {
            this.selected = false;
        }
        update();
        return true;
    }

    public void mouseDown(Screen screen, int mouseX, int mouseY, int mouseButton) {
        if (!isHovered(mouseX, mouseY))
            this.selected = false;
        this.selected = (isHovered(mouseX, mouseY) && !this.selected);
        update();
    }

    public boolean isHovered(int mouseX, int mouseY) {
        if (this.scissorPane != null && (
                mouseX < this.scissorPane.x / this.scale || mouseX > (this.scissorPane.x + this.scissorPane.width) / this.scale || mouseY < this.scissorPane.y / this.scale || mouseY > (this.scissorPane.y + this.scissorPane.height) / this.scale))
            return false;
        return (mouseX >= this.x - this.currentWidth && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height);
    }

    private void update() {
        int targetWidth = this.targetWidth;
        this.targetWidth = (this.selected || this.text.length() > 0) ? (this.expandedWidth - this.width) : 0;
        if (targetWidth != this.targetWidth)
            this.lastStateChange = System.currentTimeMillis();
    }

    private void animate() {
        this

                .currentWidth = (this.targetWidth < this.currentWidth) ? (int) Math.max(this.currentWidth - (System.currentTimeMillis() - this.lastStateChange) / 20L, this.targetWidth) : (int) Math.min(this.currentWidth + (System.currentTimeMillis() - this.lastStateChange) / 20L, this.targetWidth);
    }

    public boolean wasUpdated() {
        if (this.updated) {
            this.updated = false;
            return true;
        }
        return false;
    }

    public boolean matchesQuery(String in) {
        in = in.replaceAll("[ .]+", "").toLowerCase();
        String query = this.text.replaceAll("[ .]+", "").toLowerCase();
        return (this.text.isEmpty() || in.contains(query));
    }
}
