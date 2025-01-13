package co.crystaldev.client.patcher.hook;

import co.crystaldev.client.Reference;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.SubscribeEvent;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.MixinMinecraft;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.settings.MixinKeyBinding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.Util;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import java.awt.*;

public class MinecraftHook {
    public static final MinecraftHook INSTANCE = new MinecraftHook();

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final MixinMinecraft mcAccesor = (MixinMinecraft) mc;

    private boolean lastFullscreen = false;

    public MinecraftHook() {
        EventBus.register(this);
    }

    public static void updateKeyBindState() {
        for (KeyBinding keybinding : MixinKeyBinding.getKeybindArray()) {
            try {
                int keyCode = keybinding.getKeyCode();
                KeyBinding.setKeyBindState(keyCode, (keyCode < 256 && Keyboard.isKeyDown(keyCode)));
            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            }
        }
    }

    public static boolean fullscreen() {
        if (!(ClientOptions.getInstance()).instantFullscreen || !(ClientOptions.getInstance()).borderlessFullscreen || Util.getOSType() != Util.EnumOS.WINDOWS)
            return false;
        mcAccesor.setFullScreen(!mc.isFullScreen());
        boolean grabbed = Mouse.isGrabbed();
        if (grabbed)
            Mouse.setGrabbed(false);
        try {
            DisplayMode displayMode = Display.getDesktopDisplayMode();
            if (mc.isFullScreen()) {
                System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
                Display.setDisplayMode(displayMode);
                Display.setLocation(0, 0);
            } else {
                System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
                displayMode = new DisplayMode(mcAccesor.getTempDisplayWidth(), mcAccesor.getTempDisplayHeight());
                Display.setDisplayMode(displayMode);
                displayCommon();
            }
            Display.setFullscreen(false);
            mc.displayWidth = displayMode.getWidth();
            mc.displayHeight = displayMode.getHeight();
            if (mc.currentScreen != null) {
                mcAccesor.callResize(mc.displayWidth, mc.displayHeight);
            } else {
                mcAccesor.callUpdateFramebufferSize();
            }
            INSTANCE.lastFullscreen = mc.isFullScreen();
            mc.updateDisplay();
            Mouse.setCursorPosition(Display.getX() + Display.getWidth() >> 1, Display.getY() + Display.getHeight() >> 1);
            if (grabbed)
                Mouse.setGrabbed(true);
            Display.setResizable(false);
            Display.setResizable(true);
            return true;
        } catch (LWJGLException ex) {
            Reference.LOGGER.error("Failed to toggle borderless fullscreen", (Throwable) ex);
            return false;
        }
    }

    private static void displayCommon() {
        Display.setResizable(false);
        Display.setResizable(true);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - Display.getWidth()) / 2.0D);
        int y = (int) ((dimension.getHeight() - Display.getHeight()) / 2.0D);
        Display.setLocation(x, y);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        if (!(ClientOptions.getInstance()).borderlessFullscreen)
            return;
        boolean fullScreenNow = Minecraft.getMinecraft().isFullScreen();
        if (this.lastFullscreen != fullScreenNow) {
            fix(fullScreenNow);
            this.lastFullscreen = fullScreenNow;
        }
    }

    public void fix(boolean fullscreen) {
        try {
            if (fullscreen) {
                System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
                Display.setDisplayMode(Display.getDesktopDisplayMode());
                Display.setLocation(0, 0);
                Display.setFullscreen(false);
            } else {
                System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
                Display.setDisplayMode(new DisplayMode((Minecraft.getMinecraft()).displayWidth, (Minecraft.getMinecraft()).displayHeight));
                displayCommon();
            }
            Display.setResizable(!fullscreen);
        } catch (LWJGLException ex) {
            Reference.LOGGER.error("Failed to update screen type", (Throwable) ex);
        }
    }
}
