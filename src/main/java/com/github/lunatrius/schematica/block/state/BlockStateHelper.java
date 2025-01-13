package com.github.lunatrius.schematica.block.state;

import co.crystaldev.client.feature.impl.factions.Schematica;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockStateHelper {
    private static final List<Class<? extends Block>> UNCHECKED_BLOCK_STATES = (List<Class<? extends Block>>) ImmutableList.of(BlockCactus.class, BlockReed.class, BlockTripWire.class);

    public static <T extends Comparable<T>> IProperty<T> getProperty(IBlockState blockState, String name) {
        for (IProperty<T> prop : blockState.getPropertyNames()) {
            if (prop.getName().equals(name))
                return prop;
        }
        return null;
    }

    public static <T extends Comparable<T>> T getPropertyValue(IBlockState blockState, String name) {
        IProperty<T> property = getProperty(blockState, name);
        if (property == null)
            throw new IllegalArgumentException(name + " does not exist in " + blockState);
        return (T) blockState.getValue(property);
    }

    public static List<String> getFormattedProperties(IBlockState blockState) {
        List<String> list = new ArrayList<>();
        for (UnmodifiableIterator<Map.Entry<IProperty, Comparable>> unmodifiableIterator = blockState.getProperties().entrySet().iterator(); unmodifiableIterator.hasNext(); ) {
            Map.Entry<IProperty, Comparable> entry = unmodifiableIterator.next();
            IProperty key = entry.getKey();
            Comparable value = entry.getValue();
            String formattedValue = value.toString();
            if (Boolean.TRUE.equals(value)) {
                formattedValue = EnumChatFormatting.GREEN + formattedValue + EnumChatFormatting.RESET;
            } else if (Boolean.FALSE.equals(value)) {
                formattedValue = EnumChatFormatting.RED + formattedValue + EnumChatFormatting.RESET;
            }
            list.add(key.getName() + ": " + formattedValue);
        }
        return list;
    }

    public static boolean areBlockStatesEqual(IBlockState blockStateA, IBlockState blockStateB) {
        if (blockStateA == blockStateB)
            return true;
        Block blockA = blockStateA.getBlock();
        Block blockB = blockStateB.getBlock();
        if ((Schematica.getInstance()).enabled && (Schematica.getInstance()).dispenserMetaFix && blockA instanceof BlockDispenser && blockB instanceof BlockDispenser)
            return compareDispensers((BlockDispenser) blockA, (BlockDispenser) blockB, blockStateA, blockStateB);
        return ((blockA == blockB && UNCHECKED_BLOCK_STATES.contains(blockA.getClass()) && UNCHECKED_BLOCK_STATES.contains(blockB.getClass())) || (blockA == blockB && blockA.getMetaFromState(blockStateA) == blockB.getMetaFromState(blockStateB)));
    }

    private static boolean compareDispensers(BlockDispenser blockA, BlockDispenser blockB, IBlockState blockStateA, IBlockState blockStateB) {
        int a = blockA.getMetaFromState(blockStateA);
        int b = blockB.getMetaFromState(blockStateB);
        if (a > 5)
            a -= 8;
        if (b > 5)
            b -= 8;
        return (a == b);
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\block\state\BlockStateHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */