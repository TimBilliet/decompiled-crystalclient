package co.crystaldev.client.feature.impl.mechanic;

import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.IRegistrable;
import co.crystaldev.client.event.impl.network.WorldEditCuiEvent;
import co.crystaldev.client.event.impl.render.RenderWorldEvent;
import co.crystaldev.client.event.impl.tick.ClientTickEvent;
import co.crystaldev.client.event.impl.world.WorldEvent;
import co.crystaldev.client.feature.annotations.properties.Colour;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.annotations.properties.Slider;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.Module;
import co.crystaldev.client.shader.ShaderManager;
import co.crystaldev.client.shader.chroma.ChromaScreenShader;
import co.crystaldev.client.util.BoundingBox;
import co.crystaldev.client.util.ColorObject;
import co.crystaldev.client.util.RenderUtils;
import co.crystaldev.client.util.objects.wecui.Selection;
import io.netty.buffer.Unpooled;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.nio.charset.StandardCharsets;

@ModuleInfo(name = "WorldEdit CUI", description = "Visually displays WorldEdit selections", category = Category.MECHANIC)
public class WorldEditCUI extends Module implements IRegistrable {
  @Slider(label = "Line Width", placeholder = "{value}px", minimum = 1.0D, maximum = 10.0D, standard = 3.0D, integers = true)
  public int lineWidth = 3;

  @Colour(label = "Pos1 Color")
  public ColorObject pos1Colour = new ColorObject(209, 37, 40, 200);

  @Colour(label = "Pos2 Color")
  public ColorObject pos2Colour = new ColorObject(71, 37, 209, 200);

  @Colour(label = "Selection Color")
  public ColorObject selectionColour = new ColorObject(255, 255, 255, 160);

  private static WorldEditCUI INSTANCE;

  private Selection selection;

  private long delayedHandshakeTicks = -1L;

  public WorldEditCUI() {
    INSTANCE = this;
    this.enabled = false;
  }

  private void onRenderWorld(RenderWorldEvent.Post event) {
    if (this.selection != null)
      if (this.selection.getType() == Selection.Type.CUBOID) {
        if (this.selection.getPoints().size() == 2) {
          BlockPos pos1 = (BlockPos) this.selection.getPoints().get(0);
          BlockPos pos2 = (BlockPos) this.selection.getPoints().get(1);
          AxisAlignedBB bb = RenderUtils.normalize((AxisAlignedBB) (new BoundingBox((Vec3i) pos1, (Vec3i) pos2)).addMax(1.0D, 1.0D, 1.0D));
          GL11.glPushMatrix();
          GL11.glEnable(3042);
          GL11.glDisable(3553);
          GL11.glDisable(2929);
          GL11.glEnable(2848);
          GL11.glDepthMask(false);
          GL11.glBlendFunc(770, 771);
          GL11.glLineWidth(this.lineWidth);
          RenderUtils.setGlColor((Color) this.selectionColour);
          if (this.selectionColour.isChroma())
            ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
          RenderGlobal.drawSelectionBoundingBox(bb);
          ShaderManager.getInstance().disableShader();
          GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
          GL11.glDisable(3042);
          GL11.glEnable(3553);
          GL11.glEnable(2929);
          GL11.glDisable(2848);
          GL11.glDepthMask(true);
          GL11.glLineWidth(1.0F);
          GL11.glPopMatrix();
        }
        if (this.selection.getPoints().containsKey(Integer.valueOf(0))) {
          BlockPos pos1 = (BlockPos) this.selection.getPoints().get(Integer.valueOf(0));
          GL11.glPushMatrix();
          GL11.glEnable(3042);
          GL11.glDisable(3553);
          GL11.glDisable(2929);
          GL11.glDepthMask(false);
          GL11.glBlendFunc(770, 771);
          RenderUtils.setGlColor((Color) this.pos1Colour);
          if (this.pos1Colour.isChroma())
            ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
          RenderUtils.drawFilledBoundingBox(RenderUtils.normalize((AxisAlignedBB) new BoundingBox((Vec3i) pos1, (Vec3i) pos1.add(1, 1, 1))));
          ShaderManager.getInstance().disableShader();
          GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
          GL11.glDisable(3042);
          GL11.glEnable(3553);
          GL11.glEnable(2929);
          GL11.glDepthMask(true);
          GL11.glPopMatrix();
        }
        if (this.selection.getPoints().containsKey(Integer.valueOf(1))) {
          BlockPos pos2 = (BlockPos) this.selection.getPoints().get(Integer.valueOf(1));
          GL11.glPushMatrix();
          GL11.glEnable(3042);
          GL11.glDisable(3553);
          GL11.glDisable(2929);
          GL11.glDepthMask(false);
          GL11.glBlendFunc(770, 771);
          RenderUtils.setGlColor((Color) this.pos2Colour);
          if (this.pos2Colour.isChroma())
            ShaderManager.getInstance().enableShader(ChromaScreenShader.class);
          RenderUtils.drawFilledBoundingBox(RenderUtils.normalize((AxisAlignedBB) new BoundingBox((Vec3i) pos2, (Vec3i) pos2.add(1, 1, 1))));
          ShaderManager.getInstance().disableShader();
          GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
          GL11.glDisable(3042);
          GL11.glEnable(3553);
          GL11.glEnable(2929);
          GL11.glDepthMask(true);
          GL11.glPopMatrix();
        }
      }
  }

  public void enable() {
    super.enable();
    if (this.mc.getNetHandler() != null) {
      this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("WECUI", new PacketBuffer(Unpooled.wrappedBuffer("v|3".getBytes(StandardCharsets.UTF_8)))));
      this.delayedHandshakeTicks = 10L;
    }
  }

  public static WorldEditCUI getInstance() {
    return INSTANCE;
  }

  public void registerEvents() {
    EventBus.register(this, ClientTickEvent.Post.class, ev -> {
          if (this.delayedHandshakeTicks > 0L && --this.delayedHandshakeTicks == 0L) {
            this.delayedHandshakeTicks = -1L;
            if (this.mc.thePlayer != null)
              this.mc.thePlayer.sendChatMessage("/we cui");
          }
        });
    EventBus.register(this, WorldEvent.Load.class, ev -> {
          ev.netHandler.addToSendQueue(new C17PacketCustomPayload("WECUI", new PacketBuffer(Unpooled.wrappedBuffer("v|3".getBytes(StandardCharsets.UTF_8)))));
          this.delayedHandshakeTicks = 10L;
        });
    EventBus.register(this, WorldEditCuiEvent.class, ev -> {
          switch (ev.type) {
            case "s":
              this.selection = new Selection(ev.args[0]);
              break;
            case "p":
              if (this.selection != null)
                this.selection.addPoint(Integer.parseInt(ev.args[0]), Integer.parseInt(ev.args[1]), Integer.parseInt(ev.args[2]), Integer.parseInt(ev.args[3]), Integer.parseInt(ev.args[4]));
              break;
          }
        });
    EventBus.register(this, RenderWorldEvent.Post.class, this::onRenderWorld);
  }
}