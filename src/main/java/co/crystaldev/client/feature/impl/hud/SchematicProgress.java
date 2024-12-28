package co.crystaldev.client.feature.impl.hud;

import co.crystaldev.client.feature.annotations.ConfigurableSize;
import co.crystaldev.client.feature.annotations.properties.ModuleInfo;
import co.crystaldev.client.feature.base.Category;
import co.crystaldev.client.feature.base.HudModuleBackground;
import co.crystaldev.client.util.enums.AnchorRegion;
import co.crystaldev.client.util.objects.ModulePosition;
import co.crystaldev.client.util.type.Tuple;
import com.github.lunatrius.core.util.BlockPosHelper;
import com.github.lunatrius.core.util.MBlockPos;
import com.github.lunatrius.schematica.block.state.BlockStateHelper;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ConfigurableSize
@ModuleInfo(name = "Schematic Progress", description = "View the completion status of the current schematic onscreen", category = Category.HUD)
public class SchematicProgress extends HudModuleBackground {
  private final ExecutorService thread = Executors.newSingleThreadExecutor();

  public int missing = 0;

  public int total = 0;

  private float progress = 0.0F;

  private long lastUpdate = 0L;

  public SchematicProgress() {
    this.enabled = false;
    this.position = new ModulePosition(AnchorRegion.TOP_RIGHT, 15.0F, 150.0F);
    this.width = 110;
    this.height = 18;
    this.hasInfoHud = true;
  }

  public Tuple<String, String> getInfoHud() {
    update();
    return new Tuple("Schematic", String.format("%.1f", new Object[] { Float.valueOf(this.progress) }) + "%");
  }

  public String getDisplayText() {
    update();
    return String.format("Schematic: %.1f%c", new Object[] { Float.valueOf(this.progress), Character.valueOf('%') });
  }

  private void update() {
    long ms = System.currentTimeMillis();
    if (ClientProxy.currentSchematic.schematic != null) {
      if (ms - this.lastUpdate > 250L)
        this.thread.submit(() -> {
              this.lastUpdate = ms;
              SchematicWorld world = ClientProxy.currentSchematic.schematic;
              WorldClient worldClient = this.mc.theWorld;
              MBlockPos mcPos = new MBlockPos();
              this.total = 0;
              this.missing = 0;
              for (MBlockPos pos : BlockPosHelper.getAllInBox(BlockPos.ORIGIN, new BlockPos(world.getWidth() - 1, world.getHeight() - 1, world.getLength() - 1))) {
                IBlockState blockState = world.getBlockState((BlockPos)pos);
                Block block = blockState.getBlock();
                if (block == Blocks.air || world.isAirBlock((BlockPos)pos))
                  continue;
                this.total++;
                mcPos = mcPos.set((Vec3i)world.position.add((Vec3i)pos));
                IBlockState mcBlockState = worldClient.getBlockState((BlockPos)mcPos);
                boolean isPlaced = BlockStateHelper.areBlockStatesEqual(blockState, mcBlockState);
                if (!isPlaced)
                  this.missing++;
              }
              this.missing = this.total - this.missing;
              if (this.total == 0) {
                this.progress = 100.0F;
              } else {
                this.progress = this.missing / this.total * 100.0F;//float
              }
            });
    } else {
      this.progress = 0.0F;
    }
  }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\feature\impl\hud\SchematicProgress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */