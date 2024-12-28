package co.crystaldev.client.feature.impl.factions;

import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.render.RenderWorldEvent;
import co.crystaldev.client.feature.annotations.properties.Colour;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Slider;
import co.crystaldev.client.feature.annotations.properties.Toggle;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.shader.ShaderManager;
import co.crystaldev.client.shader.chroma.ChromaScreenShader;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.Vec3d;
import co.crystaldev.client.util.objects.crumbs.Breadcrumb;
import co.crystaldev.client.util.type.GlueList;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashSet;

@ModuleInfo(name = "Breadcrumbs", description = "Shows certain entity trails", category = Category.FACTIONS)
public class Breadcrumbs extends Module implements IRegistrable {
  @Toggle(label = "Show TNT")
  public boolean showTnt = true;

  @Toggle(label = "Show Sand")
  public boolean showSand = true;

  @Slider(label = "Timeout", placeholder = "{value}s", minimum = 1.0D, maximum = 30.0D, standard = 10.0D, integers = true)
  public int timeout = 10;

  @Slider(label = "Line Width", placeholder = "{value}px", minimum = 1.0D, maximum = 10.0D, standard = 3.0D, integers = true)
  public int lineWidth = 3;

  @Colour(label = "TNT Color")
  public ColorObject tntColour = new ColorObject(255, 85, 85, 125);

  @Colour(label = "Sand Color")
  public ColorObject sandColour = new ColorObject(255, 255, 85, 125);

  private static Breadcrumbs INSTANCE;

  private final GlueList<Breadcrumb> crumbList = new GlueList();

  private final HashSet<Breadcrumb> removeList = new HashSet<>();

  public Breadcrumbs() {
    INSTANCE = this;
    this.enabled = false;
  }

  public void configPostInit() {
    super.configPostInit();
    setOptionVisibility("TNT Color", f -> this.showTnt);
    setOptionVisibility("Sand Color", f -> this.showSand);
  }

  private void onRenderWorld(RenderWorldEvent.Post event) {
    for (Breadcrumb crumb : this.crumbList) {
      if (crumb.expired())
        this.removeList.add(crumb);
    }
    this.crumbList.removeAll(this.removeList);
    this.removeList.clear();
    if (!this.crumbList.isEmpty())
      drawCrumbs();
    for (Entity entity : this.mc.theWorld.loadedEntityList) {
      Breadcrumb.Type type = null;
      if (entity instanceof net.minecraft.entity.item.EntityTNTPrimed) {
        type = Breadcrumb.Type.TNT;
      } else if (entity instanceof net.minecraft.entity.item.EntityFallingBlock) {
        type = Breadcrumb.Type.SAND;
      }
      if (type != null) {
        int id = entity.getEntityId();
        int index = 0;
        boolean created = false;
        for (Breadcrumb crumb : this.crumbList) {
          if (crumb.id == id) {
            created = true;
            index = this.crumbList.indexOf(crumb);
            break;
          }
        }
        if (created) {
          ((Breadcrumb)this.crumbList.get(index)).addLocation(entity.posX, entity.posY, entity.posZ);
          continue;
        }
        this.crumbList.add(new Breadcrumb(id, entity.posX, entity.posY, entity.posZ, type));
      }
    }
  }

  private void drawCrumbs() {
    for (Breadcrumb crumb : this.crumbList) {
      GL11.glPushMatrix();
      GL11.glEnable(3042);
      GL11.glEnable(2848);
      GL11.glDisable(2929);
      GL11.glDisable(3553);
      GL11.glBlendFunc(770, 771);
      GL11.glLineWidth(this.lineWidth);
      if (crumb.type == Breadcrumb.Type.TNT && this.showTnt) {
        if (this.tntColour.isChroma())
          ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
        RenderUtils.setGlColor((Color)this.tntColour);
        GL11.glBegin(3);
        for (Vec3d location : crumb.locations) {
          location = RenderUtils.normalize(location);
          GL11.glVertex3d(location.x, location.y, location.z);
        }
        GL11.glEnd();
      } else if (crumb.type == Breadcrumb.Type.SAND && this.showSand) {
        if (this.sandColour.isChroma())
          ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
        RenderUtils.setGlColor((Color)this.sandColour);
        GL11.glBegin(3);
        for (Vec3d location : crumb.locations) {
          location = RenderUtils.normalize(location);
          GL11.glVertex3d(location.x, location.y, location.z);
        }
        GL11.glEnd();
      }
      ShaderManager.getInstance().disableShader();
      GL11.glDisable(3042);
      GL11.glDisable(2848);
      GL11.glEnable(2929);
      GL11.glEnable(3553);
      GL11.glPopMatrix();
    }
    GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
  }

  public static Breadcrumbs getInstance() {
    return INSTANCE;
  }

  public void registerEvents() {
    EventBus.register(this, RenderWorldEvent.Post.class, this::onRenderWorld);
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\factions\Breadcrumbs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */