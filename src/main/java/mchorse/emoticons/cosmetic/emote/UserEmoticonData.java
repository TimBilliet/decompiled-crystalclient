package mchorse.emoticons.cosmetic.emote;

import co.crystaldev.client.command.ThumbnailCommand;
import co.crystaldev.client.util.javax.Vector4f;
import mchorse.emoticons.api.animation.model.AnimatorEmoticonsController;
import mchorse.emoticons.common.EmoteAPI;
import mchorse.emoticons.common.emotes.Emote;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationMesh;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationMeshConfig;
import mchorse.emoticons.skin_n_bones.api.animation.model.ActionConfig;
import mchorse.emoticons.skin_n_bones.api.animation.model.ActionPlayback;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJBone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserEmoticonData implements IUserEmoteData {
  public AnimatorEmoticonsController animator;
  
  public ActionPlayback emoteAction;
  
  public Emote emote;
  
  private static final Map<UUID, IUserEmoteData> cache = new HashMap<>();
  
  private int emoteTimer;
  
  private double lastX;
  
  private double lastY;
  
  private double lastZ;
  
  public static IUserEmoteData get(Entity entity) {
    if (entity instanceof EntityPlayer)
      return cache.computeIfAbsent(entity.getUniqueID(), uuid -> new UserEmoticonData()); 
    return null;
  }
  
  public void setEmote(Emote emote, EntityLivingBase target) {
    if (target.worldObj.isRemote)
      stopAction(target); 
    this.emote = emote;
    this.emoteTimer = 0;
    if (target.worldObj.isRemote)
      setActionEmote(emote, target); 
  }
  
  public Emote getEmote() {
    return this.emote;
  }
  
  public void update(EntityLivingBase target) {
    if (target.worldObj.isRemote) {
      updateClient(target);
    } else {
      if (this.emote != null) {
        double diff = Math.abs(target.posX - this.lastX + target.posY - this.lastY + target.posZ - this.lastZ);
        if (diff > 0.015D || (!this.emote.looping && this.emoteTimer >= this.emote.duration) || (this.emote.looping && 
          ThumbnailCommand.isRendering() && this.emoteTimer >= this.emote.duration * 2))
          EmoteAPI.setEmoteClient("", (EntityPlayer)target); 
        this.emoteTimer++;
      } 
      this.lastX = target.posX;
      this.lastY = target.posY;
      this.lastZ = target.posZ;
    } 
  }
  
  private void updateClient(EntityLivingBase target) {
    if (this.emote != null) {
      double diff = Math.abs(target.posX - this.lastX + target.posY - this.lastY + target.posZ - this.lastZ);
      if (diff > 0.015D || (!this.emote.looping && this.emoteTimer >= this.emote.duration) || (this.emote.looping && 
        ThumbnailCommand.isRendering() && this.emoteTimer >= this.emote.duration * 2))
        setEmote(null, target); 
    } 
    this.lastX = target.posX;
    this.lastY = target.posY;
    this.lastZ = target.posZ;
    if (this.emote != null && this.emoteAction != null) {
      if (this.emote.sound != null && this.emoteAction.getTick(0.0F) == 0.0F)
        target.worldObj.playSound(target.posX, target.posY, target.posZ, this.emote.sound, 0.33F, 1.0F, false); 
      this.emoteTimer++;
    } 
    if (this.animator != null)
      this.animator.update(target); 
  }
  
  private void stopAction(EntityLivingBase target) {
    if (this.emote != null)
      this.emote.stopAnimation(this.animator); 
  }
  
  private void setActionEmote(Emote emote, EntityLivingBase target) {
    if (this.animator == null)
      setupAnimator(target); 
    if (emote != null) {
      ActionConfig config = this.animator.config.config.actions.getConfig("emote_" + emote.name);
      this.emoteAction = this.animator.animation.createAction(null, config, emote.looping);
      this.animator.setEmote(this.emoteAction);
      emote.startAnimation(this.animator);
    } else {
      this.emoteAction = null;
      this.animator.setEmote(null);
    } 
  }
  
  public boolean render(EntityLivingBase entity, double x, double y, double z, float partialTicks) {
    if (this.animator == null)
      setupAnimator(entity); 
    boolean render = (this.animator != null && this.emote != null);
    if (render) {
      if (entity instanceof AbstractClientPlayer) {
        AbstractClientPlayer player = (AbstractClientPlayer)entity;
        String type = player.getSkinType();
        if (!type.equals(this.animator.animationName)) {
          this.animator.animationName = type;
          this.animator.animation = null;
          this.animator.fetchAnimation();
        } 
        ((AnimationMeshConfig)this.animator.userConfig.meshes.get("body")).texture = player.getLocationSkin();
      } 
      this.animator.render(entity, x, y, z, 0.0F, partialTicks);
      BOBJArmature armature = ((AnimationMesh)this.animator.animation.meshes.get(0)).getCurrentArmature();
      Minecraft mc = Minecraft.getMinecraft();
      RenderManager manager = mc.getRenderManager();
      Render<?> renderLiving = manager.getEntityRenderObject((Entity)entity);
      if (renderLiving instanceof RendererLivingEntity) {
        Vector4f vec = this.animator.calcPosition(entity, (BOBJBone)armature.bones.get("head"), 0.0F, 0.0F, 0.0F, partialTicks);
        float nx = vec.x - (float)manager.viewerPosX;
        float ny = vec.y - (float)manager.viewerPosY + 0.425F - entity.height;
        float nz = vec.z - (float)manager.viewerPosZ;
        ((RendererLivingEntity)renderLiving).renderName(entity, nx, ny, nz);
      } 
      if (this.emote != null && !Minecraft.getMinecraft().isGamePaused()) {
        int tick = (int)this.emoteAction.getTick(0.0F);
        this.emote.progressAnimation(entity, armature, this.animator, tick, partialTicks);
      } 
    } 
    return render;
  }
  
  public void setupAnimator(EntityLivingBase entity) {
    AbstractClientPlayer player = (AbstractClientPlayer)entity;
    this.animator = new AnimatorEmoticonsController(player.getSkinType(), new NBTTagCompound());
    NBTTagCompound meshes = new NBTTagCompound();
    NBTTagCompound body = new NBTTagCompound();
    meshes.setTag("body", (NBTBase)body);
    body.setString("Texture", player.getLocationSkin().toString());
    this.animator.userData.setTag("Meshes", (NBTBase)meshes);
    this.animator.fetchAnimation();
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\cosmetic\emote\UserEmoticonData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */