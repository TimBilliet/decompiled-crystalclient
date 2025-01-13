package co.crystaldev.client.util.objects;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class MissingSchematicBlock {
    private final BlockPos mcPos;

    private final BlockPos schPos;

    private final AxisAlignedBB aabb;

    private final IBlockState schBlockState;

    public MissingSchematicBlock(BlockPos mcPos, BlockPos schPos, AxisAlignedBB aabb, IBlockState schBlockState) {
        this.mcPos = mcPos;
        this.schPos = schPos;
        this.aabb = aabb;
        this.schBlockState = schBlockState;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof MissingSchematicBlock))
            return false;
        MissingSchematicBlock other = (MissingSchematicBlock) o;
        if (!other.canEqual(this))
            return false;
        Object this$mcPos = getMcPos(), other$mcPos = other.getMcPos();
        if ((this$mcPos == null) ? (other$mcPos != null) : !this$mcPos.equals(other$mcPos))
            return false;
        Object this$schPos = getSchPos(), other$schPos = other.getSchPos();
        if ((this$schPos == null) ? (other$schPos != null) : !this$schPos.equals(other$schPos))
            return false;
        Object this$aabb = getAabb(), other$aabb = other.getAabb();
        if ((this$aabb == null) ? (other$aabb != null) : !this$aabb.equals(other$aabb))
            return false;
        Object this$schBlockState = getSchBlockState(), other$schBlockState = other.getSchBlockState();
        return !((this$schBlockState == null) ? (other$schBlockState != null) : !this$schBlockState.equals(other$schBlockState));
    }

    protected boolean canEqual(Object other) {
        return other instanceof MissingSchematicBlock;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $mcPos = getMcPos();
        result = result * 59 + (($mcPos == null) ? 43 : $mcPos.hashCode());
        Object $schPos = getSchPos();
        result = result * 59 + (($schPos == null) ? 43 : $schPos.hashCode());
        Object $aabb = getAabb();
        result = result * 59 + (($aabb == null) ? 43 : $aabb.hashCode());
        Object $schBlockState = getSchBlockState();
        return result * 59 + (($schBlockState == null) ? 43 : $schBlockState.hashCode());
    }

    public String toString() {
        return "MissingSchematicBlock(mcPos=" + getMcPos() + ", schPos=" + getSchPos() + ", aabb=" + getAabb() + ", schBlockState=" + getSchBlockState() + ")";
    }

    public BlockPos getMcPos() {
        return this.mcPos;
    }

    public BlockPos getSchPos() {
        return this.schPos;
    }

    public AxisAlignedBB getAabb() {
        return this.aabb;
    }

    public IBlockState getSchBlockState() {
        return this.schBlockState;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\clien\\util\objects\MissingSchematicBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */