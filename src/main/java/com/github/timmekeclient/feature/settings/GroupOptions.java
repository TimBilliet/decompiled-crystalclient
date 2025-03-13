package com.github.timmekeclient.feature.settings;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.feature.annotations.properties.Keybind;
import com.github.timmekeclient.feature.annotations.properties.Toggle;
import net.minecraft.client.settings.KeyBinding;

public class GroupOptions {
    @Toggle(label = "Group Patchcrumb")
    public boolean sharedCrumb = true;

    @Toggle(label = "Shared Adjusts")
    public boolean sharedAdjusts = true;

    @Toggle(label = "Shared Float Positions")
    public boolean sharedFloatPos = true;

    @Toggle(label = "Show Members on Map")
    public boolean showMembersOnMap = true;

    @Toggle(label = "Only Show Members on Fullscreen Map")
    public boolean onlyShowOnBigMap = false;

    @Toggle(label = "Group Chat")
    public boolean groupChat = false;

    @Keybind(label = "Group Chat Toggle")
    public KeyBinding groupChatToggle = new KeyBinding("timmekeclient.key.toggle_group_chat", 0, "Timmeke_ Client");

    @Keybind(label = "Ping Location")
    public KeyBinding pingLocation = new KeyBinding("timmekeclient.key.ping_group_location", 0, "Timmeke_ Client");

    private static GroupOptions INSTANCE;

    public GroupOptions() {
        INSTANCE = this;
        Client.registerKeyBinding(this.groupChatToggle);
        Client.registerKeyBinding(this.pingLocation);
    }

    public static GroupOptions getInstance() {
        return (INSTANCE == null) ? (INSTANCE = new GroupOptions()) : INSTANCE;
    }
}
