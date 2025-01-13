package co.crystaldev.client.util.objects;

import co.crystaldev.client.feature.impl.mechanic.ToggleSneak;

public class ToggleSneakStatus {
    private static ToggleSneakStatus INSTANCE;

    private boolean fly;

    private boolean flyBoost;

    private boolean flyBoostToggled;

    private boolean sprintHeld;

    private boolean sneakHeld;

    private boolean sprintToggled;

    private boolean sneakToggled;

    private boolean sprintVanilla;

    private boolean riding;

    private boolean ridingDismount;

    private boolean useBrackets;

    public void setFly(boolean fly) {
        this.fly = fly;
    }

    public void setFlyBoost(boolean flyBoost) {
        this.flyBoost = flyBoost;
    }

    public void setFlyBoostToggled(boolean flyBoostToggled) {
        this.flyBoostToggled = flyBoostToggled;
    }

    public void setSprintHeld(boolean sprintHeld) {
        this.sprintHeld = sprintHeld;
    }

    public void setSneakHeld(boolean sneakHeld) {
        this.sneakHeld = sneakHeld;
    }

    public void setSprintToggled(boolean sprintToggled) {
        this.sprintToggled = sprintToggled;
    }

    public void setSneakToggled(boolean sneakToggled) {
        this.sneakToggled = sneakToggled;
    }

    public void setSprintVanilla(boolean sprintVanilla) {
        this.sprintVanilla = sprintVanilla;
    }

    public void setRiding(boolean riding) {
        this.riding = riding;
    }

    public void setRidingDismount(boolean ridingDismount) {
        this.ridingDismount = ridingDismount;
    }

    public void setUseBrackets(boolean useBrackets) {
        this.useBrackets = useBrackets;
    }

    public boolean isFly() {
        return this.fly;
    }

    public boolean isFlyBoost() {
        return this.flyBoost;
    }

    public boolean isFlyBoostToggled() {
        return this.flyBoostToggled;
    }

    public boolean isSprintHeld() {
        return this.sprintHeld;
    }

    public boolean isSneakHeld() {
        return this.sneakHeld;
    }

    public boolean isSprintToggled() {
        return this.sprintToggled;
    }

    public boolean isSneakToggled() {
        return this.sneakToggled;
    }

    public boolean isSprintVanilla() {
        return this.sprintVanilla;
    }

    public boolean isRiding() {
        return this.riding;
    }

    public boolean isRidingDismount() {
        return this.ridingDismount;
    }

    public boolean isUseBrackets() {
        return this.useBrackets;
    }

    public ToggleSneakStatus() {
        INSTANCE = this;
    }

    public String getStatusString(boolean useBrackets) {
        this.useBrackets = useBrackets;
        StringBuilder builder = new StringBuilder();
        if (this.fly)
            if (this.flyBoost || this.flyBoostToggled) {
                builder.append(String.format(StatusText.FLY_BOOST.toString(), new Object[]{Double.valueOf((ToggleSneak.getInstance()).flyBoostMultiplier)}));
            } else {
                builder.append(StatusText.FLY);
            }
        if ((this.sneakHeld || this.sneakToggled) && this.fly)
            builder.append(" ").append(StatusText.FLY_DESCEND);
        if (!this.fly) {
            if (this.sneakHeld)
                builder.append(StatusText.SNEAK);
            if ((ToggleSneak.getInstance()).toggleSneak && this.sneakToggled && !this.sneakHeld)
                builder.append(StatusText.SNEAK_TOGGLED);
            if (!this.sneakToggled && !this.sneakHeld) {
                if (this.sprintHeld)
                    builder.append(StatusText.SPRINT);
                if ((ToggleSneak.getInstance()).toggleSprint && this.sprintToggled)
                    builder.append(StatusText.SPRINT_TOGGLED);
                if (this.sprintVanilla && !this.sprintHeld)
                    builder.append(StatusText.SPRINT_VANILLA);
            }
        }
        if (this.riding)
            builder.append(StatusText.RIDING);
        if (this.riding && this.sneakHeld)
            builder.append(" ").append(StatusText.RIDING_DISMOUNT);
        return builder.toString();
    }

    public static ToggleSneakStatus getInstance() {
        return (INSTANCE == null) ? new ToggleSneakStatus() : INSTANCE;
    }

    public enum StatusText {
        FLY("[Flying]", "Flying"),
        FLY_BOOST("[Flying (%.2fx boost)]", "Flying (%.2fx boost)"),
        FLY_DESCEND("[Descending]", "(Descending)"),
        SPRINT("[Sprinting (Key Held)]", "Sprinting (Key Held)"),
        SNEAK("[Sneaking (Key Held)] ", "Sneaking (Key Held) "),
        SPRINT_TOGGLED("[Sprinting (Toggled)] ", "Sprinting (Toggled) "),
        SNEAK_TOGGLED("[Sneaking (Toggled)]", "Sneaking (Toggled)"),
        SPRINT_VANILLA("[Sprinting (Vanilla)] ", "Sprinting (Vanilla) "),
        RIDING("[Riding]", "(Riding)"),
        RIDING_DISMOUNT("[Dismounting]", "(Dismounting)");

        private final String text;

        private final String noBrackets;

        StatusText(String text, String noBrackets) {
            this.text = text;
            this.noBrackets = noBrackets;
        }

        public String toString() {
            return (ToggleSneakStatus.getInstance()).useBrackets ? this.text : this.noBrackets;
        }
    }
}