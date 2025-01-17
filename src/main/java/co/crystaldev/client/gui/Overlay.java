package co.crystaldev.client.gui;

import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.font.FontRenderer;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class Overlay {
    protected final Minecraft mc;

    protected final net.minecraft.client.gui.FontRenderer mcFontRenderer;

    protected final GuiOptions opts = GuiOptions.getInstance();

    protected FontRenderer fontRenderer = Fonts.NUNITO_SEMI_BOLD_20;

    protected ScaledResolution sr;

    public int width;

    public int height;

    public int scaledWidth;

    public int scaledHeight;

    protected boolean overrideKeyboard = false;

    public boolean isOverrideKeyboard() {
        return this.overrideKeyboard;
    }

    public void setOverrideKeyboard(boolean overrideKeyboard) {
        this.overrideKeyboard = overrideKeyboard;
    }

    protected final List<Button> buttons = new ArrayList<>();

    public List<Button> getButtons() {
        return this.buttons;
    }

    private final List<Button> buttonsToRemove = new ArrayList<>();

    public List<Button> getButtonsToRemove() {
        return this.buttonsToRemove;
    }

    private boolean closed = false;

    public boolean isClosed() {
        return this.closed;
    }

    private KeyBinding holdKey = null;

    public KeyBinding getHoldKey() {
        return this.holdKey;
    }

    public void setHoldKey(KeyBinding holdKey) {
        this.holdKey = holdKey;
    }

    public Overlay() {
        this.mc = Minecraft.getMinecraft();
        this.mcFontRenderer = this.mc.fontRendererObj;
        this.sr = new ScaledResolution(this.mc);
        this.scaledWidth = this.sr.getScaledWidth();
        this.scaledHeight = this.sr.getScaledHeight();
        this.width = this.mc.displayWidth / 2;
        this.height = this.mc.displayHeight / 2;
    }

    public Overlay(KeyBinding bind) {
        this();
        this.holdKey = bind;
    }

    public void init() {
        this.buttonsToRemove.addAll(this.buttons);
    }

    public void drawDefaultBackground() {
        if (ClientOptions.getInstance().canBlur())
            ClientOptions.getInstance().blurScreen();
        int color = ClientOptions.getInstance().canBlur() ? ClientOptions.getInstance().getBackgroundColor() : 1712328720;
        int color1 = GuiOptions.getInstance().getColor((GuiOptions.getInstance()).mainColor, 2).getRGB();
        if (this.mc.theWorld != null) {
            RenderUtils.drawGradientRect(0, 0, this.width, this.height, color, color);
            RenderUtils.drawGradientRect(0, 0, this.width, this.height, color1, color1);
        }
    }

    public float getScaledScreen() {
        int s = ((this.sr == null) ? (this.sr = new ScaledResolution(this.mc)) : this.sr).getScaleFactor();
        return 1.0F / (0.5F * s);
    }

    public void onResize(ScaledResolution sr) {
        this.scaledWidth = sr.getScaledWidth();
        this.scaledHeight = sr.getScaledHeight();
        this.width = this.mc.displayWidth / 2;
        this.height = this.mc.displayHeight / 2;
        this.sr = sr;
        init();
    }

    public void keyPressed(int keyCode, char keyTyped) {
        for (Button button : this.buttons)
            button.onKeyTyped(keyTyped, keyCode);
    }

    public void keyReleased(int keyCode, char keyTyped) {
        if (this.holdKey != null && keyCode == this.holdKey.getKeyCode())
            close();
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (Button button : this.buttons) {
            if (button.isHovered(mouseX, mouseY)) {
                button.onInteract(mouseX, mouseY, mouseButton);
                onButtonInteract(button, mouseX, mouseY, mouseButton);
                break;
            }
            button.mouseDown(null, mouseX, mouseY, mouseButton);
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (this.holdKey != null && this.holdKey.getKeyCode() == -100 + mouseButton)
            close();
    }

    public void onButtonInteract(Button button, int mouseX, int mouseY, int mouseButton) {
    }

    public void addButton(Button button) {
        this.buttons.add(button);
    }

    public void removeButton(Button button) {
        this.buttonsToRemove.add(button);
        button.removed = true;
    }

    public void removeButton(Predicate<Button> predicate) {
        synchronized (this.buttons) {
            for (Button button : this.buttons) {
                if (predicate.test(button) && !button.removed)
                    removeButton(button);
            }
        }
    }

    public void handleKeyboardInput() {
        if (Keyboard.getEventKeyState()) {
            keyPressed(Keyboard.getEventKey(), Keyboard.getEventCharacter());
        } else {
            keyReleased(Keyboard.getEventKey(), Keyboard.getEventCharacter());
        }
        this.mc.dispatchKeypresses();
    }

    public void handleMouseInput() {
        int mx = Mouse.getEventX() / 2;
        int my = this.height - Mouse.getEventY() / 2 - 1;
        int mb = Mouse.getEventButton();
        if (Mouse.getEventButtonState()) {
            mouseClicked(mx, my, mb);
        } else if (mb != -1) {
            mouseReleased(mx, my, mb);
        }
    }

    public void handleInput() {
        if (Mouse.isCreated())
            while (Mouse.next())
                handleMouseInput();
        if (this.overrideKeyboard && Keyboard.isCreated())
            while (Keyboard.next())
                handleKeyboardInput();
    }

    public void clearRemovedButtons() {
        this.buttons.removeAll(this.buttonsToRemove);
        this.buttonsToRemove.clear();
    }

    public void close() {
        this.closed = true;
        for (Button button : this.buttons)
            button.onClose();
        KeyBinding.unPressAllKeys();
    }

    public abstract void draw(int paramInt1, int paramInt2, float paramFloat);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\Overlay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */