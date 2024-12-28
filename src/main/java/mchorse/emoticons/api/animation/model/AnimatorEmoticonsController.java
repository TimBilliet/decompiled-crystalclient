package mchorse.emoticons.api.animation.model;

import com.google.common.collect.Maps;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationMesh;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationMeshConfig;
import mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorController;
import mchorse.emoticons.skin_n_bones.api.animation.model.AnimatorHeldItemConfig;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class AnimatorEmoticonsController extends AnimatorController {
  private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();
  
  public ItemStack itemSlot = null;
  
  public float itemSlotScale = 0.0F;
  
  public AnimatorEmoticonsController(String animationName, NBTTagCompound userData) {
    super(animationName, userData);
  }
  
  public void renderAnimation(EntityLivingBase entity, AnimationMesh mesh, float yaw, float partialTicks) {
    updateArmor(entity);
    super.renderAnimation(entity, mesh, yaw, partialTicks);
  }
  
  protected void renderItems(EntityLivingBase entity, BOBJArmature armature) {
    if (!this.userConfig.renderHeldItems)
      return; 
    float scale = this.userConfig.scaleItems;
    ItemStack mainItem = entity.getHeldItem();
    if (this.itemSlot != null && this.userConfig.rightHands != null) {
      if (this.itemSlotScale > 0.0F)
        for (AnimatorHeldItemConfig itemConfig : this.userConfig.rightHands.values())
          renderItem(entity, this.itemSlot, armature, itemConfig, ItemCameraTransforms.TransformType.THIRD_PERSON, scale * this.itemSlotScale);  
    } else if (mainItem != null && this.userConfig.rightHands != null) {
      for (AnimatorHeldItemConfig itemConfig : this.userConfig.rightHands.values())
        renderItem(entity, mainItem, armature, itemConfig, ItemCameraTransforms.TransformType.THIRD_PERSON, scale); 
    } 
  }
  
  private void updateArmor(EntityLivingBase entity) {
    AnimationMeshConfig helmet = (AnimationMeshConfig)this.userConfig.meshes.get("armor_helmet");
    AnimationMeshConfig chest = (AnimationMeshConfig)this.userConfig.meshes.get("armor_chest");
    AnimationMeshConfig leggings = (AnimationMeshConfig)this.userConfig.meshes.get("armor_leggings");
    AnimationMeshConfig feet = (AnimationMeshConfig)this.userConfig.meshes.get("armor_feet");
    if (helmet != null)
      updateArmorSlot(helmet, entity, 3); 
    if (chest != null)
      updateArmorSlot(chest, entity, 2); 
    if (leggings != null)
      updateArmorSlot(leggings, entity, 1); 
    if (feet != null)
      updateArmorSlot(feet, entity, 0); 
  }
  
  private void updateArmorSlot(AnimationMeshConfig config, EntityLivingBase entity, int slot) {
    ItemStack stack = entity.getCurrentArmor(slot);
    if (stack != null && stack.getItem() instanceof ItemArmor) {
      ItemArmor item = (ItemArmor)stack.getItem();
      config.visible = true;
      config.texture = getArmorResource((Entity)entity, stack, slot, (String)null);
      config.color = -1;
      if (item.hasColor(stack))
        config.color = item.getColor(stack); 
    } else {
      config.visible = false;
      config.color = -1;
    } 
  }
  
  private ResourceLocation getArmorResource(Entity entity, ItemStack stack, int slot, String type) {
    ItemArmor item = (ItemArmor)stack.getItem();
    String texture = item.getArmorMaterial().getName();
    String domain = "minecraft";
    int idx = texture.indexOf(':');
    if (idx != -1) {
      domain = texture.substring(0, idx);
      texture = texture.substring(idx + 1);
    } 
    String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", new Object[] { domain, texture, Integer.valueOf(isLegSlot(slot) ? 2 : 1), (type == null) ? "" : String.format("_%s", new Object[] { type }) });
    ResourceLocation resourcelocation = ARMOR_TEXTURE_RES_MAP.get(s1);
    if (resourcelocation == null) {
      resourcelocation = new ResourceLocation(s1);
      ARMOR_TEXTURE_RES_MAP.put(s1, resourcelocation);
    } 
    return resourcelocation;
  }
  
  private boolean isLegSlot(int slotIn) {
    return (slotIn == 1);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\api\animation\model\AnimatorEmoticonsController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */