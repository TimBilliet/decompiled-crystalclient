package com.github.lunatrius.schematica.world.schematic;

import co.crystaldev.client.mixin.accessor.net.minecraft.nbt.MixinNBTTagCompound;
import com.github.lunatrius.schematica.api.ISchematic;
import com.github.lunatrius.schematica.api.event.PostSchematicCaptureEvent;
import com.github.lunatrius.schematica.reference.Reference;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public abstract class SchematicFormat {
    public static final Map<String, SchematicFormat> FORMATS = new HashMap<>();

    public abstract ISchematic readFromNBT(NBTTagCompound paramNBTTagCompound);

    public abstract boolean writeToNBT(NBTTagCompound paramNBTTagCompound, ISchematic paramISchematic);

    public static ISchematic readFromFile(File file) {
        try {
            NBTTagCompound tagCompound = SchematicUtil.readTagCompoundFromFile(file);
            String format = tagCompound.getString("Materials");
            SchematicFormat schematicFormat = FORMATS.get(format);
            if (schematicFormat == null)
                throw new UnsupportedFormatException(format);
            return schematicFormat.readFromNBT(tagCompound);
        } catch (Exception ex) {
            Reference.logger.error("Failed to read schematic!", ex);
            return null;
        }
    }

    public static ISchematic readFromFile(File directory, String filename) {
        return readFromFile(new File(directory, filename));
    }

    public static boolean writeToFile(File file, ISchematic schematic) {
        try {
            PostSchematicCaptureEvent event = new PostSchematicCaptureEvent(schematic);
            event.call();
            NBTTagCompound tagCompound = new NBTTagCompound();
            ((SchematicFormat) FORMATS.get(FORMAT_DEFAULT)).writeToNBT(tagCompound, schematic);
            try (DataOutputStream dataOutputStream = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(file)))) {
                MixinNBTTagCompound.callWriteEntry("Schematic", (NBTBase) tagCompound, dataOutputStream);
            }
            return true;
        } catch (Exception ex) {
            Reference.logger.error("Failed to write schematic!", ex);
            return false;
        }
    }

    public static boolean writeToFile(File directory, String filename, ISchematic schematic) {
        return writeToFile(new File(directory, filename), schematic);
    }

    public static void writeToFileAndNotify(File file, ISchematic schematic, EntityPlayer player) {
        boolean success = writeToFile(file, schematic);
        String message = success ? "schematica.command.save.saveSucceeded" : "schematica.command.save.saveFailed";
        player.addChatMessage((IChatComponent) new ChatComponentTranslation(I18n.format(message, new Object[0]), new Object[]{file.getName()}));
    }

    static {
        FORMATS.put("Alpha", new SchematicAlpha());
    }

    public static String FORMAT_DEFAULT = "Alpha";
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\world\schematic\SchematicFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */