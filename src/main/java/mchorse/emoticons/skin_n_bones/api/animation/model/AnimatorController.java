package mchorse.emoticons.skin_n_bones.api.animation.model;

import co.crystaldev.client.util.javax.Matrix4f;
import co.crystaldev.client.util.javax.Tuple4f;
import co.crystaldev.client.util.javax.Vector4f;
import mchorse.emoticons.client.RenderLightmap;
import mchorse.emoticons.skin_n_bones.api.animation.Animation;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationManager;
import mchorse.emoticons.skin_n_bones.api.animation.AnimationMesh;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJArmature;
import mchorse.emoticons.skin_n_bones.api.bobj.BOBJBone;
import mchorse.mclib.utils.MatrixUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

public class AnimatorController {
    public static final FloatBuffer matrix = BufferUtils.createFloatBuffer(16);

    public static final float[] buffer = new float[16];

    public static final IAnimatorFactory DEFAULT_FACTORY;

    static {
        DEFAULT_FACTORY = (controller -> new Animator(controller));
    }

    public IAnimatorFactory factory = DEFAULT_FACTORY;

    public Animation animation;

    public IAnimator animator;

    public ActionPlayback emote;

    public AnimatorConfig.AnimatorConfigEntry config;

    public AnimatorConfig userConfig = new AnimatorConfig();

    public long lastModified;

    public int checkConfig;

    public String animationName;

    public NBTTagCompound userData;

    private final Minecraft mc;

    private final Vector4f result = new Vector4f();

    private final Matrix4f rotate = new Matrix4f();

    public AnimatorController(String animationName, NBTTagCompound userData) {
        refresh(animationName, userData);
        this.mc = Minecraft.getMinecraft();
    }

    public Vector4f calcPosition(EntityLivingBase entity, BOBJBone bone, float x, float y, float z, float partial) {
        float pi = 3.1415927F;
        this.result.set(x, y, z, 1.0F);
        bone.mat.transform((Tuple4f) this.result);
        this.rotate.setIdentity();
        this.rotate.rotY((180.0F - entity.renderYawOffset + 180.0F) / 180.0F * 3.1415927F);
        this.rotate.transform((Tuple4f) this.result);
        this.result.scale(0.9375F);
        float ex = (float) (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partial);
        float ey = (float) (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partial);
        float ez = (float) (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partial);
        this.result.x += ex;
        this.result.y += ey;
        this.result.z += ez;
        return this.result;
    }

    public void setEmote(ActionPlayback emote) {
        this.emote = emote;
        if (this.animator != null)
            this.animator.setEmote(emote);
    }

    public void refresh(String animationName, NBTTagCompound userData) {
        this.animation = null;
        this.animator = null;
        this.animationName = animationName;
        this.userData = userData;
    }

    public void renderOnScreen(EntityPlayer player, int x, int y, float scale, float alpha) {
        fetchAnimation();
        if (this.animation != null && this.animation.meshes.size() > 0) {
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableAlpha();
            GlStateManager.enableDepth();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            float mult = this.userConfig.scaleGui;
            GL11.glPushMatrix();
            GL11.glTranslatef(x, y, 0.0F);
            GL11.glScalef(scale * mult, -scale * mult, scale * mult);
            GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            float yaw = player.rotationYawHead;
            float prevYaw = player.prevRotationYawHead;
            float pitch = player.rotationPitch;
            float prevPitch = player.prevRotationPitch;
            player.rotationYawHead = player.prevRotationYawHead = 0.0F;
            player.rotationPitch = player.prevRotationPitch = 0.0F;
            renderAnimation((EntityLivingBase) player, this.animation.meshes.get(0), 0.0F, 0.0F);
            player.rotationYawHead = yaw;
            player.prevRotationYawHead = prevYaw;
            player.rotationPitch = pitch;
            player.prevRotationPitch = prevPitch;
            GL11.glPopMatrix();
            GlStateManager.disableDepth();
            GlStateManager.disableAlpha();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
        }
    }

    protected void renderOnScreen(EntityPlayer player, AnimationMesh mesh, int x, int y, float scale, float alpha) {
        this.animation.render(this.userConfig.meshes);
    }

    public void render(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (this.animation != null && this.animation.meshes.size() > 0) {
            GlStateManager.disableCull();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.enableRescaleNormal();
            GlStateManager.blendFunc(770, 771);
            float yaw = interpolate(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
            if (entity.isRiding()) {
                Entity vehicle = entity.ridingEntity;
                if (vehicle instanceof net.minecraft.entity.item.EntityMinecart) {
                    yaw = interpolate(vehicle.prevRotationYaw, vehicle.rotationYaw, partialTicks);
                    yaw += 90.0F;
                }
            }
            float scale = this.userConfig.scale;
            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);
            boolean captured = MatrixUtils.captureMatrix();
            GL11.glScalef(scale, scale, scale);
            if (entity.isPlayerSleeping()) {
                EntityPlayer player = (EntityPlayer) entity;
                GlStateManager.rotate(player.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
            } else {
                GL11.glRotatef(180.0F - yaw - 180.0F, 0.0F, 1.0F, 0.0F);
            }
            renderAnimation(entity, this.animation.meshes.get(0), yaw, partialTicks);
            if (captured)
                MatrixUtils.releaseMatrix();
            GL11.glPopMatrix();
            GlStateManager.enableCull();
            GlStateManager.disableRescaleNormal();
        }
    }

    private float interpolate(float prev, float yaw, float partialTicks) {
        float result;
        for (result = yaw - prev; result < -180.0F; result += 360.0F) ;
        while (result >= 180.0F)
            result -= 360.0F;
        return prev + partialTicks * result;
    }

    public void renderAnimation(EntityLivingBase entity, AnimationMesh mesh, float yaw, float partialTicks) {
        BOBJArmature original = mesh.getArmature();
        BOBJArmature armature = this.animator.useArmature(original);
        float alpha = 1.0F;
        if (entity.isInvisible())
            alpha = !entity.isInvisibleToPlayer((EntityPlayer) (Minecraft.getMinecraft()).thePlayer) ? 0.15F : 0.0F;
        setupBoneMatrices(entity, armature, yaw, partialTicks);
        for (AnimationMesh part : this.animation.meshes) {
            part.setCurrentArmature(armature);
            part.alpha = alpha;
            part.updateMesh();
        }
        GlStateManager.enableRescaleNormal();
        boolean flag = RenderLightmap.set(entity, partialTicks);
        this.animation.render(this.userConfig.meshes);
        if (flag)
            RenderLightmap.unset();
        GlStateManager.disableRescaleNormal();
        renderItems(entity, armature);
        renderHead(entity, (BOBJBone) armature.bones.get(this.userConfig.head));
    }

    public void setupBoneMatrices(EntityLivingBase entity, BOBJArmature armature, float yaw, float partialTicks) {
        for (BOBJBone bone : armature.orderedBones)
            bone.reset();
        setupBoneTransformations(entity, armature, yaw, partialTicks);
        for (BOBJBone bone : armature.orderedBones)
            armature.matrices[bone.index] = bone.compute();
    }

    protected void setupBoneTransformations(EntityLivingBase entity, BOBJArmature armature, float yaw, float partialTicks) {
        BOBJBone head = (BOBJBone) armature.bones.get(this.userConfig.head);
        if (head != null) {
            float yawHead = entity.prevRotationYawHead + (entity.rotationYawHead - entity.prevRotationYawHead) * partialTicks;
            float pitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            yawHead = (yaw - yawHead) / 180.0F * 3.1415927F;
            pitch = pitch / 180.0F * 3.1415927F;
            head.rotateX = pitch;
            head.rotateY = yawHead;
        }
        if (this.animator != null)
            this.animator.applyActions(armature, partialTicks);
    }

    protected void renderHead(EntityLivingBase entity, BOBJBone head) {
        ItemStack stack = entity.getCurrentArmor(3);
        if (stack != null && head != null) {
            Item item = stack.getItem();
            if (!(item instanceof net.minecraft.item.ItemArmor)) {
                GlStateManager.pushMatrix();
                setupMatrix(head);
                GlStateManager.translate(0.0F, 0.25F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.scale(0.625F, 0.625F, 0.625F);
                this.mc.getItemRenderer().renderItem(entity, stack, ItemCameraTransforms.TransformType.HEAD);
                GlStateManager.popMatrix();
            }
        }
    }

    protected void renderItems(EntityLivingBase entity, BOBJArmature armature) {
        if (!this.userConfig.renderHeldItems)
            return;
        float scale = this.userConfig.scaleItems;
        ItemStack mainItem = entity.getHeldItem();
        if (mainItem != null && this.userConfig.rightHands != null)
            for (AnimatorHeldItemConfig itemConfig : this.userConfig.rightHands.values())
                renderItem(entity, mainItem, armature, itemConfig, ItemCameraTransforms.TransformType.THIRD_PERSON, scale);
    }

    public void renderItem(EntityLivingBase entity, ItemStack stack, BOBJArmature armature, AnimatorHeldItemConfig itemConfig, ItemCameraTransforms.TransformType type, float scale) {
        BOBJBone bone = (BOBJBone) armature.bones.get(itemConfig.boneName);
        if (bone != null) {
            GlStateManager.pushMatrix();
            setupMatrix(bone);
            GlStateManager.translate(0.0F, -0.0625F, 0.0625F);
            GlStateManager.scale(scale * itemConfig.scaleX, scale * itemConfig.scaleY, scale * itemConfig.scaleZ);
            GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(0.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(0.0F, 0.0F, 0.0F, 1.0F);
            this.mc.getItemRenderer().renderItem(entity, stack, type);
            GlStateManager.popMatrix();
        }
    }

    public void setupMatrix(BOBJBone bone) {
        setupMatrix(bone.mat);
    }

    public void setupMatrix(Matrix4f m) {
        buffer[0] = m.m00;
        buffer[1] = m.m10;
        buffer[2] = m.m20;
        buffer[3] = m.m30;
        buffer[4] = m.m01;
        buffer[5] = m.m11;
        buffer[6] = m.m21;
        buffer[7] = m.m31;
        buffer[8] = m.m02;
        buffer[9] = m.m12;
        buffer[10] = m.m22;
        buffer[11] = m.m32;
        buffer[12] = m.m03;
        buffer[13] = m.m13;
        buffer[14] = m.m23;
        buffer[15] = m.m33;
        matrix.clear();
        matrix.put(buffer);
        matrix.flip();
        GL11.glMultMatrix(matrix);
    }

    public void update(EntityLivingBase target) {
        fetchAnimation();
        if (this.animator != null) {
            watchConfig();
            this.animator.update(target);
        }
    }

    protected void watchConfig() {
        this.checkConfig++;
        if (this.checkConfig > 10) {
            this.checkConfig = 0;
            if (this.lastModified < this.config.lastModified) {
                this.animation = null;
                fetchAnimation();
            }
        }
    }

    public void fetchAnimation() {
        if (this.animation != null)
            return;
        Animation animation = AnimationManager.INSTANCE.getAnimation(this.animationName);
        if (animation != null) {
            this.animation = animation;
            this.config = AnimationManager.INSTANCE.getConfig(animation.name);
            this.userConfig.copy(this.config.config);
            this.userConfig.fromNBT(this.userData);
            this.animator = this.factory.createAnimator(this);
            this.animator.setEmote(this.emote);
            this.lastModified = this.config.lastModified;
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\emoticons\skin_n_bones\api\animation\model\AnimatorController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */