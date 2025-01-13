package co.crystaldev.client.mixin.net.minecraft.client.audio;

import co.crystaldev.client.event.impl.world.PlaySoundEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulscode.sound.SoundSystem;

import java.util.*;

@Mixin({SoundManager.class})
public abstract class MixinSoundManager {
    @Shadow
    @Final
    private Map<String, ISound> playingSounds;

    @Unique
    private final List<String> pausedSounds = new ArrayList<>();

    @Shadow
    public abstract boolean isSoundPlaying(ISound paramISound);


    @Redirect(method = {"playSound"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/SoundHandler;getSound(Lnet/minecraft/util/ResourceLocation;)Lnet/minecraft/client/audio/SoundEventAccessorComposite;"))
    private SoundEventAccessorComposite SoundHandler$getSound(SoundHandler soundHandler, ResourceLocation location) {
        SoundEventAccessorComposite soundeventaccessorcomposite = soundHandler.getSound(location);
        if (soundeventaccessorcomposite != null) {
            PlaySoundEvent event = new PlaySoundEvent((SoundManager) (Object) this, location, soundeventaccessorcomposite.getSoundCategory());
            event.call();
            if (event.isCancelled())
                return null;
        }
        return soundeventaccessorcomposite;
    }

//    @Redirect(method = {"pauseAllSounds"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/SoundManager$SoundSystemStarterThread;pause(Ljava/lang/String;)V", remap = false))
//    private void onlyPauseSoundIfNecessary(@Coerce SoundSystem soundSystem, String sound) {
//        if (isSoundPlaying(this.playingSounds.get(sound))) {
//            soundSystem.pause(sound);
//            this.pausedSounds.add(sound);
//        }
//    }

    @Redirect(method = {"resumeAllSounds"}, at = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;", remap = false))
    private Iterator<String> iterateOverPausedSounds(Set<String> keySet) {
        return this.pausedSounds.iterator();
    }

    @Inject(method = {"resumeAllSounds"}, at = {@At("TAIL")})
    private void clearPausedSounds(CallbackInfo ci) {
        this.pausedSounds.clear();
    }

    @Redirect(method = {"playSound"}, slice = @Slice(from = @At(value = "CONSTANT", args = {"stringValue=Unable to play unknown soundEvent: {}"}, ordinal = 0)), at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Lorg/apache/logging/log4j/Marker;Ljava/lang/String;[Ljava/lang/Object;)V", ordinal = 0, remap = false))
    private void silenceWarning(Logger instance, Marker marker, String s, Object[] objects) {
    }
}
