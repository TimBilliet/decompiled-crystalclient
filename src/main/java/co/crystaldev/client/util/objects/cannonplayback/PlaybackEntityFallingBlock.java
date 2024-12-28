package co.crystaldev.client.util.objects.cannonplayback;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.world.World;

public class PlaybackEntityFallingBlock extends EntityFallingBlock {
  public PlaybackEntityFallingBlock(World world, double x, double y, double z, IBlockState state) {
    super(world, x, y, z, state);
  }
  
  public void moveEntity(double x, double y, double z) {}
  
  public void onUpdate() {}
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\cannonplayback\PlaybackEntityFallingBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */