package co.crystaldev.client.mixin.net.minecraft.world;

import co.crystaldev.client.duck.TileEntityExt;
import co.crystaldev.client.feature.impl.all.Fullbright;
import co.crystaldev.client.feature.impl.mechanic.NoLag;
import co.crystaldev.client.feature.impl.mechanic.WeatherChanger;
import co.crystaldev.client.handler.ModuleHandler;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin({World.class})
public abstract class MixinWorld {
    @Unique
//  private final List<TileEntity> emptyTileEntityList = (List<TileEntity>)ImmutableList.of();
    private final List<TileEntity> emptyTileEntityList = new ArrayList<>();
    @Shadow
    @Final
    public boolean isRemote;

    @Shadow
    @Final
    private List<TileEntity> tileEntitiesToBeRemoved;

    @Shadow
    @Final
    public List<TileEntity> loadedTileEntityList;

    @Shadow
    @Final
    public List<TileEntity> tickableTileEntities;

    @ModifyVariable(method = {"updateEntityWithOptionalForce"}, at = @At("STORE"), ordinal = 1)
    private boolean checkIfWorldIsRemoteBeforeForceUpdating(boolean isForced) {
        return (isForced && !this.isRemote);
    }

    @Inject(method = {"getCollidingBoundingBoxes"}, cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT, at = {@At(value = "INVOKE", target = "Lnet/minecraft/world/World;getEntitiesWithinAABBExcludingEntity(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Ljava/util/List;")})
    private void filterEntities(Entity entityIn, AxisAlignedBB p_getCollidingBoundingBoxes_2_, CallbackInfoReturnable<List<AxisAlignedBB>> cir, List<AxisAlignedBB> aabb, int i0, int i1, int i2, int i3, int i4, int i5, WorldBorder border, boolean b0, boolean b1, IBlockState state, BlockPos.MutableBlockPos pos, double d0) {
        if (entityIn instanceof net.minecraft.entity.item.EntityTNTPrimed || entityIn instanceof net.minecraft.entity.item.EntityFallingBlock || entityIn instanceof net.minecraft.entity.item.EntityItem || entityIn instanceof net.minecraft.client.particle.EntityFX)
            cir.setReturnValue(aabb);
    }

    @Overwrite
    public double getHorizon() {
        return 0.0D;
    }

    @Inject(method = {"checkLightFor"}, cancellable = true, at = {@At("HEAD")})
    public void checkLightFor(EnumSkyBlock lightType, BlockPos pos, CallbackInfoReturnable<Boolean> ci) {
        if (lightType == EnumSkyBlock.SKY)
            ci.setReturnValue(Boolean.valueOf(false));
    }

    @Inject(method = {"getRainStrength"}, at = {@At("HEAD")}, cancellable = true)
    public void World$getRainStrength(float delta, CallbackInfoReturnable<Float> ci) {
        if (WeatherChanger.getInstance() != null && (WeatherChanger.getInstance()).enabled)
            if (WeatherChanger.getInstance().getState() == WeatherChanger.State.CLEAR) {
                ci.setReturnValue(Float.valueOf(0.0F));
            } else {
                ci.setReturnValue(Float.valueOf((float) (WeatherChanger.getInstance()).weatherModifier));
            }
    }

    @Inject(method = {"getThunderStrength"}, at = {@At("HEAD")}, cancellable = true)
    public void World$getThunderStrength(float delta, CallbackInfoReturnable<Float> ci) {
        if (WeatherChanger.getInstance() != null && (WeatherChanger.getInstance()).enabled)
            if (WeatherChanger.getInstance().getState() == WeatherChanger.State.STORMING) {
                ci.setReturnValue(Float.valueOf((float) (WeatherChanger.getInstance()).weatherModifier));
            } else {
                ci.setReturnValue(Float.valueOf(0.0F));
            }
    }

    @Inject(method = {"checkLight"}, cancellable = true, at = {@At("HEAD")})
    public void checkLight(CallbackInfoReturnable<Boolean> ci) {
        if ((Fullbright.getInstance()).enabled)
            ci.setReturnValue(Boolean.valueOf(true));
    }

    @Inject(method = {"getRawLight"}, cancellable = true, at = {@At("HEAD")})
    public void getRawLight(CallbackInfoReturnable<Integer> ci) {
        if ((Fullbright.getInstance()).enabled)
            ci.setReturnValue(Integer.valueOf(15));
    }

    @Redirect(method = {"updateEntities"}, at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;"))
    private Iterator<TileEntity> updateEntities(List<TileEntity> instance) {
        if (!Minecraft.getMinecraft().isSingleplayer() && (NoLag.getInstance()).enabled) {
            int tickRate = (NoLag.getInstance()).tileEntityTickRate;
            if (tickRate > 1 && ModuleHandler.getTotalTicks() % tickRate != 0L)
                return this.emptyTileEntityList.iterator();
        }
        return instance.iterator();
    }

    @Redirect(method = {"updateEntities"}, at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", ordinal = 0))
    private boolean List$isEmpty(List<TileEntity> instance) {
        if (!instance.isEmpty()) {
            instance.removeIf(te -> ((TileEntityExt) te).crystal$shouldBeRemoved());
            this.loadedTileEntityList.removeIf(te -> ((TileEntityExt) te).crystal$shouldBeRemoved());
            this.tickableTileEntities.removeIf(te -> ((TileEntityExt) te).crystal$shouldBeRemoved());
        }
        return true;
    }

    @Overwrite(aliases = {"markTileEntityForRemoval"})
    public void markTileEntityForRemoval(TileEntity tileEntity) {
        TileEntityExt te = (TileEntityExt) tileEntity;
        te.crystal$setShouldBeRemoved(true);
        te.crystal$setRemovalTick(ModuleHandler.getTotalTicks() + (this.tileEntitiesToBeRemoved.size() % 250));
        this.tileEntitiesToBeRemoved.add(tileEntity);
    }

    @Shadow
    public abstract void tick();

    @Shadow
    public abstract boolean tickUpdates(boolean paramBoolean);
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\mixin\net\minecraft\world\MixinWorld.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */