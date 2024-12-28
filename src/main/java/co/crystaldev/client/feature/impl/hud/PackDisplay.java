package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.render.GuiScreenEvent;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModuleBackground;
import co.crystaldev.client.mixin.accessor.net.minecraft.client.MixinMinecraft;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.enums.AnchorRegion;
import co.crystaldev.client.util.enums.ChatColor;
import co.crystaldev.client.util.objects.ModulePosition;
import co.crystaldev.client.util.type.Tuple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.ResourceLocation;

import java.util.List;

@ModuleInfo(name = "Pack Display", description = "Display the icon and name of the selected resource pack onscreen", category = Category.HUD)
public class PackDisplay extends HudModuleBackground implements IRegistrable {
  @Toggle(label = "Display Pack Icon")
  public boolean displayIcon = true;
  
  private static final DefaultResourcePack DEFAULT_RESOURCE_PACK = ((MixinMinecraft)Minecraft.getMinecraft()).getMcDefaultResourcePack();
  
  private String packName = null;
  
  private ResourceLocation packIcon = null;
  
  public PackDisplay() {
    this.enabled = false;
    this.hasInfoHud = true;
    this.width = 110;
    this.height = 18;
    this.position = new ModulePosition(AnchorRegion.TOP_RIGHT, 140.0F, 28.0F);
  }
  
  public void configPostInit() {
    super.configPostInit();
    loadPack();
  }
  
  public void enable() {
    super.enable();
    loadPack();
  }
  
  public String getDisplayText() {
    return null;
  }
  
  public Tuple<String, String> getInfoHud() {
    return new Tuple("Resource Pack", ChatColor.stripColor(this.packName));
  }
  
  public void draw() {
    if (this.mc.theWorld == null)
      return; 
    if (!this.drawBackground) {
      RenderUtils.drawCenteredString("[" + this.packName + "&r]", getRenderX() + this.width / 2, getRenderY() + this.height / 2, this.textColor);
      return;
    } 
    int x = getRenderX();
    int y = getRenderY();
    int iconSize = this.displayIcon ? this.height : 0;
    drawBackground(x, y, x + this.width, y + this.height);
    RenderUtils.drawCenteredString(this.packName, x + (this.width - iconSize) / 2 + iconSize, y + this.height / 2, this.textColor);
    if (this.displayIcon) {
      GlStateManager.enableBlend();
      GlStateManager.resetColor();
      this.mc.getTextureManager().bindTexture(this.packIcon);
      Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, iconSize, iconSize, iconSize, iconSize);
      GlStateManager.disableBlend();
    } 
  }
  
  private void loadPack() {
    DynamicTexture texture;
    IResourcePack pack = getCurrentPack();
    try {
      texture = (pack == null || pack.getPackImage() == null) ? TextureUtil.missingTexture : new DynamicTexture(getCurrentPack().getPackImage());
    } catch (Exception ex) {
      texture = TextureUtil.missingTexture;
    } 
    this.packIcon = this.mc.getTextureManager().getDynamicTextureLocation("texturepackicon", texture);
    this.packName = ChatColor.translate((pack == null || pack == DEFAULT_RESOURCE_PACK) ? "Default" : pack.getPackName());
    this.width = this.height + 16 + this.mc.fontRendererObj.getStringWidth(this.packName);
  }
  
  private IResourcePack getCurrentPack() {
    List<ResourcePackRepository.Entry> repo = this.mc.getResourcePackRepository().getRepositoryEntries();
    return !repo.isEmpty() ? ((ResourcePackRepository.Entry)repo.get(0)).getResourcePack() : (IResourcePack)DEFAULT_RESOURCE_PACK;
  }
  
  public void registerEvents() {
    EventBus.register(this, GuiScreenEvent.Pre.class, ev -> {
          if (this.mc.currentScreen instanceof net.minecraft.client.gui.GuiScreenResourcePacks)
            loadPack(); 
        });
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\hud\PackDisplay.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */