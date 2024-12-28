package mchorse.emoticons.skin_n_bones.api.animation.model;

import mchorse.emoticons.skin_n_bones.api.animation.Animation;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Animator implements IAnimator {
  public ActionPlayback idle;
  
  public ActionPlayback walking;
  
  public ActionPlayback running;
  
  public ActionPlayback sprinting;
  
  public ActionPlayback crouching;
  
  public ActionPlayback crouchingIdle;
  
  public ActionPlayback swimming;
  
  public ActionPlayback swimmingIdle;
  
  public ActionPlayback flying;
  
  public ActionPlayback flyingIdle;
  
  public ActionPlayback riding;
  
  public ActionPlayback ridingIdle;
  
  public ActionPlayback dying;
  
  public ActionPlayback falling;
  
  public ActionPlayback sleeping;
  
  public ActionPlayback jump;
  
  public ActionPlayback swipe;
  
  public ActionPlayback hurt;
  
  public ActionPlayback land;
  
  public ActionPlayback shoot;
  
  public ActionPlayback consume;
  
  public ActionPlayback emote;
  
  public ActionPlayback active;
  
  public ActionPlayback lastActive;
  
  public List<ActionPlayback> actions = new ArrayList<>();
  
  public double prevX = 3.4028234663852886E38D;
  
  public double prevZ = 3.4028234663852886E38D;
  
  public double prevMY;
  
  public boolean wasOnGround = true;
  
  public boolean wasShooting = false;
  
  public boolean wasConsuming = false;
  
  public AnimatorController controller;
  
  public Animator(AnimatorController controller) {
    this.controller = controller;
    refresh();
  }
  
  public void refresh() {
    AnimatorActionsConfig actions = this.controller.userConfig.actions;
    Animation animation = this.controller.animation;
    this.idle = animation.createAction(this.idle, actions.getConfig("idle"), true);
    this.walking = animation.createAction(this.walking, actions.getConfig("walking"), true);
    this.running = animation.createAction(this.running, actions.getConfig("running"), true);
    this.sprinting = animation.createAction(this.sprinting, actions.getConfig("sprinting"), true);
    this.crouching = animation.createAction(this.crouching, actions.getConfig("crouching"), true);
    this.crouchingIdle = animation.createAction(this.crouchingIdle, actions.getConfig("crouching_idle"), true);
    this.swimming = animation.createAction(this.swimming, actions.getConfig("swimming"), true);
    this.swimmingIdle = animation.createAction(this.swimmingIdle, actions.getConfig("swimming_idle"), true);
    this.flying = animation.createAction(this.flying, actions.getConfig("flying"), true);
    this.flyingIdle = animation.createAction(this.flyingIdle, actions.getConfig("flying_idle"), true);
    this.riding = animation.createAction(this.riding, actions.getConfig("riding"), true);
    this.ridingIdle = animation.createAction(this.ridingIdle, actions.getConfig("riding_idle"), true);
    this.dying = animation.createAction(this.dying, actions.getConfig("dying"), false);
    this.falling = animation.createAction(this.falling, actions.getConfig("falling"), true);
    this.sleeping = animation.createAction(this.sleeping, actions.getConfig("sleeping"), true);
    this.swipe = animation.createAction(this.swipe, actions.getConfig("swipe"), false);
    this.jump = animation.createAction(this.jump, actions.getConfig("jump"), false, 2);
    this.hurt = animation.createAction(this.hurt, actions.getConfig("hurt"), false, 3);
    this.land = animation.createAction(this.land, actions.getConfig("land"), false);
    this.shoot = animation.createAction(this.shoot, actions.getConfig("shoot"), true);
    this.consume = animation.createAction(this.consume, actions.getConfig("consume"), true);
  }
  
  public void setEmote(ActionPlayback emote) {
    if (emote != null) {
      this.emote = emote;
    } else if (this.emote != null) {
      this.emote = null;
    } 
  }
  
  public void update(EntityLivingBase target) {
    if (this.prevX == 3.4028234663852886E38D) {
      this.prevX = target.posX;
      this.prevZ = target.posZ;
    } 
    controlActions(target);
    if (this.active != null)
      this.active.update(); 
    if (this.lastActive != null)
      this.lastActive.update(); 
    Iterator<ActionPlayback> it = this.actions.iterator();
    while (it.hasNext()) {
      ActionPlayback action = it.next();
      action.update();
      if (action.finishedFading()) {
        action.unfade();
        it.remove();
      } 
    } 
  }
  
  protected void controlActions(EntityLivingBase target) {
    double dx = target.posX - this.prevX;
    double dz = target.posZ - this.prevZ;
    boolean creativeFlying = (target instanceof EntityPlayer && ((EntityPlayer)target).capabilities.isFlying);
    boolean wet = target.isInWater();
    float threshold = creativeFlying ? 0.1F : (wet ? 0.025F : 0.01F);
    boolean moves = (Math.abs(dx) > threshold || Math.abs(dz) > threshold);
    if (this.emote != null) {
      setActiveAction(this.emote);
    } else if (target.getHealth() <= 0.0F) {
      setActiveAction(this.dying);
    } else if (target.isPlayerSleeping()) {
      setActiveAction(this.sleeping);
    } else if (wet) {
      setActiveAction(!moves ? this.swimmingIdle : this.swimming);
    } else if (target.isRiding()) {
      Entity riding = target.ridingEntity;
      moves = (Math.abs(riding.posX - this.prevX) > threshold || Math.abs(riding.posZ - this.prevZ) > threshold);
      this.prevX = riding.posX;
      this.prevZ = riding.posZ;
      setActiveAction(!moves ? this.ridingIdle : this.riding);
    } else if (creativeFlying) {
      setActiveAction(!moves ? this.flyingIdle : this.flying);
    } else {
      float speed = (float)(Math.round(Math.sqrt(dx * dx + dz * dz) * 1000.0D) / 1000.0D);
      if (target.isSneaking()) {
        speed /= 0.065F;
        setActiveAction(!moves ? this.crouchingIdle : this.crouching);
        if (this.crouching != null)
          this.crouching.setSpeed((target.moveForward > 0.0F) ? speed : -speed); 
      } else if (!target.onGround && target.motionY < 0.0D && target.fallDistance > 1.25D) {
        setActiveAction(this.falling);
      } else if (target.isSprinting() && this.sprinting != null) {
        setActiveAction(this.sprinting);
        this.sprinting.setSpeed(speed / 0.281F);
      } else {
        setActiveAction(!moves ? this.idle : this.running);
        speed /= 0.216F;
        if (this.running != null)
          this.running.setSpeed((target.moveForward >= 0.0F) ? speed : -speed); 
        if (this.walking != null)
          this.walking.setSpeed((target.moveForward > 0.0F) ? speed : -speed); 
      } 
      if (target.onGround && !this.wasOnGround && !target.isSprinting() && this.prevMY < -0.5D)
        addAction(this.land); 
    } 
    if (!target.onGround && this.wasOnGround && Math.abs(target.motionY) > 0.20000000298023224D) {
      addAction(this.jump);
      this.wasOnGround = false;
    } 
    boolean shooting = this.wasShooting;
    boolean consuming = this.wasConsuming;
    ItemStack stack = target.getHeldItem();
    if (stack != null) {
      this.wasShooting = false;
      this.wasConsuming = false;
    } else {
      this.wasShooting = false;
      this.wasConsuming = false;
    } 
    if (shooting && !this.wasShooting && this.shoot != null)
      this.shoot.fade(); 
    if (consuming && !this.wasConsuming && this.consume != null)
      this.consume.fade(); 
    if (target.hurtTime == target.maxHurtTime - 1)
      addAction(this.hurt); 
    if (target.isSwingInProgress && target.swingProgress == 0.0F && !target.isPlayerSleeping())
      addAction(this.swipe); 
    this.prevX = target.posX;
    this.prevZ = target.posZ;
    this.prevMY = target.motionY;
    this.wasOnGround = target.onGround;
  }
  
  public void setActiveAction(ActionPlayback action) {
    if (this.active == action || action == null)
      return; 
    if (this.active != null && action.priority < this.active.priority)
      return; 
    if (this.active != null) {
      this.lastActive = this.active;
      this.lastActive.fade();
    } 
    this.active = action;
    this.active.reset();
  }
  
  public void addAction(ActionPlayback action) {
    if (action == null)
      return; 
    if (this.actions.contains(action)) {
      action.reset();
      return;
    } 
    action.reset();
    this.actions.add(action);
  }
  
  public BOBJArmature useArmature(BOBJArmature armature) {
    if (this.active != null && this.active.customArmature != null)
      return this.active.customArmature; 
    return armature;
  }
  
  public void applyActions(BOBJArmature armature, float partialTicks) {
    if (this.active != null)
      this.active.apply(armature, partialTicks); 
    if (this.lastActive != null && this.lastActive.isFading())
      this.lastActive.applyInactive(armature, partialTicks, 1.0F - this.lastActive.getFadeFactor(partialTicks)); 
    for (ActionPlayback action : this.actions) {
      if (action.isFading()) {
        action.applyInactive(armature, partialTicks, 1.0F - action.getFadeFactor(partialTicks));
        continue;
      } 
      action.apply(armature, partialTicks);
    } 
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\animation\model\Animator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */