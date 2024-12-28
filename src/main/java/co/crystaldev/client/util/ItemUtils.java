package co.crystaldev.client.util;


import co.crystaldev.client.util.enums.ChatColor;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;

import java.util.List;

public class ItemUtils {
    public static List<String> getLoreLines(ItemStack is) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        if (!is.hasTagCompound())
            return (List<String>)builder.build();
        NBTTagList loreNbt = is.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
        for (int i = 0; i < loreNbt.tagCount(); i++)
            builder.add(loreNbt.getStringTagAt(i));
        return (List<String>)builder.build();
    }

    public static List<String> getLoreLinesStripped(ItemStack is) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        List<String> loreLines = getLoreLines(is);
        for (String line : loreLines)
            builder.add(ChatColor.stripColor(line));
        return (List<String>)builder.build();
    }
}