package com.github.lunatrius.schematica.block.state.pattern;

import com.github.lunatrius.core.exceptions.LocalizedException;
import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.RegistryNamespacedDefaultedByKey;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BlockStateReplacer {
    private static final RegistryNamespacedDefaultedByKey<ResourceLocation, Block> BLOCK_REGISTRY = Block.blockRegistry;

    private final IBlockState defaultReplacement;

    private BlockStateReplacer(IBlockState defaultReplacement) {
        this.defaultReplacement = defaultReplacement;
    }

    public IBlockState getReplacement(IBlockState original, Map<IProperty, Comparable> properties) {
        IBlockState replacement = this.defaultReplacement;
        replacement = applyProperties(replacement, original.getProperties());
        replacement = applyProperties(replacement, properties);
        return replacement;
    }

    private IBlockState applyProperties(IBlockState blockState, Map<IProperty, Comparable> properties) {
        for (Map.Entry<IProperty, Comparable> entry : properties.entrySet()) {
            try {
                blockState = blockState.withProperty(entry.getKey(), entry.getValue());
            } catch (IllegalArgumentException illegalArgumentException) {
            }
        }
        return blockState;
    }

    public static BlockStateReplacer forBlockState(IBlockState replacement) {
        return new BlockStateReplacer(replacement);
    }

    public static BlockStateHelper getMatcher(BlockStateInfo blockStateInfo) {
        BlockStateHelper matcher = BlockStateHelper.forBlock(blockStateInfo.block);
        for (Map.Entry<IProperty, Comparable> entry : blockStateInfo.stateData.entrySet()) {
            matcher.where(entry.getKey(), input -> (input != null && input.equals(entry.getValue())));
        }
        return matcher;
    }

    public static BlockStateInfo fromString(String input) throws LocalizedException {
        String blockName, stateData;
        int start = input.indexOf('[');
        int end = input.indexOf(']');
        if (start > -1 && end > -1) {
            blockName = input.substring(0, start);
            stateData = input.substring(start + 1, end);
        } else {
            blockName = input;
            stateData = "";
        }
        ResourceLocation location = new ResourceLocation(blockName);
        if (!BLOCK_REGISTRY.containsKey(location))
            throw new LocalizedException(I18n.format("schematica.message.invalidBlock", new Object[0]), new Object[]{blockName});
        Block block = (Block) BLOCK_REGISTRY.getObject(location);
        Map<IProperty, Comparable> propertyData = parsePropertyData(block.getDefaultState(), stateData, true);
        return new BlockStateInfo(block, propertyData);
    }

    public static Map<IProperty, Comparable> parsePropertyData(IBlockState blockState, String stateData, boolean strict) throws LocalizedException {
        HashMap<IProperty, Comparable> map = new HashMap<>();
        if (stateData == null || stateData.length() == 0)
            return map;
        String[] propertyPairs = stateData.split(",");
        for (String propertyPair : propertyPairs) {
            String[] split = propertyPair.split("=");
            if (split.length != 2)
                throw new LocalizedException(I18n.format("schematica.message.invalidProperty", new Object[0]), new Object[]{propertyPair});
            putMatchingProperty(map, blockState, split[0], split[1], strict);
        }
        return map;
    }

    private static boolean putMatchingProperty(Map<IProperty, Comparable> map, IBlockState blockState, String name, String value, boolean strict) throws LocalizedException {
        for (IProperty property : blockState.getPropertyNames()) {
            if (property.getName().equalsIgnoreCase(name)) {
                Collection<Comparable> allowedValues = property.getAllowedValues();
                for (Comparable allowedValue : allowedValues) {
                    if (String.valueOf(allowedValue).equalsIgnoreCase(value)) {
                        map.put(property, allowedValue);
                        return true;
                    }
                }
            }
        }
        if (strict)
            throw new LocalizedException(I18n.format("schematica.message.invalidPropertyForBlock", new Object[0]), name + "=" + value, BLOCK_REGISTRY.getNameForObject(blockState.getBlock()));
        return false;
    }

    public static class BlockStateInfo {
        public final Block block;

        public final Map<IProperty, Comparable> stateData;

        public BlockStateInfo(Block block, Map<IProperty, Comparable> stateData) {
            this.block = block;
            this.stateData = stateData;
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\block\state\pattern\BlockStateReplacer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */