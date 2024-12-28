package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.network.PacketReceivedEvent;
import co.crystaldev.client.event.impl.player.PlayerEvent;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import co.crystaldev.client.feature.annotations.ConfigurableSize;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModuleBackground;
import co.crystaldev.client.util.enums.AnchorRegion;
import co.crystaldev.client.util.objects.ModulePosition;
import co.crystaldev.client.util.type.Tuple;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.world.World;

@ConfigurableSize
@ModuleInfo(name = "Combo Count", description = "Displays your current combo on the screen", category = Category.HUD)
public class ComboCount extends HudModuleBackground implements IRegistrable {
    private int counter = 0;

    private int currentEntityId = 0;

    private long lastAttack = 0L;

    public ComboCount() {
        this.enabled = false;
        this.hasInfoHud = true;
        this.position = new ModulePosition(AnchorRegion.TOP_LEFT, 5.0F, 163.0F);
        this.width = 60;
        this.height = 18;
    }

    public String getDisplayText() {
        return this.counter + " hits";
    }

    public Tuple<String, String> getInfoHud() {
        return new Tuple("Combo", this.counter + " hits");
    }


    public void enable() {
        this.counter = 0;
        this.lastAttack = 0L;
        super.enable();
    }


    public void registerEvents() {
        EventBus.register(this, ClientTickEvent.Post.class, ev -> {
            if (System.currentTimeMillis() - this.lastAttack > 2000L) {
                this.counter = 0;
                this.lastAttack = 0L;
            }
        });
        EventBus.register(this, PlayerEvent.Attack.class, ev -> {
            if (ev.player == this.mc.thePlayer && this.currentEntityId != ev.target.getEntityId()) {
                this.counter = 0;

                this.currentEntityId = ev.target.getEntityId();
            }
        });

        EventBus.register(this, PacketReceivedEvent.Post.class, ev -> {
            if (ev.packet instanceof S19PacketEntityStatus) {
                S19PacketEntityStatus packet = (S19PacketEntityStatus) ev.packet;
                if (packet.getOpCode() == 2) {
                    Entity target = packet.getEntity((World) this.mc.theWorld);
                    if (target != null) {
                        if (target.getEntityId() == this.mc.thePlayer.getEntityId()) {
                            this.counter = 0;
                            this.lastAttack = 0L;
                            return;
                        }
                        if (target.getEntityId() == this.currentEntityId) {
                            this.counter++;
                            this.lastAttack = System.currentTimeMillis();
                        }
                    }
                }
            }
        });
    }
}
