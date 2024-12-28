package co.crystaldev.client.mixin.net.minecraft.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wdl.WDLHooks;

@Mixin({GuiIngameMenu.class})
public abstract class MixinGuiIngameMenu extends GuiScreen {
  @Inject(method = {"initGui"}, at = {@At("TAIL")})
  private void initGui(CallbackInfo ci) {
    int i = -16;
    GuiButton button = null;
    for (GuiButton b : this.buttonList) {
      if (b.id == 7) {
        button = b;
        break;
      }
    }
    if (button == null)
      return;
    this.buttonList.remove(button);
    this.buttonList.add(new GuiButton(-9999, this.width / 2 + 2, this.height / 4 + 96 + i, 98, 20, I18n.format("menu.multiplayer")));
    WDLHooks.injectWDLButtons((GuiIngameMenu)(Object)this, this.buttonList);
  }
  
  @Inject(method = {"actionPerformed"}, at = {@At("TAIL")})
  private void actionPerformed(GuiButton button, CallbackInfo ci) {
    WDLHooks.handleWDLButtonClick((GuiIngameMenu)(Object)this, button);
    if (button.id == -9999)
      this.mc.displayGuiScreen(new GuiMultiplayer(this));
  }
}
