package co.crystaldev.client.handler;

import co.crystaldev.client.Client;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.network.ServerDisconnectEvent;
import co.crystaldev.client.event.impl.player.InputEvent;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Overlay;
import co.crystaldev.client.gui.overlay.OverlayEmoticonWheel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class OverlayHandler implements IRegistrable {
    private static OverlayHandler INSTANCE;

    private final Minecraft mc = Minecraft.getMinecraft();

    private Overlay currentOverlay = null;

    public Overlay getCurrentOverlay() {
        return this.currentOverlay;
    }

    private final KeyBinding emoteWheel = new KeyBinding("crystalclient.key.emote_wheel", 34, "Crystal Client");

    public OverlayHandler() {
        INSTANCE = this;
        Client.registerKeyBinding(this.emoteWheel);
    }

    public boolean hasOverlay() {
        return (this.currentOverlay != null && !this.currentOverlay.isClosed());
    }

    public void displayOverlay(Overlay overlay) {
        if (this.currentOverlay != null)
            this.currentOverlay.close();
        if (overlay != null)
            overlay.init();
        this.currentOverlay = overlay;
        this.mc.inGameHasFocus = false;
        this.mc.mouseHelper.ungrabMouseCursor();
    }

    public void drawScreen(float partialTicks) {
        if (this.currentOverlay == null)
            return;
        if (this.currentOverlay.isClosed()) {
            this.currentOverlay = null;
            if (this.mc.currentScreen == null)
                this.mc.setIngameFocus();
            return;
        }
        float scale = this.currentOverlay.getScaledScreen();
        int mouseX = Mouse.getX() / 2, mouseY = this.currentOverlay.height - Mouse.getY() / 2 - 1;
        if (this.mc.currentScreen != null) {
            this.currentOverlay.close();
            return;
        }
        GL11.glPushMatrix();
        GL11.glScalef(scale, scale, scale);
        GL11.glTranslatef(0.0F, 0.0F, 1.0F);
        this.currentOverlay.drawDefaultBackground();
        this.currentOverlay.draw(mouseX, mouseY, partialTicks);
        for (Button button : this.currentOverlay.getButtons()) {
            if (button.visible && !button.removed)
                button.drawButton(mouseX, mouseY, button.isHovered(mouseX, mouseY));
        }
        this.currentOverlay.clearRemovedButtons();
        GL11.glPopMatrix();
    }

    private void checkKeybinds(int code) {
        if (this.mc.currentScreen != null || getInstance().hasOverlay())
            return;
        if (this.emoteWheel.getKeyCode() == code)
            displayOverlay((Overlay) new OverlayEmoticonWheel(this.emoteWheel));
    }

    public static OverlayHandler getInstance() {
        return INSTANCE;
    }

    public void registerEvents() {
        EventBus.register(this, ServerDisconnectEvent.class, ev -> {
            if (this.currentOverlay != null)
                this.currentOverlay.close();
        });
        EventBus.register(this, InputEvent.Key.class, ev -> {
            if (ev.isKeyDown()) {
                checkKeybinds(ev.getKeyCode());
            } else if (hasOverlay()) {
                KeyBinding kb = this.currentOverlay.getHoldKey();
                if (kb != null && kb.getKeyCode() == ev.getKeyCode())
                    this.currentOverlay.close();
            }
        });
        EventBus.register(this, InputEvent.Mouse.class, ev -> {
            if (ev.buttonState) {
                checkKeybinds(-100 + ev.button);
            } else if (hasOverlay()) {
                KeyBinding kb = this.currentOverlay.getHoldKey();
                if (kb != null && kb.getKeyCode() == -100 + ev.button)
                    this.currentOverlay.close();
            }
        });
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\handler\OverlayHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */