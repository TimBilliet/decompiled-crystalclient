package com.github.lunatrius.schematica.client.printer.registry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlacementData {
    private final IValidPlayerFacing validPlayerFacing;

    private final IValidBlockFacing validBlockFacing;

    private IOffset offsetX;

    private IOffset offsetY;

    private IOffset offsetZ;

    private IExtraClick extraClick;

    public PlacementData() {
        this(null, null);
    }

    public PlacementData(IValidPlayerFacing validPlayerFacing) {
        this(validPlayerFacing, null);
    }

    public PlacementData(IValidBlockFacing validBlockFacing) {
        this(null, validBlockFacing);
    }

    public PlacementData(IValidPlayerFacing validPlayerFacing, IValidBlockFacing validBlockFacing) {
        this.validPlayerFacing = validPlayerFacing;
        this.validBlockFacing = validBlockFacing;
        this.offsetX = null;
        this.offsetY = null;
        this.offsetZ = null;
    }

    public PlacementData setOffsetX(IOffset offset) {
        this.offsetX = offset;
        return this;
    }

    public PlacementData setOffsetY(IOffset offset) {
        this.offsetY = offset;
        return this;
    }

    public PlacementData setOffsetZ(IOffset offset) {
        this.offsetZ = offset;
        return this;
    }

    public PlacementData setExtraClick(IExtraClick extraClick) {
        this.extraClick = extraClick;
        return this;
    }

    public float getOffsetX(IBlockState blockState) {
        if (this.offsetX != null)
            return this.offsetX.getOffset(blockState);
        return 0.5F;
    }

    public float getOffsetY(IBlockState blockState) {
        if (this.offsetY != null)
            return this.offsetY.getOffset(blockState);
        return 0.5F;
    }

    public float getOffsetZ(IBlockState blockState) {
        if (this.offsetZ != null)
            return this.offsetZ.getOffset(blockState);
        return 0.5F;
    }

    public int getExtraClicks(IBlockState blockState) {
        if (this.extraClick != null)
            return this.extraClick.getExtraClicks(blockState);
        return 0;
    }

    public boolean isValidPlayerFacing(IBlockState blockState, EntityPlayer player, BlockPos pos, World world) {
        return (this.validPlayerFacing == null || this.validPlayerFacing.isValid(blockState, player, pos, world));
    }

    public List<EnumFacing> getValidBlockFacings(List<EnumFacing> solidSides, IBlockState blockState) {
        List<EnumFacing> list = (this.validBlockFacing != null) ? this.validBlockFacing.getValidBlockFacings(solidSides, blockState) : new ArrayList<>(solidSides);
        for (Iterator<EnumFacing> iterator = list.iterator(); iterator.hasNext(); ) {
            EnumFacing facing = iterator.next();
            if (this.offsetY != null) {
                float offset = this.offsetY.getOffset(blockState);
                if (offset < 0.5D && facing == EnumFacing.UP) {
                    iterator.remove();
                    continue;
                }
                if (offset > 0.5D && facing == EnumFacing.DOWN)
                    iterator.remove();
            }
        }
        return list;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\client\printer\registry\PlacementData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */