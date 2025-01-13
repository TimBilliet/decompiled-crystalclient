package com.github.lunatrius.schematica.command;

import co.crystaldev.client.util.type.Tuple;
import com.github.lunatrius.core.util.MBlockPos;
import com.github.lunatrius.schematica.block.state.pattern.BlockStateReplacer;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.github.lunatrius.schematica.reference.Reference;
import com.github.lunatrius.schematica.util.SchematicReplaceAction;
import net.minecraft.block.Block;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;

import java.util.Arrays;
import java.util.List;

public class CommandSchematicaReplace extends CommandSchematicaBase {
    private static final RegistryNamespacedDefaultedByKey<ResourceLocation, Block> BLOCK_REGISTRY = Block.blockRegistry;

    public String getCommandName() {
        return "schematicaReplace";
    }

    public String getCommandUsage(ICommandSender sender) {
        return I18n.format("schematica.command.replace.usage", new Object[0]);
    }

    public List<String> func_180525_a(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length < 3)
            return getListOfStringsMatchingLastWord(args, BLOCK_REGISTRY.getKeys());
        return null;
    }

    public List<String> getCommandAliases() {
        return Arrays.asList(new String[]{"schematicareplace", "schematicreplace", "schematicReplace"});
    }

    public boolean canCommandSenderUseCommand(ICommandSender obj) {
        return true;
    }

    public boolean isUsernameIndex(String[] strings, int i) {
        return false;
    }

    public int compareTo(ICommand obj) {
        return 0;
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        SchematicWorld schematic = ClientProxy.currentSchematic.schematic;
        if (schematic == null)
            throw new CommandException(I18n.format("schematica.command.replace.noSchematic", new Object[0]), new Object[0]);
        if (args.length != 2)
            throw new CommandException(I18n.format("schematica.command.replace.usage", new Object[0]), new Object[0]);
        try {
            BlockStateReplacer.BlockStateInfo patternInfo = BlockStateReplacer.fromString(args[0]);
            BlockStateHelper matcher = BlockStateReplacer.getMatcher(patternInfo);
            BlockStateReplacer.BlockStateInfo replacementInfo = BlockStateReplacer.fromString(args[1]);
            BlockStateReplacer replacer = BlockStateReplacer.forBlockState(replacementInfo.block.getDefaultState());
            Tuple<Integer, List<MBlockPos>> data = schematic.replaceBlock(matcher, replacer, replacementInfo.stateData);
            sender.addChatMessage((IChatComponent) new ChatComponentTranslation(I18n.format("schematica.command.replace.success", new Object[0]), new Object[]{data.getItem1()}));
            SchematicReplaceAction action = new SchematicReplaceAction(patternInfo, (List) data.getItem2());
            ClientProxy.currentSchematic.replaceHistory.push(action);
        } catch (Exception e) {
            Reference.logger.error("Something went wrong!", e);
            throw new CommandException(e.getMessage(), new Object[0]);
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\github\lunatrius\schematica\command\CommandSchematicaReplace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */