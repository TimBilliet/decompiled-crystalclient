package co.crystaldev.client.gui.buttons;

import co.crystaldev.client.Resources;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.font.FontRenderer;
import co.crystaldev.client.font.Fonts;
import co.crystaldev.client.gui.Button;
import co.crystaldev.client.gui.Screen;
//import co.crystaldev.client.gui.screens.ScreenCrosshairSettings;
//import co.crystaldev.client.gui.screens.ScreenInfoHudSettings;
import co.crystaldev.client.gui.screens.ScreenCrosshairSettings;
import co.crystaldev.client.gui.screens.ScreenInfoHudSettings;
import co.crystaldev.client.gui.screens.ScreenModules;
import co.crystaldev.client.gui.screens.ScreenSettings;
//import co.crystaldev.client.handler.NotificationHandler;
import co.crystaldev.client.handler.NotificationHandler;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.FadingColor;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class ModuleButton extends Button {
  private final Module module;

  private final ResourceLocation icon;

  private final ResourceButton settingsButton;

  private final ResourceButton hoistButton;

  private final FadingColor backgroundColor;

  private final FadingColor textColor;

  private final FadingColor statusColor1;

  private final FadingColor statusColor2;

  private final FadingColor statusFD1;

  private final FadingColor statusFD2;

  public ModuleButton(Module module, int x, int y, int width, int height) {
    super(-1, x, y, width, height, module.name);
    this.module = module;
    this.icon = module.icon;
    this.backgroundColor = new FadingColor(this.opts.hoveredButtonBackground, 0, 150);
    this.textColor = new FadingColor(this.opts.neutralTextColor, this.opts.hoveredTextColor);
    this.statusColor1 = new FadingColor(this.opts.mainDisabled, this.opts.getColor(this.opts.mainEnabled, 255));
    this.statusColor2 = new FadingColor(this.opts.secondaryDisabled, this.opts.getColor(this.opts.secondaryEnabled, 255));
    this.statusFD1 = new FadingColor(this.opts.unselectedTextColor.darker(), this.opts.getColor(this.opts.neutralTextColor.darker(), 255));
    this.statusFD2 = new FadingColor(this.opts.unselectedTextColor, this.opts.getColor(this.opts.neutralTextColor, 255));
    int buttonSize = (int)(this.height * 0.3D);
    this.settingsButton = new ResourceSubButton(-1, this.x + this.width - 8 - buttonSize, this.y + this.height - 8 - buttonSize, buttonSize, buttonSize, Resources.COG);
    this.settingsButton.setDrawBackground(false);
    this.hoistButton = new HoistSubButton(-1, this.x + this.width - 12 - buttonSize * 2, this.y + this.height - 8 - buttonSize, buttonSize, buttonSize, Resources.STAR);
    this.hoistButton.setDrawBackground(false);
  }

  public void onUpdate() {
    this.settingsButton.x = this.x + this.width - 8 - this.settingsButton.width;
    this.settingsButton.y = this.y + this.height - 8 - this.settingsButton.height;
    this.hoistButton.x = this.x + this.width - 12 - this.hoistButton.width * 2;
    this.hoistButton.y = this.y + this.height - 8 - this.hoistButton.height;
  }

  public void drawButton(int mouseX, int mouseY, boolean hovered) {
    FadingColor status1, status2;
    Screen.scissorStart(this.scissorPane);
    this.backgroundColor.fade(hovered);
    this.textColor.fade(hovered);
    if (!this.module.forceDisabled) {
      (status1 = this.statusColor1).fade(this.module.enabled);
      (status2 = this.statusColor2).fade(this.module.enabled);
    } else {
      (status1 = this.statusFD1).fade(true);
      (status2 = this.statusFD2).fade(true);
    }
    RenderUtils.drawRoundedRectWithGradientBorder(this.x, this.y, (this.x + this.width), (this.y + this.height), 14.0D, 4.5F, status1
        .getCurrentColor().getRGB(), status2.getCurrentColor().getRGB(), this.backgroundColor
        .getCurrentColor().getRGB());
    Color textColor = this.textColor.getCurrentColor();
    if (this.icon != null) {
      int w = (int)(this.height * 0.6D);
      RenderUtils.setGlColor(textColor);
      RenderUtils.drawCustomSizedResource(this.icon, this.x + 6, this.y + this.height / 2 - w / 2, w, w);
    }
    FontRenderer fr = Fonts.NUNITO_SEMI_BOLD_20;
    if (fr.getStringWidth(this.module.name) >= this.width * 0.6D)
      fr = Fonts.NUNITO_SEMI_BOLD_18;
    if (fr.getStringWidth(this.module.name) >= this.width * 0.6D)
      fr = Fonts.NUNITO_SEMI_BOLD_16;
    fr.drawString(this.module.name, this.x + this.width - 8 - fr.getStringWidth(this.module.name), this.y + 5, textColor
        .getRGB());
    this.settingsButton.drawButton(mouseX, mouseY, (this.module.isConfigurable() && this.settingsButton.isHovered(mouseX, mouseY)));
    this.hoistButton.drawButton(mouseX, mouseY, this.hoistButton.isHovered(mouseX, mouseY));
    Screen.scissorEnd(this.scissorPane);
  }

  public void onInteract(int mouseX, int mouseY, int mouseButton) {
    if (mouseButton == 1 || this.settingsButton.isHovered(mouseX, mouseY)) {
      if (!this.module.isConfigurable()) {
        NotificationHandler.addNotification(this.module.name + " does not have any configurable options");
        return;
      }
      if (this.module instanceof co.crystaldev.client.feature.impl.hud.InfoHud) {
        this.mc.displayGuiScreen((GuiScreen)new ScreenInfoHudSettings(this.mc.currentScreen));
      } else if (this.module instanceof co.crystaldev.client.feature.impl.combat.CrosshairSettings) {
        this.mc.displayGuiScreen((GuiScreen)new ScreenCrosshairSettings(this.mc.currentScreen));
      } else {
        this.mc.displayGuiScreen((GuiScreen)new ScreenSettings(this.module, this.mc.currentScreen));
      }
    } else if (this.hoistButton.isHovered(mouseX, mouseY)) {
      this.module.hoisted = !this.module.hoisted;
      ((ScreenModules)this.mc.currentScreen).initModules();
    } else {
      this.module.toggle();
    }
  }

  public class HoistSubButton extends ResourceButton {
    private final FadingColor fc;

    public HoistSubButton(int id, int x, int y, int width, int height, ResourceLocation resourceLocation) {
      super(id, x, y, width, height, resourceLocation);
      this
        .fc = new FadingColor(new Color(253, 192, 28, this.fadingColor.getColor1().getAlpha()), new Color(255, 233, 0, this.fadingColor.getColor2().getAlpha()));
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
      FadingColor fc = ModuleButton.this.module.hoisted ? this.fc : this.iconColor;
      fc.fade(hovered);
      RenderUtils.glColor(fc.getCurrentColor().getRGB());
      RenderUtils.drawCustomSizedResource(this.resourceLocation, this.x, this.y, this.width + 2, this.height + 2);
    }
  }

  private static class ResourceSubButton extends ResourceButton {
    public ResourceSubButton(int id, int x, int y, int width, int height, ResourceLocation resourceLocation) {
      super(id, x, y, width, height, resourceLocation);
    }

    public void drawButton(int mouseX, int mouseY, boolean hovered) {
      this.fadingColor.fade(hovered);
      this.iconColor.fade(hovered);
      RenderUtils.glColor(this.iconColor.getCurrentColor().getRGB());
      RenderUtils.drawCustomSizedResource(this.resourceLocation, this.x, this.y, this.width, this.height);
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\buttons\ModuleButton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */