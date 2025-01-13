package co.crystaldev.client.feature.impl.combat;

import co.crystaldev.client.Reference;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Slider;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.handler.NotificationHandler;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.renderer.MixinEntityRenderer;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.shader.MixinShaderGroup;
import co.crystaldev.client.util.Reflector;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.util.MathHelper;

import java.io.IOException;

@ModuleInfo(name = "Motion Blur", description = "Make your camera motion appear smoother", category = Category.COMBAT)
public class MotionBlur extends Module implements IRegistrable {
    @Toggle(label = "Use Old Blur")
    public boolean oldBlur = false;

    @Slider(label = "Blur Amount", placeholder = "{value}x", minimum = 1.0D, maximum = 8.0D, standard = 2.0D, integers = true)
    public int blurAmount = 2;

    private static MotionBlur INSTANCE;

    public MotionBlur() {
        INSTANCE = this;
        this.enabled = false;
    }

    public void enable() {
        if (Reflector.GameSettings$ofFastRender(this.mc.gameSettings)) {
            if (this.mc.thePlayer != null)
                NotificationHandler.addNotification("Fast Render is not compatible with Motion Blur, disabling.");
            disable();
            return;
        }
        super.enable();
    }

    public void disable() {
        if (this.mc.entityRenderer.isShaderActive())
            this.mc.entityRenderer.stopUseShader();
        super.disable();
    }

    public void loadShader() {
        if (!this.mc.entityRenderer.isShaderActive()) {
            MixinEntityRenderer entityRenderer = (MixinEntityRenderer) this.mc.entityRenderer;
            entityRenderer.callLoadShader(Reference.MOTION_BLUR_SHADER);
            if (ShaderLinkHelper.getStaticShaderLinkHelper() != null)
                try {
                    ShaderGroup group = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), Reference.MOTION_BLUR_SHADER);
                    entityRenderer.setTheShaderGroup(group);
                    group.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
                } catch (IOException ex) {
                    Reference.LOGGER.error("Error initializing motion blur", ex);
                }
        }
    }

    public static MotionBlur getInstance() {
        return INSTANCE;
    }

    public void registerEvents() {
        EventBus.register(this, ClientTickEvent.Post.class, ev -> {
            if (!Reflector.GameSettings$ofFastRender(this.mc.gameSettings)) {
                if (this.mc.theWorld != null)
                    loadShader();
                if (this.mc.entityRenderer != null && this.mc.entityRenderer.isShaderActive()) {
                    MixinEntityRenderer entityRenderer = (MixinEntityRenderer) this.mc.entityRenderer;
                    MixinShaderGroup sg = (MixinShaderGroup) entityRenderer.getShaderGroup();
                    try {
                        for (Shader s : sg.getListShaders()) {
                            ShaderUniform su = s.getShaderManager().getShaderUniform("Phosphor");
                            if (su != null) {
                                float amount;
                                if (this.oldBlur) {
                                    amount = 0.7F + this.blurAmount / 100.0F * 3.0F - 0.01F;
                                } else {
                                    amount = 1.0F - MathHelper.clamp_float(this.blurAmount / 10.0F, 0.0F, 0.99F);
                                }
                                su.set(amount, this.oldBlur ? 1.0F : 0.0F, 0.0F);
                            }
                        }
                    } catch (IllegalArgumentException ex) {
                        Reference.LOGGER.error("Error rendering menu blur", ex);
                    }
                }
            }
        });
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\combat\MotionBlur.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */