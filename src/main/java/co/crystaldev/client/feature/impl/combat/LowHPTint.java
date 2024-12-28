package co.crystaldev.client.feature.impl.combat;

import co.crystaldev.client.Resources;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.render.RenderOverlayEvent;
import co.crystaldev.client.feature.annotations.properties.Colour;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Slider;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.shader.ShaderManager;
import co.crystaldev.client.shader.chroma.ChromaScreenTexturedShader;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.RenderUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleInfo(name = "Low HP Tint", description = "Displays a tinted vignette onscreen while low on health", category = Category.COMBAT)
public class LowHPTint extends Module implements IRegistrable {
  @Slider(label = "Health Threshold", placeholder = "{value} hearts", minimum = 1.0D, maximum = 10.0D, standard = 4.0D, integers = true)
  public int threshold = 4;
  
  @Colour(label = "Color")
  public ColorObject color = new ColorObject(255, 0, 0, 255);
  
  public LowHPTint() {
    this.enabled = false;
  }
  
  public void registerEvents() {
    EventBus.register(this, RenderOverlayEvent.All.class, (byte)0, ev -> {
          if (this.mc.thePlayer.getHealth() <= (this.threshold * 2) && !this.mc.thePlayer.capabilities.disableDamage) {
            ScaledResolution sr = new ScaledResolution(this.mc);
            boolean wasBlend = GL11.glGetBoolean(3042);
            boolean wasDepthTest = GL11.glGetBoolean(2929);
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GlStateManager.tryBlendFuncSeparate(1, 769, 1, 0);
            RenderUtils.setGlColor((Color)this.color);
            if (this.color.isChroma())
              ShaderManager.getInstance().enableShader(ChromaScreenTexturedShader.class); 
            RenderUtils.drawCustomSizedResource(Resources.VIGNETTE_TEXTURE, 0, 0, sr.getScaledWidth(), sr.getScaledHeight());
            ShaderManager.getInstance().disableShader();
            RenderUtils.resetColor();
            GL11.glDepthMask(true);
            if (wasDepthTest)
              GL11.glEnable(2929); 
            if (!wasBlend)
              GL11.glDisable(3042); 
            GL11.glPopMatrix();
          } 
        });
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\combat\LowHPTint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */