package com.github.timmekeclient.gui.screens;

import com.github.timmekeclient.feature.settings.ClientOptions;
import net.minecraft.client.gui.GuiScreen;

public class ScreenClientOptions extends ScreenSettings {
    public ScreenClientOptions(GuiScreen parent) {
        super(ClientOptions.getInstance(), parent);
    }
}
