//package co.crystaldev.client.network.socket.client.group;
//
//import co.crystaldev.client.Client;
//import co.crystaldev.client.Reference;
//import co.crystaldev.client.feature.impl.factions.FloatFinder;
//import co.crystaldev.client.feature.settings.GroupOptions;
//import co.crystaldev.client.network.ByteBufWrapper;
//import co.crystaldev.client.network.INetHandler;
//import co.crystaldev.client.network.Packet;
//import com.google.gson.JsonObject;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.network.NetworkPlayerInfo;
//import net.minecraft.util.BlockPos;
//
//import java.io.IOException;
//import java.util.UUID;
//
//public class PacketFloatFinder extends Packet {
//
//    private UUID id;
//    private BlockPos horizontal;
//    private BlockPos vertical;
//    private BlockPos barrelBlock;
//    private BlockPos powerBlock;
//
//    @Override
//    public void write(ByteBufWrapper out) throws IOException {
//        JsonObject obj = new JsonObject();
//        FloatFinder instance = FloatFinder.getInstance();
//        obj.addProperty("floatX", instance.horizontal.getX());
//        obj.addProperty("floatY", instance.horizontal.getY());
//        obj.addProperty("floatZ", instance.horizontal.getZ());
//        obj.addProperty("barrelX", instance.barrelBlockPos.getX());
//        obj.addProperty("barrelY", instance.barrelBlockPos.getY());
//        obj.addProperty("barrelZ", instance.barrelBlockPos.getZ());
//        obj.addProperty("powerX", instance.powerBlockPos.getX());
//        obj.addProperty("powerZ", instance.powerBlockPos.getZ());
//        out.writeString(Reference.GSON.toJson(obj));
//    }
//
//    @Override
//    public void read(ByteBufWrapper in) throws IOException {
//        id = in.readUUID();
//        boolean fromSameServer = Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap().stream().anyMatch(pl -> pl.getGameProfile().getId().equals(id));
//        if (Minecraft.getMinecraft().theWorld != null && fromSameServer) {
//            JsonObject obj = Reference.GSON.fromJson(in.readString(), JsonObject.class);
//            horizontal = new BlockPos(obj.get("floatX").getAsInt(), obj.get("floatY").getAsInt(), obj.get("floatZ").getAsInt());
//            vertical = new BlockPos(obj.get("barrelX").getAsInt(), obj.get("floatY").getAsInt(), obj.get("barrelZ").getAsInt());
//            barrelBlock = new BlockPos(obj.get("barrelX").getAsInt(), obj.get("barrelY").getAsInt(), obj.get("barrelZ").getAsInt());
//            powerBlock = new BlockPos(obj.get("powerX").getAsInt(), obj.get("barrelY").getAsInt(), obj.get("powerZ").getAsInt());
//        }
//    }
//
//    @Override
//    public void process(INetHandler handler) {
//        if (Minecraft.getMinecraft().theWorld == null || Minecraft.getMinecraft().getNetHandler() == null
//                || !(FloatFinder.getInstance()).enabled || !(GroupOptions.getInstance()).sharedFloatPos
//                || horizontal == null || vertical == null || barrelBlock == null || powerBlock == null) {
//            return;
//        }
//        for (NetworkPlayerInfo player : Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()) {
//            if (player.getGameProfile().getId().equals(id)) {
//                Client.sendMessage("Incoming group Float Finder position from " + player.getGameProfile().getName(), true);
//                FloatFinder.getInstance().horizontal = horizontal;
//                FloatFinder.getInstance().vertical = vertical;
//                FloatFinder.getInstance().barrelBlockPos = barrelBlock;
//                FloatFinder.getInstance().powerBlockPos = powerBlock;
//            }
//        }
//    }
//}
