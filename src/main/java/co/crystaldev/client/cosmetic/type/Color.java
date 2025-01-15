package co.crystaldev.client.cosmetic.type;

import co.crystaldev.client.cosmetic.CosmeticEntry;
import co.crystaldev.client.cosmetic.base.Cosmetic;
import co.crystaldev.client.util.enums.IconColor;
import org.jetbrains.annotations.NotNull;

public class Color extends Cosmetic {
    private final IconColor iconColor;

    public IconColor getIconColor() {
        return this.iconColor;
    }

    public Color(@NotNull CosmeticEntry entry) {
        super(entry);
        this.iconColor = IconColor.fromName(entry.getName());
    }
}