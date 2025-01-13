package mchorse.mclib.utils;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.List;

public class RayTracing {
    public static Entity getTargetEntity(Entity input, double maxReach) {
        MovingObjectPosition result = rayTraceWithEntity(input, maxReach);
        return (result.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) ? result.entityHit : null;
    }

    public static MovingObjectPosition rayTraceWithEntity(Entity input, double maxReach) {
        double blockDistance = maxReach;
        MovingObjectPosition result = rayTrace(input, maxReach, 1.0F);
        Vec3 eyes = new Vec3(input.posX, input.posY + input.getEyeHeight(), input.posZ);
        if (result != null)
            blockDistance = result.hitVec.distanceTo(eyes);
        Vec3 look = input.getLook(1.0F);
        Vec3 max = eyes.addVector(look.xCoord * maxReach, look.yCoord * maxReach, look.zCoord * maxReach);
        Vec3 hit = null;
        Entity target = null;
        float area = 1.0F;
        List<Entity> list = input.worldObj.getEntitiesInAABBexcluding(input, input.getEntityBoundingBox().addCoord(look.xCoord * maxReach, look.yCoord * maxReach, look.zCoord * maxReach).expand(area, area, area), new Predicate<Entity>() {
            public boolean apply(Entity entity) {
                return (entity != null && entity.canBeCollidedWith());
            }
        });
        double entityDistance = blockDistance;
        for (int i = 0; i < list.size(); i++) {
            Entity entity = list.get(i);
            if (entity != input) {
                float size = entity.getCollisionBorderSize();
                AxisAlignedBB aabb = entity.getEntityBoundingBox().expand(size, size, size);
                MovingObjectPosition intercept = aabb.calculateIntercept(eyes, max);
                if (aabb.isVecInside(eyes)) {
                    if (entityDistance >= 0.0D) {
                        hit = (intercept == null) ? eyes : intercept.hitVec;
                        target = entity;
                        entityDistance = 0.0D;
                    }
                } else if (intercept != null) {
                    double eyesDistance = eyes.distanceTo(intercept.hitVec);
                    if (eyesDistance < entityDistance || entityDistance == 0.0D)
                        if (entity == input.ridingEntity && !input.canBeCollidedWith()) {
                            if (entityDistance == 0.0D) {
                                hit = intercept.hitVec;
                                target = entity;
                            }
                        } else {
                            hit = intercept.hitVec;
                            target = entity;
                            entityDistance = eyesDistance;
                        }
                }
            }
        }
        if (target != null)
            result = new MovingObjectPosition(target, hit);
        return result;
    }

    public static MovingObjectPosition rayTrace(Entity input, double blockReachDistance, float partialTicks) {
        Vec3 eyePos = new Vec3(input.posX, input.posY + input.getEyeHeight(), input.posZ);
        Vec3 eyeDir = input.getLook(partialTicks);
        Vec3 eyeReach = eyePos.addVector(eyeDir.xCoord * blockReachDistance, eyeDir.yCoord * blockReachDistance, eyeDir.zCoord * blockReachDistance);
        return input.worldObj.rayTraceBlocks(eyePos, eyeReach, false, false, true);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mcli\\utils\RayTracing.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */