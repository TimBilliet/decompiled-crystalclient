package com.github.lunatrius.schematica.world.schematic;

import com.github.lunatrius.core.util.MBlockPos;
import com.github.lunatrius.schematica.api.ISchematic;
import com.github.lunatrius.schematica.api.event.PreSchematicSaveEvent;
import com.github.lunatrius.schematica.nbt.NBTHelper;
import com.github.lunatrius.schematica.reference.Reference;
import com.github.lunatrius.schematica.world.storage.Schematic;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.RegistryNamespacedDefaultedByKey;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SchematicAlpha extends SchematicFormat {
    private static final RegistryNamespacedDefaultedByKey<ResourceLocation, Block> BLOCK_REGISTRY = Block.blockRegistry;

    public ISchematic readFromNBT(NBTTagCompound tagCompound) {
        ItemStack icon = SchematicUtil.getIconFromNBT(tagCompound);
        byte[] localBlocks = tagCompound.getByteArray("Blocks");
        byte[] localMetadata = tagCompound.getByteArray("Data");
        boolean extra = false;
        byte[] extraBlocks = null;
        byte[] extraBlocksNibble = null;
        if (tagCompound.hasKey("AddBlocks")) {
            extra = true;
            extraBlocksNibble = tagCompound.getByteArray("AddBlocks");
            extraBlocks = new byte[extraBlocksNibble.length * 2];
            for (int j = 0; j < extraBlocksNibble.length; j++) {
                extraBlocks[j * 2] = (byte) (extraBlocksNibble[j] >> 4 & 0xF);
                extraBlocks[j * 2 + 1] = (byte) (extraBlocksNibble[j] & 0xF);
            }
        } else if (tagCompound.hasKey("Add")) {
            extra = true;
            extraBlocks = tagCompound.getByteArray("Add");
        }
        short width = tagCompound.getShort("Width");
        short length = tagCompound.getShort("Length");
        short height = tagCompound.getShort("Height");
        Short id = null;
        Map<Short, Short> oldToNew = new HashMap<>();
        if (tagCompound.hasKey("SchematicaMapping")) {
            NBTTagCompound mapping = tagCompound.getCompoundTag("SchematicaMapping");
            Set<String> names = mapping.getKeySet();
            for (String name : names)
                oldToNew.put(mapping.getShort(name), (short) Block.getIdFromBlock(BLOCK_REGISTRY.getObject(new ResourceLocation(name))));
        }
        MBlockPos pos = new MBlockPos();
        Schematic schematic = new Schematic(icon, width, height, length);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    int index = x + (y * length + z) * width;
                    int blockID = localBlocks[index] & 0xFF | (extra ? ((extraBlocks[index] & 0xFF) << 8) : 0);
                    int meta = localMetadata[index] & 0xFF;
                    if ((id = oldToNew.get((short) blockID)) != null)
                        blockID = id;
                    Block block = BLOCK_REGISTRY.getObjectById(blockID);
                    pos.set(x, y, z);
                    try {
                        IBlockState blockState = block.getStateFromMeta(meta);
                        schematic.setBlockState((BlockPos) pos, blockState);
                    } catch (Exception e) {
                        Reference.logger.error("Could not set block state at {} to {} with metadata {}", pos, BLOCK_REGISTRY.getNameForObject(block), meta, e);
                    }
                }
            }
        }
        NBTTagList tileEntitiesList = tagCompound.getTagList("TileEntities", 10);
        for (int i = 0; i < tileEntitiesList.tagCount(); i++) {
            try {
                TileEntity tileEntity = NBTHelper.readTileEntityFromCompound(tileEntitiesList.getCompoundTagAt(i));
                if (tileEntity != null)
                    schematic.setTileEntity(tileEntity.getPos(), tileEntity);
            } catch (Exception e) {
                Reference.logger.error("TileEntity failed to load properly!", e);
            }
        }
        return (ISchematic) schematic;
    }

    public boolean writeToNBT(NBTTagCompound tagCompound, ISchematic schematic) {
        NBTTagCompound tagCompoundIcon = new NBTTagCompound();
        ItemStack icon = schematic.getIcon();
        icon.writeToNBT(tagCompoundIcon);
        tagCompound.setTag("Icon", (NBTBase) tagCompoundIcon);
        tagCompound.setShort("Width", (short) schematic.getWidth());
        tagCompound.setShort("Length", (short) schematic.getLength());
        tagCompound.setShort("Height", (short) schematic.getHeight());
        int size = schematic.getWidth() * schematic.getLength() * schematic.getHeight();
        byte[] localBlocks = new byte[size];
        byte[] localMetadata = new byte[size];
        byte[] extraBlocks = new byte[size];
        byte[] extraBlocksNibble = new byte[(int) Math.ceil(size / 2.0D)];
        boolean extra = false;
        MBlockPos pos = new MBlockPos();
        Map<String, Short> mappings = new HashMap<>();
        for (int x = 0; x < schematic.getWidth(); x++) {
            for (int y = 0; y < schematic.getHeight(); y++) {
                for (int z = 0; z < schematic.getLength(); z++) {
                    int index = x + (y * schematic.getLength() + z) * schematic.getWidth();
                    IBlockState blockState = schematic.getBlockState((BlockPos) pos.set(x, y, z));
                    Block block = blockState.getBlock();
                    int blockId = Block.getIdFromBlock(block);
                    localBlocks[index] = (byte) blockId;
                    localMetadata[index] = (byte) block.getMetaFromState(blockState);
                    extraBlocks[index] = (byte) (blockId >> 8);
                    if ((byte) (blockId >> 8) > 0)
                        extra = true;
                    String name = String.valueOf(BLOCK_REGISTRY.getNameForObject(block));
                    if (!mappings.containsKey(name))
                        mappings.put(name, Short.valueOf((short) blockId));
                }
            }
        }
        int count = 20;
        NBTTagList tileEntitiesList = new NBTTagList();
        for (TileEntity tileEntity : schematic.getTileEntities()) {
            try {
                NBTTagCompound tileEntityTagCompound = NBTHelper.writeTileEntityToCompound(tileEntity);
                tileEntitiesList.appendTag((NBTBase) tileEntityTagCompound);
            } catch (Exception e) {
                BlockPos tePos = tileEntity.getPos();
                int index = tePos.getX() + (tePos.getY() * schematic.getLength() + tePos.getZ()) * schematic.getWidth();
                if (--count > 0) {
                    IBlockState blockState = schematic.getBlockState(tePos);
                    Block block = blockState.getBlock();
                    Reference.logger.error("Block {}[{}] with TileEntity {} failed to save! Replacing with bedrock...", new Object[]{block, (block != null) ? BLOCK_REGISTRY.getNameForObject(block) : "?", tileEntity.getClass().getName(), e});
                }
                localBlocks[index] = (byte) Block.getIdFromBlock(Blocks.bedrock);
                localMetadata[index] = 0;
                extraBlocks[index] = 0;
            }
        }
        for (int i = 0; i < extraBlocksNibble.length; i++) {
            if (i * 2 + 1 < extraBlocks.length) {
                extraBlocksNibble[i] = (byte) (extraBlocks[i * 2] << 4 | extraBlocks[i * 2 + 1]);
            } else {
                extraBlocksNibble[i] = (byte) (extraBlocks[i * 2] << 4);
            }
        }
        NBTTagList entityList = new NBTTagList();
        List<Entity> entities = schematic.getEntities();
        for (Entity entity : entities) {
            try {
                NBTTagCompound entityCompound = NBTHelper.writeEntityToCompound(entity);
                if (entityCompound != null)
                    entityList.appendTag((NBTBase) entityCompound);
            } catch (Throwable t) {
                Reference.logger.error("Entity {} failed to save, skipping!", new Object[]{entity, t});
            }
        }
        PreSchematicSaveEvent event = new PreSchematicSaveEvent(schematic, mappings);
        event.call();
        NBTTagCompound nbtMapping = new NBTTagCompound();
        for (Map.Entry<String, Short> entry : mappings.entrySet())
            nbtMapping.setShort(entry.getKey(), ((Short) entry.getValue()).shortValue());
        tagCompound.setString("Materials", "Alpha");
        tagCompound.setByteArray("Blocks", localBlocks);
        tagCompound.setByteArray("Data", localMetadata);
        if (extra)
            tagCompound.setByteArray("AddBlocks", extraBlocksNibble);
        tagCompound.setTag("Entities", (NBTBase) entityList);
        tagCompound.setTag("TileEntities", (NBTBase) tileEntitiesList);
        tagCompound.setTag("SchematicaMapping", (NBTBase) nbtMapping);
        NBTTagCompound extendedMetadata = event.extendedMetadata;
        if (!extendedMetadata.hasNoTags())
            tagCompound.setTag("ExtendedMetadata", (NBTBase) extendedMetadata);
        return true;
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\world\schematic\SchematicAlpha.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */