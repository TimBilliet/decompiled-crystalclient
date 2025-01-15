package co.crystaldev.client.mixin.net.minecraft.client.multiplayer;

import co.crystaldev.client.event.Event;
import co.crystaldev.client.event.impl.entity.EntitySpawnEvent;
import co.crystaldev.client.event.impl.network.ServerDisconnectEvent;
import co.crystaldev.client.event.impl.world.WorldEvent;
import co.crystaldev.client.feature.impl.mechanic.NoLag;
import co.crystaldev.client.feature.settings.ClientOptions;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wdl.WDLHooks;

import java.util.Random;

@Mixin({WorldClient.class})
public abstract class MixinWorldClient extends World {
    @Shadow
    private NetHandlerPlayClient sendQueue;

    protected MixinWorldClient(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn, Profiler profilerIn, boolean client) {
        super(saveHandlerIn, info, providerIn, profilerIn, client);
    }

    @Inject(method = {"<init>"}, at = {@At("RETURN")})
    private void constructorTail(NetHandlerPlayClient netHandler, WorldSettings settings, int dimension, EnumDifficulty difficulty, Profiler profilerIn, CallbackInfo ci) {
        if ((Object) this instanceof com.github.lunatrius.schematica.client.world.SchematicWorld) {
            (new WorldEvent.SchematicLoad(this, this.sendQueue)).call();
        } else {
            (new WorldEvent.Load(this, this.sendQueue)).call();
        }
    }

    @Inject(method = {"sendQuittingDisconnectingPacket"}, at = {@At("HEAD")})
    private void sendQuittingDisconnectingPacket(CallbackInfo ci) {
        (new ServerDisconnectEvent(this.sendQueue.getNetworkManager())).call();
    }

    @ModifyConstant(method = {"doVoidFogParticles"}, constant = {@Constant(intValue = 1000)})
    private int lowerTickCount(int original) {
        return NoLag.isEnabled((NoLag.getInstance()).lowAnimationTick) ? 250 : original;
    }

    @Redirect(method = {"doVoidFogParticles"}, at = @At(value = "NEW", target = "java/util/Random"))
    private Random replaceRandom() {
        return this.rand;
    }

    @ModifyVariable(method = {"doVoidFogParticles"}, at = @At("STORE"), ordinal = 0)
    private boolean showBarriers(boolean value) {
        return ((ClientOptions.getInstance()).showBarriers || value);
    }

    @Inject(method = {"tick"}, at = {@At("TAIL")})
    private void onWorldTick(CallbackInfo ci) {
        WDLHooks.onWorldClientTick((WorldClient) (Object) this);
    }

    @Inject(method = {"doPreChunk"}, at = {@At("HEAD")})
    private void doPreChunk(int p_73025_1_, int p_73025_2_, boolean p_73025_3_, CallbackInfo ci) {
        WDLHooks.onWorldClientDoPreChunk((WorldClient) (Object) this, p_73025_1_, p_73025_2_, p_73025_3_);
    }

    @Inject(method = {"removeEntityFromWorld"}, at = {@At("HEAD")})
    private void removeEntityFromWorld(int p_73028_1_, CallbackInfoReturnable<Entity> ci) {
        WDLHooks.onWorldClientRemoveEntityFromWorld((WorldClient) (Object) this, p_73028_1_);
    }

    @Inject(method = {"addEntityToWorld"}, cancellable = true, at = {@At(value = "INVOKE", target = "Ljava/util/Set;add(Ljava/lang/Object;)Z", shift = At.Shift.BEFORE, ordinal = 0)})
    public void entitySpawnEventPre(int entityID, Entity entityToSpawn, CallbackInfo ci) {
        Event event = (new EntitySpawnEvent.Pre(entityToSpawn)).call();
        if (event.isCancelled())
            ci.cancel();
    }

    @Inject(method = {"addEntityToWorld"}, at = {@At("TAIL")})
    public void entitySpawnEventPost(int entityID, Entity entityToSpawn, CallbackInfo ci) {
        (new EntitySpawnEvent.Post(entityToSpawn)).call();
    }
}
