package co.crystaldev.client.group;

import co.crystaldev.client.Client;
import co.crystaldev.client.feature.impl.hud.PotionCount;
import co.crystaldev.client.network.Packet;
import co.crystaldev.client.network.socket.client.group.PacketStatusUpdate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class StatusUpdateTask implements Runnable {
    public void run() {
        EntityPlayerSP entityPlayerSP = (Minecraft.getMinecraft()).thePlayer;
        if (entityPlayerSP != null && GroupManager.getSelectedGroup() != null) {
            String uuid = Client.getUniqueID().toString();
            int x = MathHelper.floor_double(((EntityPlayer) entityPlayerSP).posX);
            int y = MathHelper.floor_double(((EntityPlayer) entityPlayerSP).posY);
            int z = MathHelper.floor_double(((EntityPlayer) entityPlayerSP).posZ);
            int health = (int) Math.ceil(entityPlayerSP.getHealth());
            int pots = PotionCount.getPotionCount((EntityPlayer) entityPlayerSP);
            ItemStack isHelm = entityPlayerSP.getCurrentArmor(3);
            ItemStack isBoots = entityPlayerSP.getCurrentArmor(0);

            float helmet = (isHelm == null || !isHelm.isItemStackDamageable()) ? -1.0F : MathHelper.clamp_float((isHelm.getMaxDamage() - (float) isHelm.getItemDamage()) / isHelm.getMaxDamage(), 0.0F, 1.0F);
            float boots = (isBoots == null || !isBoots.isItemStackDamageable()) ? -1.0F : MathHelper.clamp_float((isBoots.getMaxDamage() - (float) isBoots.getItemDamage()) / isBoots.getMaxDamage(), 0.0F, 1.0F);

            PacketStatusUpdate packet = new PacketStatusUpdate(uuid, x, y, z, health, pots, helmet, boots);
            Client.sendPacket((Packet) packet);
        }
    }
}