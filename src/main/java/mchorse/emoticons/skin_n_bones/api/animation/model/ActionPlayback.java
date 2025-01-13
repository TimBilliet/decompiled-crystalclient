package mchorse.emoticons.skin_n_bones.api.animation.model;

import mchorse.emoticons.skin_n_bones.api.bobj.BOBJAction;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJBone;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJGroup;

public class ActionPlayback {
    public BOBJAction action;

    public ActionConfig config;

    private int fade;

    private float ticks;

    private final int duration;

    private float speed = 1.0F;

    private boolean looping = false;

    private boolean fading = false;

    public boolean playing = true;

    public int priority;

    public BOBJArmature customArmature;

    public ActionPlayback(BOBJAction action, ActionConfig config) {
        this(action, config, true);
    }

    public ActionPlayback(BOBJAction action, ActionConfig config, boolean looping) {
        this.action = action;
        this.config = config;
        this.duration = action.getDuration();
        this.looping = looping;
        setSpeed(1.0F);
    }

    public ActionPlayback(BOBJAction action, ActionConfig config, boolean looping, int priority) {
        this(action, config, looping);
        this.priority = priority;
    }

    public void reset() {
        if (this.config.reset)
            this.ticks = (Math.copySign(1.0F, this.speed) < 0.0F) ? this.duration : 0.0F;
        unfade();
    }

    public boolean finishedFading() {
        return (this.fading == true && this.fade <= 0);
    }

    public boolean isFading() {
        return (this.fading == true && this.fade > 0);
    }

    public void fade() {
        this.fade = (int) this.config.fade;
        this.fading = true;
    }

    public void unfade() {
        this.fade = 0;
        this.fading = false;
    }

    public float getFadeFactor(float partialTicks) {
        return (this.fade - partialTicks) / this.config.fade;
    }

    public void setSpeed(float speed) {
        this.speed = speed * this.config.speed;
    }

    public void update() {
        if (this.fading && this.fade > 0) {
            this.fade--;
            return;
        }
        if (!this.playing)
            return;
        this.ticks += this.speed;
        if (!this.looping && !this.fading && this.ticks >= this.duration)
            fade();
        if (this.looping)
            if (this.ticks >= this.duration && this.speed > 0.0F && this.config.clamp) {
                this.ticks -= this.duration;
                this.ticks += this.config.tick;
            } else if (this.ticks < 0.0F && this.speed < 0.0F && this.config.clamp) {
                this.ticks = this.duration + this.ticks;
                this.ticks -= this.config.tick;
            }
    }

    public float getTick(float partialTick) {
        float ticks = this.ticks + partialTick * this.speed;
        if (this.looping)
            if (ticks >= this.duration && this.speed > 0.0F && this.config.clamp) {
                ticks -= this.duration;
            } else if (this.ticks < 0.0F && this.speed < 0.0F && this.config.clamp) {
                ticks = this.duration + ticks;
            }
        return ticks;
    }

    public void apply(BOBJArmature armature, float partialTick) {
        for (BOBJGroup group : this.action.groups.values()) {
            BOBJBone bone = (BOBJBone) armature.bones.get(group.name);
            if (bone != null)
                group.apply(bone, getTick(partialTick));
        }
    }

    public void applyInactive(BOBJArmature armature, float partialTick, float x) {
        for (BOBJGroup group : this.action.groups.values()) {
            BOBJBone bone = (BOBJBone) armature.bones.get(group.name);
            if (bone != null)
                group.applyInterpolate(bone, this.ticks, x);
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\animation\model\ActionPlayback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */