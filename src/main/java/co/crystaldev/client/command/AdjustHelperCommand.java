//package co.crystaldev.client.command;
//
//import co.crystaldev.client.Client;
//import co.crystaldev.client.command.base.AbstractCommand;
//import co.crystaldev.client.command.base.CommandInfo;
//import co.crystaldev.client.command.base.args.CommandArguments;
//import co.crystaldev.client.command.base.exceptions.CommandException;
//import co.crystaldev.client.feature.impl.factions.AdjustHelper;
//import net.minecraft.client.Minecraft;
//import net.minecraft.command.ICommandSender;
//import net.minecraft.util.BlockPos;
//import net.minecraft.util.EnumFacing;
//import net.minecraft.util.Vec3;
//
//@CommandInfo(name = "adjusthelper", aliases = {"adjust", "adj"}, description = "Quickly access to many Adjust Helper features.", usage = {"/adjusthelper [pos1 | pos2] &7- Defines scan boundaries.", "/adjusthelper reset &7- Resets the defined boundaries.", "/adjusthelper scan <cardinal direction> &7- Start a scan with defined boundaries."}, minimumArguments = 1)
//public class AdjustHelperCommand extends AbstractCommand {
//  public void execute(ICommandSender sender, CommandArguments arguments) throws CommandException {
//    String errorMsg;
//    if (!(AdjustHelper.getInstance()).enabled) {
//      sendErrorMessage("This command cannot be used while the module is disabled.");
//      return;
//    }
//    Vec3 playerPos = (Minecraft.getMinecraft()).thePlayer.getPositionVector();
//    BlockPos pos = new BlockPos(Math.floor(playerPos.xCoord), Math.floor(playerPos.yCoord), Math.floor(playerPos.zCoord));
//    switch (arguments.getString(0).toLowerCase()) {
//      case "pos1":
//        (AdjustHelper.getInstance()).pos1 = pos;
//        (AdjustHelper.getInstance()).currentAdjust = null;
//        Client.sendMessage(String.format("pos1 set to &bX:&r %s&b, Y:&r %s&b, Z: &r%s", new Object[] { Integer.valueOf(pos.getX()), Integer.valueOf(pos.getY()), Integer.valueOf(pos.getZ()) }), true);
//        return;
//      case "pos2":
//        (AdjustHelper.getInstance()).pos2 = pos;
//        (AdjustHelper.getInstance()).currentAdjust = null;
//        Client.sendMessage(String.format("pos2 set to &bX:&r %s&b, Y:&r %s&b, Z: &r%s", new Object[] { Integer.valueOf(pos.getX()), Integer.valueOf(pos.getY()), Integer.valueOf(pos.getZ()) }), true);
//        return;
//      case "scan":
//        errorMsg = "Invalid direction. Valid directions: &8[&7North, South, East, West&8]";
//        if (arguments.ensureArguments(-1, -1, 2, errorMsg)) {
//          EnumFacing direction = EnumFacing.byName(arguments.getString(1));
//          if (direction != null) {
//            AdjustHelper.getInstance().scan(direction);
//          } else {
//            throw new CommandException(errorMsg, new Object[0]);
//          }
//        }
//        return;
//      case "reset":
//        (AdjustHelper.getInstance()).pos1 = new BlockPos(0, 0, 0);
//        (AdjustHelper.getInstance()).pos2 = new BlockPos(0, 0, 0);
//        (AdjustHelper.getInstance()).currentAdjust = null;
//        Client.sendMessage("Adjust positions have been reset", true);
//        return;
//    }
//    Client.sendMessage(getCommandUsage(sender), false);
//  }
//}
//
//
///* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\command\AdjustHelperCommand.class
// * Java compiler version: 8 (52.0)
// * JD-Core Version:       1.1.3
// */