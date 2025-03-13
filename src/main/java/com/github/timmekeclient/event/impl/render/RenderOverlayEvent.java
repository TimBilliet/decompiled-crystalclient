package com.github.timmekeclient.event.impl.render;

import com.github.timmekeclient.event.Cancellable;
import com.github.timmekeclient.event.Event;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class RenderOverlayEvent extends Event {
    public final float partialTicks;

    public RenderOverlayEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public static class All extends RenderOverlayEvent {
        public final ScaledResolution scaledResolution;

        public All(float partialTicks, ScaledResolution sr) {
            super(partialTicks);
            this.scaledResolution = sr;
        }
    }

    @Cancellable
    public static class BossBar extends RenderOverlayEvent {
        public BossBar(float partialTicks) {
            super(partialTicks);
        }
    }

    @Cancellable
    public static class Crosshair extends RenderOverlayEvent {
        private final boolean visible;

        public boolean isVisible() {
            return this.visible;
        }

        public Crosshair(float partialTicks, boolean visible) {
            super(partialTicks);
            this.visible = visible;
        }
    }

    public static class Gui extends RenderOverlayEvent {
        private final GuiScreen screen;

        public GuiScreen getScreen() {
            return this.screen;
        }

        public Gui(GuiScreen screen, float partialTicks) {
            super(partialTicks);
            this.screen = screen;
        }
    }

    public static class Title extends RenderOverlayEvent {
        private final String title;
        private final String subTitle;
        public String getSubTitle(){
            return subTitle;
        }
        public Title(String title, String subTitle, float partialTicks) {
            super(partialTicks);
            this.title= title;
            this.subTitle = subTitle;
        }
    }
}
