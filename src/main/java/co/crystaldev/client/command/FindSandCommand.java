package co.crystaldev.client.command;

import co.crystaldev.client.Client;
import co.crystaldev.client.command.base.AbstractCommand;
import co.crystaldev.client.command.base.CommandInfo;
import co.crystaldev.client.command.base.args.CommandArguments;
import co.crystaldev.client.command.base.exceptions.CommandException;
import co.crystaldev.client.event.EventBus;
import co.crystaldev.client.event.SubscribeEvent;
import co.crystaldev.client.event.impl.render.RenderWorldEvent;
import co.crystaldev.client.util.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

@CommandInfo(name = "findsand", usage = {"findsand [radius | clear]"}, description = "Highlights all nearby sand.", minimumArguments = 1)
public class FindSandCommand extends AbstractCommand {
  private final Color[] COLORS = new Color[] { new Color(245, 196, 46, 150), new Color(234, 123, 64, 150), new Color(141, 141, 141, 150) };
  
  private final Set<BatchedSand> sand = new HashSet<>();
  
  private final Set<BatchedSand> toRemove = new HashSet<>();
  
  public FindSandCommand() {
    EventBus.register(this);
  }
  
  public void execute(ICommandSender sender, CommandArguments arguments) throws CommandException {
    if (arguments.getString(0).equalsIgnoreCase("clear")) {
      this.toRemove.addAll(this.sand);
      Client.sendMessage("Cleared visualized sand blocks", true);
    } else {
      int radius = arguments.argumentExistsAtIndex(0) ? arguments.getInt(0, 1, 128) : 32, count = 0;
      BlockPos pos = (Minecraft.getMinecraft()).thePlayer.getPosition();
      for (int x = -radius; x <= radius; x++) {
        for (int y = -radius; y <= radius; y++) {
          for (int z = -radius; z <= radius; z++) {
            BlockPos p = pos.add(x, y, z);
            IBlockState state = (Minecraft.getMinecraft()).theWorld.getBlockState(p);
            Block block = (Minecraft.getMinecraft()).theWorld.getBlockState(p).getBlock();
            if (block instanceof net.minecraft.block.BlockFalling) {
              Color color;
              if (block instanceof net.minecraft.block.BlockSand) {
                color = (block.getMetaFromState(state) == 1) ? this.COLORS[1] : this.COLORS[0];
              } else {
                color = this.COLORS[2];
              } 
              this.sand.add(new BatchedSand(p, color));
              count++;
            } 
          } 
        } 
      } 
      Client.sendMessage(String.format("Marked %sx sand", new Object[] { Integer.valueOf(count) }), true);
    } 
  }
  
  @SubscribeEvent
  public void onRenderWorld(RenderWorldEvent.Post event) {
    GL11.glPushMatrix();
    boolean wasTex2d = GL11.glGetBoolean(3553);
    boolean wasBlend = GL11.glGetBoolean(3042);
    boolean wasDepth = GL11.glGetBoolean(2929);
    boolean wasLineSmooth = GL11.glGetBoolean(2848);
    GL11.glEnable(3042);
    GL11.glDisable(3553);
    GL11.glDisable(2929);
    GL11.glEnable(2848);
    GL11.glDepthMask(false);
    GL11.glBlendFunc(770, 771);
    GL11.glLineWidth(1.0F);
    for (BatchedSand sand : this.sand) {
      AxisAlignedBB aabb = RenderUtils.normalize(sand.aabb);
      RenderUtils.setGlColor(sand.color);
      RenderUtils.drawFilledBoundingBox(aabb);
      Block state = (Minecraft.getMinecraft()).theWorld.getBlockState(sand.pos).getBlock();
      if (!(state instanceof net.minecraft.block.BlockSand) && !(state instanceof net.minecraft.block.BlockGravel) && !(state instanceof net.minecraft.block.BlockAnvil))
        this.toRemove.add(sand); 
    } 
    if (!wasBlend)
      GL11.glDisable(3042); 
    if (wasTex2d)
      GL11.glEnable(3553); 
    if (wasDepth)
      GL11.glEnable(2929); 
    if (wasLineSmooth)
      GL11.glDisable(2848); 
    GL11.glDepthMask(true);
    GL11.glColor4d(255.0D, 255.0D, 255.0D, 255.0D);
    GL11.glPopMatrix();
    if (!this.toRemove.isEmpty())
      this.sand.removeAll(this.toRemove); 
  }
  
  private static class BatchedSand {
    private final AxisAlignedBB aabb;
    
    private final BlockPos pos;
    
    private final Color color;
    
    public BatchedSand(BlockPos pos, Color color) {
      this.aabb = RenderUtils.posToAABB(this.pos = pos);
      this.color = color;
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\command\FindSandCommand.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */