package co.crystaldev.client.feature.impl.combat;

import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.render.RenderOverlayEvent;
import co.crystaldev.client.feature.annotations.properties.*;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Dropdown;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.group.GroupManager;
import co.crystaldev.client.gui.GuiOptions;
import co.crystaldev.client.shader.ShaderManager;
import co.crystaldev.client.shader.chroma.ChromaScreenShader;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.RenderUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleInfo(name = "Crosshair Settings", description = "Various settings to control how your crosshair behaves", category = Category.COMBAT)
public class CrosshairSettings extends Module implements IRegistrable {
  private final int TRANSPARENT = (new ColorObject(0, 0, 0, 0)).getRGB();
  
  @Toggle(label = "Use Custom Crosshair")
  public boolean useCustomCrosshair = true;
  
  @PageBreak(label = "Design")
  @Toggle(label = "Dot")
  public boolean dot = false;
  
  @Toggle(label = "Dynamic Color")
  public boolean dynamicColor = true;
  
  @DropdownMenu(label = "Style", values = {"Circle", "Cross", "Arrow"}, defaultValues = {"Cross"})
  public Dropdown<String> style;
  
  @Slider(label = "Dot Size", minimum = 0.5D, maximum = 2.5D, standard = 1.0D)
  public double dotSize = 1.0D;
  
  @Slider(label = "Length", minimum = 1.0D, maximum = 10.0D, standard = 4.0D, integers = true)
  public int length = 4;
  
  @Slider(label = "Thickness", minimum = 1.0D, maximum = 4.0D, standard = 1.0D, integers = true)
  public int thickness = 1;
  
  @Slider(label = "Gap", minimum = 0.0D, maximum = 5.0D, standard = 2.0D, integers = true)
  public int gap = 2;
  
  @Colour(label = "Color")
  public ColorObject color = new ColorObject(255, 255, 255, 185);
  
  @Colour(label = "Friendly Color")
  public ColorObject friendlyColor = ColorObject.fromColor((GuiOptions.getInstance()).secondaryColor).setAlpha(185);
  
  @Colour(label = "Enemy Color")
  public ColorObject enemyColor = ColorObject.fromColor((GuiOptions.getInstance()).secondaryRed).setAlpha(185);
  
  private static CrosshairSettings INSTANCE;
  
  public CrosshairSettings() {
    INSTANCE = this;
    this.enabled = false;
  }
  
  public void renderCrosshair(int x, int y) {
    GL11.glPushMatrix();
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glEnable(2848);
    GL11.glBlendFunc(770, 771);
    ColorObject color = null;
    if (this.dynamicColor) {
      Entity entity = this.mc.pointedEntity;
      if (entity != null)
        if (entity instanceof net.minecraft.entity.monster.EntityMob) {
          color = this.enemyColor;
        } else if (entity instanceof net.minecraft.entity.passive.EntityAnimal) {
          color = this.friendlyColor;
        } else if (entity instanceof EntityPlayer) {
          EntityPlayer ep = (EntityPlayer)entity;
          if (GroupManager.getSelectedGroup() != null && GroupManager.getSelectedGroup().getMember(ep.getUniqueID()) != null) {
            color = this.friendlyColor;
          } else if (!ep.isInvisibleToPlayer((EntityPlayer)this.mc.thePlayer)) {
            color = this.enemyColor;
          } 
        }  
    } 
    if (color == null)
      color = this.color; 
    RenderUtils.setGlColor((Color)color);
    if (color.isChroma())
      ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
    x += 7;
    y += 7;
    if (this.dot)
      RenderUtils.drawCircle(x, y, this.dotSize, color.getRGB());
    RenderUtils.drawCircle(200, 200, 10,color.getRGB());
    String currentSelection = (String) this.style.getCurrentSelection();
    if ("Circle".equals(currentSelection)) {
      RenderUtils.drawTorus(x, y, this.gap, this.gap + this.thickness, color.getRGB());
    } else if ("Arrow".equals(currentSelection)) {
      y++;
      RenderUtils.drawLines(new float[]{x - this.length, y + this.length, x, y, x, y, x + this.length, y + this.length}, this.thickness * 2.0F, color

              .getRGB());
    } else {
      RenderUtils.drawFastRect(x + this.thickness / 2.0D, (y - this.gap), x - this.thickness / 2.0D, (y - this.gap - this.length));
      RenderUtils.drawFastRect(x + this.thickness / 2.0D, (y + this.gap), x - this.thickness / 2.0D, (y + this.gap + this.length));
      RenderUtils.drawFastRect((x - this.gap), y + this.thickness / 2.0D, (x - this.gap - this.length), y - this.thickness / 2.0D);
      RenderUtils.drawFastRect((x + this.gap), y + this.thickness / 2.0D, (x + this.gap + this.length), y - this.thickness / 2.0D);
    }
    ShaderManager.getInstance().disableShader();
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glDisable(3042);
    GL11.glEnable(3553);
    GL11.glDisable(2848);
    GL11.glPopMatrix();
  }
  
  public static CrosshairSettings getInstance() {
    return INSTANCE;
  }
  
  public void registerEvents() {
    EventBus.register(this, RenderOverlayEvent.Crosshair.class, ev -> {
      if (this.useCustomCrosshair && ev.isVisible()) {
        ev.setCancelled(true);
        ScaledResolution res = new ScaledResolution(this.mc);
      }
    });
  }
}
