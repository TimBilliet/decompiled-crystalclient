package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.Resources;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.render.RenderOverlayEvent;
import co.crystaldev.client.feature.annotations.properties.Colour;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModule;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.AnchorRegion;
import co.crystaldev.client.util.objects.ModulePosition;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.boss.BossStatus;

import java.awt.*;

@ModuleInfo(name = "Boss Bar", description = "Modify the default Minecraft boss bar", category = Category.HUD)
public class BossBar extends HudModule implements IRegistrable {
  @Toggle(label = "Use Resource Pack Texture")
  public boolean resourcePackTexture = false;
  
  @Toggle(label = "Text Shadow")
  public boolean textShadow = true;
  
  @Colour(label = "Text Color", isTextRender = true)
  public ColorObject textColor = new ColorObject(255, 255, 255, 255);
  
  @Colour(label = "Bar Color")
  public ColorObject barColor = new ColorObject(255, 0, 255, 255);
  
  private final FontRenderer fr;
  
  private final GuiIngame guiIngame;
  
  public BossBar() {
    this.enabled = true;
    this.position = new ModulePosition(AnchorRegion.TOP_CENTER, 0.0F, 40.0F);
    this.fr = this.mc.fontRendererObj;
    this.guiIngame = this.mc.ingameGUI;
    this.width = 182;
    this.height = 17;
  }
  
  public void draw() {
    if (BossStatus.bossName != null && BossStatus.statusBarTime > 0)
      renderBossHealth(BossStatus.bossName, BossStatus.healthScale); 
  }
  
  public void drawDefault() {
    if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
      renderBossHealth(BossStatus.bossName, BossStatus.healthScale);
    } else {
      renderBossHealth("Crystal Client", 0.5F);
    } 
  }
  
  private void renderBossHealth(String bossName, float healthScale) {
    if (BossStatus.bossName != null && BossStatus.statusBarTime > 0)
      BossStatus.statusBarTime--; 
    int healthWidth = (int)(healthScale * (this.width + 1));
    int barX = getRenderX();
    int barY = getRenderY() + 12;
    int textY = barY - 10;
    if (!this.resourcePackTexture) {
      RenderUtils.setGlColor((Color)this.barColor);
      this.mc.getTextureManager().bindTexture(Resources.BOSS_BAR);
    } else {
      this.mc.getTextureManager().bindTexture(GuiIngame.icons);
    } 
    GlStateManager.enableBlend();
    GlStateManager.enableAlpha();
    GlStateManager.alphaFunc(516, 0.1F);
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    this.guiIngame.drawTexturedModalRect(barX, barY, 0, 74, this.width, 5);
    this.guiIngame.drawTexturedModalRect(barX, barY, 0, 74, this.width, 5);
    if (healthWidth > 0) {
      this.guiIngame.drawTexturedModalRect(barX, barY, 0, 79, healthWidth, 5);
      RenderUtils.resetColor();
    } 
    GlStateManager.disableAlpha();
    GlStateManager.disableBlend();
    RenderUtils.drawString(bossName, getRenderX() + this.width / 2 - this.fr.getStringWidth(bossName) / 2, textY, this.textColor, this.textShadow);
    RenderUtils.resetColor();
    this.mc.getTextureManager().bindTexture(GuiIngame.icons);
  }
  
  public void registerEvents() {
    EventBus.register(this, RenderOverlayEvent.BossBar.class, ev -> ev.setCancelled(true));
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\hud\BossBar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */