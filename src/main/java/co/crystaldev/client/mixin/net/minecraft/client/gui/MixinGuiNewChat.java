package co.crystaldev.client.mixin.net.minecraft.client.gui;

import co.crystaldev.client.feature.impl.all.ChatSettings;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin({GuiNewChat.class})
public abstract class MixinGuiNewChat extends Gui {
    @Shadow
    private boolean isScrolled;

    private float percentComplete;

    private int newLines;

    @ModifyConstant(method = {"setChatLine"}, constant = {@Constant(intValue = 100)})
    private int expandChatHistory(int original) {
        return 65535;
    }

    @Redirect(method = {"*"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"))
    public void removeBackground(int left, int top, int right, int bottom, int color) {
        if (!(ChatSettings.getInstance()).enabled || !(ChatSettings.getInstance()).clear)
            Gui.drawRect(left, top, right, bottom, color);
    }

    private long prevMillis = System.currentTimeMillis();

    private float animationPercent;

    private int lineBeingDrawn;

    @Shadow
    public abstract float getChatScale();

    private void updatePercentage(long diff) {
        if (this.percentComplete < 1.0F)
            this.percentComplete += 0.004F * (float) diff;
        this.percentComplete = MathHelper.clamp_float(this.percentComplete, 0.0F, 1.0F);
    }

    @Inject(method = {"drawChat"}, at = {@At("HEAD")})
    private void modifyChatRendering(CallbackInfo ci) {
        long current = System.currentTimeMillis();
        long diff = current - this.prevMillis;
        this.prevMillis = current;
        updatePercentage(diff);
        float t = this.percentComplete;
        this.animationPercent = MathHelper.clamp_float(1.0F - --t * t * t * t, 0.0F, 1.0F);
    }

    @Inject(method = {"drawChat"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;pushMatrix()V", ordinal = 0, shift = At.Shift.AFTER)})
    private void translate(CallbackInfo ci) {
        if ((ChatSettings.getInstance()).enabled && (ChatSettings.getInstance()).smoothChat && !this.isScrolled)
            GlStateManager.translate(0.0F, (9.0F - 9.0F * this.animationPercent) * getChatScale(), 0.0F);
    }

    @ModifyArg(method = {"drawChat"}, index = 0, at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;", ordinal = 0, remap = false))
    private int getLineBeingDrawn(int line) {
        this.lineBeingDrawn = line;
        return line;
    }

    @ModifyArg(method = {"drawChat"}, index = 3, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private int modifyTextOpacity(int original) {
        if ((ChatSettings.getInstance()).enabled && (ChatSettings.getInstance()).smoothChat && this.lineBeingDrawn <= this.newLines) {
            int opacity = original >> 24 & 0xFF;
            opacity = (int) (opacity * this.animationPercent);
            return original & 0xFFFFFF | opacity << 24;
        }
        return original;
    }

    @Inject(method = {"printChatMessageWithOptionalDeletion"}, at = {@At("HEAD")})
    private void resetPercentage(CallbackInfo ci) {
        this.percentComplete = 0.0F;
    }

    @ModifyVariable(method = {"setChatLine"}, at = @At("STORE"), ordinal = 0)
    private List<IChatComponent> setNewLines(List<IChatComponent> original) {
        this.newLines = original.size() - 1;
        return original;
    }
}

