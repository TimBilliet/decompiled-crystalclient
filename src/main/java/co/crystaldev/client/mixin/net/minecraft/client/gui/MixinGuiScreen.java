package co.crystaldev.client.mixin.net.minecraft.client.gui;

import co.crystaldev.client.Client;
import co.crystaldev.client.feature.settings.ClientOptions;
import co.crystaldev.client.util.CallbackClickEvent;
import co.crystaldev.client.util.objects.resources.ImageSelection;
import co.crystaldev.client.util.task.ScreenshotTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.IChatComponent;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.awt.*;
import java.awt.datatransfer.Transferable;

@Mixin({GuiScreen.class})
public abstract class MixinGuiScreen {
    @Shadow
    public int height;

    @Shadow
    public Minecraft mc;

    @Redirect(method = {"handleKeyboardInput"}, at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;getEventKeyState()Z", remap = false))
    private boolean checkCharacter() {
        return ((Keyboard.getEventKey() == 0 && Keyboard.getEventCharacter() >= ' ') || Keyboard.getEventKeyState());
    }

//  @Inject(method = {"drawHoveringText(Ljava/util/List;IILnet/minecraft/client/gui/FontRenderer;)V"}, locals = LocalCapture.CAPTURE_FAILEXCEPTION,
//          at = @At(
//                  value = "FIELD",
//                  target = "Lnet/minecraft/client/gui/GuiScreen;zLevel:F",
//                  ordinal = 0,
//                  shift = At.Shift.BEFORE))
//  private void handleScrollableTooltips(List<String> textLines, int x, int y, FontRenderer font, CallbackInfo ci, int i, int l1, int i2, int k) {
//    RenderUtils.doScrollableTooltipTransform(this.height, i2, k);
//  }

    @Inject(method = {"handleComponentClick"}, locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true, at = {@At(value = "INVOKE", target = "Lnet/minecraft/event/ClickEvent;getAction()Lnet/minecraft/event/ClickEvent$Action;", ordinal = 0, shift = At.Shift.BEFORE)})
    protected void handleComponentClick(IChatComponent component, CallbackInfoReturnable<Boolean> ci, ClickEvent clickevent) {
        if (clickevent instanceof ScreenshotTask.ScreenshotClickEvent) {
            ScreenshotTask.ScreenshotClickEvent event = (ScreenshotTask.ScreenshotClickEvent) clickevent;
            if (event.getScreenshotAction() == ScreenshotTask.ScreenshotClickEvent.Action.COPY) {
                (new Thread(() -> {
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents((Transferable) new ImageSelection(ScreenshotTask.getCurrentImage()), null);
                    Client.sendMessage("Screenshot has been copied to your clipboard", true);
                })).start();
            } else {
                (new Thread(() -> {
                    if (ScreenshotTask.getCurrentScreenshot().exists()) {
                        ScreenshotTask.getCurrentScreenshot().delete();
                        Client.sendMessage("Screenshot has been deleted", true);
                    } else {
                        Client.sendMessage("Could not delete screenshot (already deleted or moved?)", true);
                    }
                })).start();
            }
            ci.setReturnValue(Boolean.TRUE);
        } else if (clickevent instanceof CallbackClickEvent) {
            CallbackClickEvent event = (CallbackClickEvent) clickevent;
            event.getConsumer().accept(component);
            ci.setReturnValue(Boolean.TRUE);
        }
    }

    @Inject(method = {"drawWorldBackground"}, at = {@At("HEAD")}, cancellable = true)
    public void drawWorldBackground(int tint, CallbackInfo ci) {
        if (Minecraft.getMinecraft().theWorld != null && (ClientOptions.getInstance().clearMenuBackground && (mc.currentScreen instanceof GuiIngameMenu))
        || (ClientOptions.getInstance().clearInventoryBackground && mc.currentScreen instanceof GuiInventory)
        || ClientOptions.getInstance().clearContainerBackground && mc.currentScreen instanceof GuiContainer && !(mc.currentScreen instanceof GuiInventory))
            ci.cancel();
    }
}
