package co.crystaldev.client.feature.impl.factions;

import co.crystaldev.client.Client;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.network.ServerDisconnectEvent;
import co.crystaldev.client.event.impl.player.InputEvent;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import co.crystaldev.client.event.impl.world.WorldEvent;
import co.crystaldev.client.feature.annotations.properties.Keybind;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Slider;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModuleBackground;
import co.crystaldev.client.mixin.accessor.net.minecraft.entity.item.MixinEntityFallingBlock;
import co.crystaldev.client.util.enums.AnchorRegion;
import co.crystaldev.client.util.objects.ModulePosition;
import co.crystaldev.client.util.objects.cannonplayback.PlaybackEntityFallingBlock;
import co.crystaldev.client.util.objects.cannonplayback.PlaybackEntityTNTPrimed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@ModuleInfo(name = "Cannon Playback", description = "Record and playback every tick of your cannon firing", category = Category.FACTIONS)
public class CannonPlayback extends HudModuleBackground implements IRegistrable {
    @Slider(label = "Max Recording Time", placeholder = "{value} ticks", minimum = 1.0D, maximum = 800.0D, standard = 200.0D, integers = true)
    public int maxTime = 200;

    @Toggle(label = "Record TNT")
    public boolean recordTnt = true;

    @Toggle(label = "Record Sand")
    public boolean recordSand = true;

    @Keybind(label = "Start Recording")
    public KeyBinding record = new KeyBinding("crystalclient.key.start_recording", 0, "Crystal Client - Cannon Playback");

    @Keybind(label = "Reset Recording")
    public KeyBinding reset = new KeyBinding("crystalclient.key.reset_recording", 0, "Crystal Client - Cannon Playback");

    @Keybind(label = "Previous Frame")
    public KeyBinding prevFrame = new KeyBinding("crystalclient.key.previous_frame", 0, "Crystal Client - Cannon Playback");

    @Keybind(label = "Next Frame")
    public KeyBinding nextFrame = new KeyBinding("crystalclient.key.next_frame", 0, "Crystal Client - Cannon Playback");

    private final List<Frame> frames = new LinkedList<>();

    public List<Frame> getFrames() {
        return this.frames;
    }

    private Frame currentFrame = null;

    private boolean recording = false;

    public boolean isRecording() {
        return this.recording;
    }

    private boolean playback = false;

    public boolean isPlayback() {
        return this.playback;
    }

    public CannonPlayback() {
        this.enabled = false;
        this.width = this.mc.fontRendererObj.getStringWidth("Frame 999/999 - 9999 TNT | 9999 Sand") + 12;
        this.height = 18;
        this.position = new ModulePosition(AnchorRegion.TOP_CENTER, 0.0F, 63.0F);
    }

    private void onKeyPressed(InputEvent.Key event) {
        if (this.record.isPressed())
            if (this.recording) {
                if (this.frames.isEmpty())
                    return;
                this.recording = false;
                this.playback = true;
                switchToFrame(this.currentFrame = getFirstFrame());
                Client.sendMessage("Stopped recording and began playback.", true);
            } else {
                reset();
                this.recording = true;
                Client.sendMessage("Reset playback and began recording.", true);
            }
        if (this.reset.isPressed()) {
            Client.sendMessage("Resetting playback.", true);
            reset();
            return;
        }
        if (this.playback) {
            if (this.recording) {
                Client.sendErrorMessage("You shouldn't be playing back and recording at the same time!", true);
                reset();
                return;
            }
            if (this.currentFrame == null || this.frames.isEmpty())
                return;
            int index = Math.max(this.frames.indexOf(this.currentFrame), 0);
            if (this.prevFrame.isPressed()) {
                if (index == 0) {
                    Client.sendErrorMessage("Already at the first frame.", true);
                    return;
                }
                switchToFrame(this.frames.get(--index));
            }
            if (this.nextFrame.isPressed()) {
                if (index == this.frames.size() - 1) {
                    Client.sendErrorMessage("Already at the last frame.", true);
                    return;
                }
                switchToFrame(this.frames.get(++index));
            }
        }
    }

    public String getDisplayText() {

        if (this.recording)
            return String.format("Recording (Frame %d/%d - %.2f%c)", new Object[]{Integer.valueOf(this.frames.size()), Integer.valueOf(this.maxTime),
                    Double.valueOf(this.frames.size() / this.maxTime * 100.0D), Character.valueOf('%')});
        //TODO implementeren cannonplayback
        // if (this.playback)
        //   return String.format("Frame %d/%d - %d TNT | %d Sand", new Object[] { Integer.valueOf(this.frames.indexOf(this.currentFrame) + 1),
        //        Integer.valueOf(this.frames.size()), Integer.valueOf(Frame.access$000(this.currentFrame)), Integer.valueOf(Frame.access$100(this.currentFrame)) });
        return null;
    }

    public String getDefaultDisplayText() {
        return "Frame 10/109 - 1009 TNT | 271 Sand";
    }

    public void draw() {
        String display = this.drawingDefaultText ? getDefaultDisplayText() : getDisplayText();
        if (display == null)
            return;
        super.draw();
    }

    private void reset() {
        this.recording = false;
        this.playback = false;
        this.frames.clear();
        this.currentFrame = null;
        if (this.mc.theWorld != null)
            for (Entity entity : this.mc.theWorld.loadedEntityList) {
                if (entity instanceof PlaybackEntityTNTPrimed || entity instanceof PlaybackEntityFallingBlock)
                    entity.setDead();
            }
    }

    private Frame getFirstFrame() {
        if (this.frames.isEmpty())
            return null;
        for (Frame frame : this.frames) {
            if (frame.tntSize != 0 || frame.sandSize != 0)
                return frame;
        }
        return this.frames.get(0);
    }

    private void switchToFrame(Frame frame) {
        if (this.currentFrame != null)
            for (Entity entity : this.mc.theWorld.loadedEntityList) {
                if (entity instanceof PlaybackEntityTNTPrimed || entity instanceof PlaybackEntityFallingBlock)
                    entity.setDead();
            }
        this.currentFrame = frame;
        for (FakeEntityPos tnt : frame.tnt) {
            PlaybackEntityTNTPrimed entity = new PlaybackEntityTNTPrimed(tnt.world, tnt.x, tnt.y, tnt.z);
            this.mc.theWorld.addEntityToWorld(entity.getEntityId(), (Entity) entity);
        }
        for (FakeEntityPos sand : frame.sand) {
            PlaybackEntityFallingBlock entity = new PlaybackEntityFallingBlock(sand.world, sand.x, sand.y, sand.z, sand.state);
            this.mc.theWorld.addEntityToWorld(entity.getEntityId(), (Entity) entity);
        }
    }

    public void registerEvents() {
        EventBus.register(this, ClientTickEvent.Pre.class, ev -> {
            if (this.mc.theWorld != null && this.recording)
                if (this.frames.size() < this.maxTime) {
                    this.frames.add(new Frame());
                } else {
                    this.recording = false;
                    this.playback = true;
                    Client.sendMessage(String.format("Recording has reached the maximum of %d frames.", new Object[]{Integer.valueOf(this.maxTime)}), true);
                    if (!this.frames.isEmpty())
                        switchToFrame(getFirstFrame());
                }
        });
        EventBus.register(this, InputEvent.Key.class, this::onKeyPressed);
        EventBus.register(this, ServerDisconnectEvent.class, ev -> reset());
        EventBus.register(this, WorldEvent.Load.class, ev -> reset());
    }

    private class Frame {
        private final Set<FakeEntityPos> tnt = new HashSet<>();

        private final Set<FakeEntityPos> sand = new HashSet<>();

        private final int tntSize;

        private final int sandSize;

        public Frame() {
            for (Entity entity : (Minecraft.getMinecraft()).theWorld.loadedEntityList) {
                if (entity instanceof net.minecraft.entity.item.EntityTNTPrimed && CannonPlayback.this.recordTnt) {
                    this.tnt.add(new FakeEntityPos(entity.worldObj, entity.posX, entity.posY, entity.posZ, null));
                    continue;
                }
                if (entity instanceof net.minecraft.entity.item.EntityFallingBlock && CannonPlayback.this.recordSand)
                    this.sand.add(new FakeEntityPos(entity.worldObj, entity.posX, entity.posY, entity.posZ, ((MixinEntityFallingBlock) entity).getFallTile()));
            }
            this.tntSize = this.tnt.size();
            this.sandSize = this.sand.size();
        }
    }

    private static class FakeEntityPos {
        private final World world;

        private final double x;

        private final double y;

        private final double z;

        private final IBlockState state;

        public FakeEntityPos(World world, double x, double y, double z, IBlockState state) {
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.state = state;
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\factions\CannonPlayback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */