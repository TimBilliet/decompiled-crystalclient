package co.crystaldev.client.feature.impl.mechanic;

import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.init.ModuleOptionUpdateEvent;
import co.crystaldev.client.event.impl.player.MovementInputUpdateEvent;
import co.crystaldev.client.event.impl.render.GuiScreenEvent;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.PageBreak;
import co.crystaldev.client.feature.annotations.properties.Slider;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModuleText;
import co.crystaldev.client.feature.impl.hud.InfoHud;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.entity.MixinEntityPlayerSP;
import co.crystaldev.client.util.enums.AnchorRegion;
import co.crystaldev.client.util.objects.ModulePosition;
import co.crystaldev.client.util.objects.ToggleSneakStatus;
import co.crystaldev.client.util.type.Tuple;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.potion.Potion;

@ModuleInfo(name = "Toggle Sneak/Sprint", description = "Ability to toggle your sprinting/sneaking", category = Category.MECHANIC, nameAliases = {"Toggle Sneak"})
public class ToggleSneak extends HudModuleText implements IRegistrable {
    @Toggle(label = "Toggle Sprint")
    public boolean toggleSprint = true;

    @Toggle(label = "Toggle Sneak")
    public boolean toggleSneak = false;

    @Toggle(label = "Double Tap Sprint")
    public boolean doubleTapSprint = true;

    @PageBreak(label = "Fly Boost")
    @Toggle(label = "Fly Boost")
    public boolean flyBoost = true;

    @Toggle(label = "Fly Boost Hold")
    public boolean flyBoostHold = false;

    @Toggle(label = "Vertical Boost")
    public boolean verticalBoost = true;

    @Slider(label = "Fly Boost Multiplier", placeholder = "{value}x", minimum = 1.0D, maximum = 10.0D, standard = 2.0D)
    public double flyBoostMultiplier = 2.0D;

    @Slider(label = "Key Press Threshold", placeholder = "{value}ms", minimum = 50.0D, maximum = 1000.0D, standard = 300.0D, integers = true)
    public int pressLength = 300;

    private static ToggleSneak INSTANCE;

    private final GameSettings gameSettings;

    private ToggleSneakStatus status;

    private String displayString = "";

    private long sneakPressStart;

    private long sprintPressStart;

    public ToggleSneak() {
        this.enabled = true;
        this.hasInfoHud = true;
        this.width = this.mc.fontRendererObj.getStringWidth(ToggleSneakStatus.StatusText.SPRINT.toString());

        this.height = this.mc.fontRendererObj.FONT_HEIGHT;
        this.gameSettings = this.mc.gameSettings;
        this.status = ToggleSneakStatus.getInstance();
        this.position = new ModulePosition(AnchorRegion.TOP_RIGHT, 5.0F, 5.0F);
        this.dynamicSize = true;
        INSTANCE = this;
    }

    public void configPostInit() {
        super.configPostInit();
        setOptionVisibility("Fly Boost Hold", f -> this.flyBoost);
        setOptionVisibility("Vertical Boost", f -> this.flyBoost);
        setOptionVisibility("Fly Boost Multiplier", f -> this.flyBoost);
    }

    public Tuple<String, String> getInfoHud() {
        if (this.displayString.isEmpty())
            return null;
        return new Tuple<>("TS", this.displayString);
    }

    public String getDisplayText() {
        return this.displayString;
    }

    public String getDefaultDisplayText() {
        this.status.setUseBrackets((!this.infoHudEnabled || !(InfoHud.getInstance()).enabled));
        return ToggleSneakStatus.StatusText.SPRINT.toString();
    }

    public void enable() {
        super.enable();
        this.status = new ToggleSneakStatus();
    }

    private void onMovementUpdate(MovementInputUpdateEvent event) {
        EntityPlayerSP player = this.mc.thePlayer;
        if (player == null)
            return;
        boolean sneakKeyDown = this.gameSettings.keyBindSneak.isPressed();
        boolean sprintKeyDown = this.gameSettings.keyBindSprint.isPressed();
        boolean jumpKeyDown = this.gameSettings.keyBindJump.isPressed();
        if (sneakKeyDown && this.sneakPressStart == 0L) {
            this.sneakPressStart = System.currentTimeMillis();
        } else if (!sneakKeyDown) {
            long diff = System.currentTimeMillis() - this.sneakPressStart;
            if (diff < this.pressLength && !this.status.isRidingDismount() && this.toggleSneak)
                this.status.setSneakToggled(!this.status.isSneakToggled());
            this.sneakPressStart = 0L;
        }
        if (this.toggleSneak && this.status.isSneakToggled()) {
            player.setSneaking(true);
            event.input.sneak = true;
            event.input.moveStrafe = (float) (event.input.moveStrafe * 0.3D);
            event.input.moveForward = (float) (event.input.moveForward * 0.3D);
        }
        if (sneakKeyDown && !this.status.isSneakToggled()) {
            this.status.setSneakHeld(true);
        } else if (this.status.isSneakToggled() || !sneakKeyDown) {
            this.status.setSneakHeld(false);
        }
        boolean shouldSprint = (this.toggleSprint && !player.isSprinting() && this.status.isSprintToggled() && event.input.moveForward > 0.0F && !player.isSneaking() && (player.getFoodStats().getFoodLevel() > 6 || this.mc.playerController.isInCreativeMode()) && !player.isUsingItem() && !player.isPotionActive(Potion.blindness));
        if (shouldSprint)
            player.setSprinting(true);
        if (sprintKeyDown && this.sprintPressStart == 0L) {
            this.sprintPressStart = System.currentTimeMillis();
        } else if (!sprintKeyDown) {
            long diff = System.currentTimeMillis() - this.sprintPressStart;
            if (diff < this.pressLength && this.toggleSprint) {
                this.status.setSprintToggled(!this.status.isSprintToggled());
                if (player.capabilities.isFlying && !this.flyBoostHold)
                    this.status.setFlyBoostToggled(this.status.isSprintToggled());
            }
            this.sprintPressStart = 0L;
        }
        if (sprintKeyDown && !this.status.isSprintToggled()) {
            this.status.setSprintHeld(true);
        } else if (this.status.isSneakToggled() || !sprintKeyDown) {
            this.status.setSprintHeld(false);
        }
        if (player.capabilities.isFlying && ((this.flyBoostHold && sprintKeyDown) || this.status.isFlyBoostToggled()) && this.flyBoost) {
            this.status.setFlyBoost(true);
            player.capabilities.setFlySpeed(0.05F * (float) this.flyBoostMultiplier);
            if (this.verticalBoost) {
                if (sneakKeyDown)
                    player.motionY -= 0.15D * this.flyBoostMultiplier;
                if (jumpKeyDown)
                    player.motionY += 0.15D * this.flyBoostMultiplier;
            }
        } else if (player.capabilities.getFlySpeed() != 0.05F) {
            player.capabilities.setFlySpeed(0.05F);
            this.status.setFlyBoost(false);
        }
        this.status.setFly(player.capabilities.isFlying);
        this.status.setRiding(player.isRiding());
        this.status.setRidingDismount((player.isRiding() && sneakKeyDown));
        if (!this.doubleTapSprint)
            ((MixinEntityPlayerSP) player).setSprintToggleTimer(0);
        int doubleTapTimer = ((MixinEntityPlayerSP) player).getSprintToggleTimer();
        this.status.setSprintVanilla(((doubleTapTimer == 7 || !sprintKeyDown) && player.isSprinting() && !this.status.isSprintToggled()));
        this.displayString = this.status.getStatusString((!this.infoHudEnabled || !(InfoHud.getInstance()).enabled));
    }

    public static ToggleSneak getInstance() {
        return INSTANCE;
    }

    public void registerEvents() {
        EventBus.register(this, ModuleOptionUpdateEvent.class, ev -> {
            if (ev.getModule().equals(this) && (ev.getOptionName().equals("Fly Boost") || ev.getOptionName().equals("Fly Boost Hold")))
                this.status.setFlyBoostToggled(false);
        });
        EventBus.register(this, GuiScreenEvent.Pre.class, ev -> {
            if (this.status.isSneakToggled() || this.status.isSneakHeld()) {
                this.status.setSneakHeld(false);
                this.status.setSneakToggled(false);
            }
        });
        EventBus.register(this, MovementInputUpdateEvent.class, this::onMovementUpdate);
    }
}
