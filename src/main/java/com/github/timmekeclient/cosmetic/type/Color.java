package com.github.timmekeclient.cosmetic.type;

import com.github.timmekeclient.cosmetic.CosmeticEntry;
import com.github.timmekeclient.cosmetic.base.Cosmetic;
import com.github.timmekeclient.util.enums.IconColor;
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