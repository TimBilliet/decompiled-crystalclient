package co.crystaldev.client.feature.impl.mechanic;

import co.crystaldev.client.Client;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.render.GuiScreenEvent;
import co.crystaldev.client.event.impl.render.RenderTickEvent;
import co.crystaldev.client.event.impl.world.WorldEvent;
import co.crystaldev.client.feature.annotations.properties.Keybind;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.opengl.Display;

@ModuleInfo(name = "Perspective Mod", description = "Allows you to freely look around your player", category = Category.MECHANIC, nameAliases = {"Freelook"})
public class PerspectiveMod extends Module implements IRegistrable {
    @Keybind(label = "Keybind")
    public KeyBinding keybind = new KeyBinding("crystalclient.key.toggle_perspective_mod", 56, "Crystal Client");

    @Toggle(label = "Hold Mode")
    public boolean holdMode = true;

    @Toggle(label = "Invert Pitch")
    public boolean invertPitch = true;

    private static PerspectiveMod INSTANCE;

    public boolean perspectiveToggled = false;

    public float cameraYaw = 0.0F;

    public float cameraPitch = 0.0F;

    private int previousPerspective = 0;

    private boolean prevState = false;

    public PerspectiveMod() {
        INSTANCE = this;
        this.enabled = true;
    }

    public void disable() {
        resetPerspective();
        super.disable();
    }

    public boolean getDefaultForceDisabledState() {
        return Client.isOnHypixel();
    }

    public void onPressed(boolean state) {
        if (this.enabled) {
            if (state) {
                this.perspectiveToggled = !this.perspectiveToggled;
                this.cameraYaw = this.mc.thePlayer.rotationYaw;
                this.cameraPitch = this.mc.thePlayer.rotationPitch;
                if (this.perspectiveToggled) {
                    this.previousPerspective = this.mc.gameSettings.thirdPersonView;
                    this.mc.gameSettings.thirdPersonView = 1;
                } else {
                    this.mc.gameSettings.thirdPersonView = this.previousPerspective;
                }
            } else if (this.holdMode) {
                resetPerspective();
            }
        } else if (this.perspectiveToggled) {
            resetPerspective();
        }
    }

    public boolean overrideMouse() {
        if (this.mc.inGameHasFocus && Display.isActive()) {
            if (!this.perspectiveToggled)
                return true;
            this.mc.mouseHelper.mouseXYChange();
            float f1 = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float f2 = f1 * f1 * f1 * 8.0F;
            float f3 = this.mc.mouseHelper.deltaX * f2;
            float f4 = this.mc.mouseHelper.deltaY * f2;
            if (this.invertPitch)
                f4 = -f4;
            this.cameraYaw += f3 * 0.15F;
            this.cameraPitch += f4 * 0.15F;
            if (this.cameraPitch > 90.0F)
                this.cameraPitch = 90.0F;
            if (this.cameraPitch < -90.0F)
                this.cameraPitch = -90.0F;
            this.mc.renderGlobal.setDisplayListEntitiesDirty();
        }
        return false;
    }

    public void resetPerspective() {
        this.perspectiveToggled = false;
        this.mc.gameSettings.thirdPersonView = this.previousPerspective;
    }

    public static PerspectiveMod getInstance() {
        return INSTANCE;
    }

    public void registerEvents() {
        EventBus.register(this, GuiScreenEvent.Pre.class, ev -> {
            if (ev != null && this.perspectiveToggled && this.holdMode)
                resetPerspective();
        });
        EventBus.register(this, WorldEvent.Load.class, ev -> {
            if (this.perspectiveToggled)
                resetPerspective();
        });
        EventBus.register(this, RenderTickEvent.Pre.class, ev -> {
            boolean down = this.keybind.isKeyDown();
            if (down != this.prevState && this.mc.currentScreen == null && this.mc.theWorld != null && this.mc.thePlayer != null) {
                this.prevState = down;
                onPressed(down);
            }
        });
    }
}

