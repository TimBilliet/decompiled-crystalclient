package com.github.lunatrius.schematica.handler.client;

import co.crystaldev.client.event.SubscribeEvent;
import co.crystaldev.client.event.impl.render.RenderTickEvent;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class RenderTickHandler {
    public static final RenderTickHandler INSTANCE = new RenderTickHandler();

    private final Minecraft minecraft = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRenderTick(RenderTickEvent.Post event) {
        SchematicWorld schematic = ClientProxy.currentSchematic.schematic;
        ClientProxy.movingObjectPosition = (schematic != null) ? rayTrace(schematic, 1.0F) : null;
    }

    private MovingObjectPosition rayTrace(SchematicWorld schematic, float partialTicks) {
        Entity renderViewEntity = this.minecraft.getRenderViewEntity();
        if (renderViewEntity == null)
            return null;
        double blockReachDistance = this.minecraft.playerController.getBlockReachDistance();
        double posX = renderViewEntity.posX;
        double posY = renderViewEntity.posY;
        double posZ = renderViewEntity.posZ;
        renderViewEntity.posX -= schematic.position.x;
        renderViewEntity.posY -= schematic.position.y;
        renderViewEntity.posZ -= schematic.position.z;
        Vec3 vecPosition = renderViewEntity.getPositionEyes(partialTicks);
        Vec3 vecLook = renderViewEntity.getLook(partialTicks);
        Vec3 vecExtendedLook = vecPosition.addVector(vecLook.xCoord * blockReachDistance, vecLook.yCoord * blockReachDistance, vecLook.zCoord * blockReachDistance);
        renderViewEntity.posX = posX;
        renderViewEntity.posY = posY;
        renderViewEntity.posZ = posZ;
        return schematic.rayTraceBlocks(vecPosition, vecExtendedLook, false, false, true);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\handler\client\RenderTickHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */