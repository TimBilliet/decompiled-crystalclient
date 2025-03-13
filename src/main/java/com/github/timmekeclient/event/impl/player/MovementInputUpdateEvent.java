package com.github.timmekeclient.event.impl.player;

import com.github.timmekeclient.event.Event;
import net.minecraft.util.MovementInput;

public class MovementInputUpdateEvent extends Event {
    public final MovementInput input;

    public MovementInputUpdateEvent(MovementInput input) {
        this.input = input;
    }
}
