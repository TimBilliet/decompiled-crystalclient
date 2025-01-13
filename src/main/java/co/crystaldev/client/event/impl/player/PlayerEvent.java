package co.crystaldev.client.event.impl.player;

import co.crystaldev.client.event.Event;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

public class PlayerEvent extends Event {
    public final EntityPlayer player;

    protected PlayerEvent(EntityPlayer player) {
        this.player = player;
    }

    public static class LoggedIn extends PlayerEvent {
        public LoggedIn(EntityPlayer player) {
            super(player);
        }
    }

    public static class LoggedOut extends PlayerEvent {
        public LoggedOut(EntityPlayer player) {
            super(player);
        }
    }

    public static class Attack extends PlayerEvent {
        public final Entity target;

        public Attack(EntityPlayer player, Entity target) {
            super(player);
            this.target = target;
        }
    }

    public static class Damage extends PlayerEvent {
        private final DamageSource source;

        private final float health;

        private final float damageAmount;

        public DamageSource getSource() {
            return this.source;
        }

        public float getHealth() {
            return this.health;
        }

        public float getDamageAmount() {
            return this.damageAmount;
        }

        public Damage(EntityPlayer player, DamageSource damageSrc, float health, float damageAmount) {
            super(player);
            this.source = damageSrc;
            this.health = health;
            this.damageAmount = damageAmount;
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\event\impl\player\PlayerEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */