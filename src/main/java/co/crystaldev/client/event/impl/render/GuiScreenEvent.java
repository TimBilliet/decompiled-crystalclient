package co.crystaldev.client.event.impl.render;

import co.crystaldev.client.event.Cancellable;
import co.crystaldev.client.event.Event;
import net.minecraft.client.gui.GuiScreen;

public class GuiScreenEvent extends Event {
    public GuiScreen gui;

    private GuiScreenEvent(GuiScreen gui) {
        this.gui = gui;
    }

    @Cancellable
    public static class Pre extends GuiScreenEvent {
        public final GuiScreen oldScreen;

        public Pre(GuiScreen gui, GuiScreen oldScreen) {
            super(gui);
            this.oldScreen = oldScreen;
        }
    }

    public static class Post extends GuiScreenEvent {
        public Post(GuiScreen gui) {
            super(gui);
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\render\GuiScreenEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */