package com.github.timmekeclient.event.impl.world;

import com.github.timmekeclient.event.Cancellable;
import com.github.timmekeclient.event.Event;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.util.ResourceLocation;

@Cancellable
public class PlaySoundEvent extends Event {
    public final SoundManager manager;

    public final String name;

    public final ResourceLocation location;

    public final SoundCategory category;

    public PlaySoundEvent(SoundManager manager, ResourceLocation location, SoundCategory category) {
        this.manager = manager;
        this.location = location;
        this.name = this.location.getResourcePath();
        this.category = category;
    }
}
