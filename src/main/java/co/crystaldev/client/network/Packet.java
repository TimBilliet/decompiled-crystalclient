package co.crystaldev.client.network;

import co.crystaldev.client.Reference;
import co.crystaldev.client.network.plugin.server.PacketCooldown;
import co.crystaldev.client.network.plugin.server.PacketDisallowedModules;
import co.crystaldev.client.network.plugin.server.PacketNotification;
import co.crystaldev.client.network.plugin.server.PacketUpdateWorld;
//import co.crystaldev.client.network.plugin.shared.PacketWaypointAdd;
//import co.crystaldev.client.network.plugin.shared.PacketWaypointRemove;
import co.crystaldev.client.network.plugin.shared.PacketWaypointAdd;
import co.crystaldev.client.network.plugin.shared.PacketWaypointRemove;
import co.crystaldev.client.network.socket.client.PacketOnlineUsers;
import co.crystaldev.client.network.socket.client.PacketServerConnection;
import co.crystaldev.client.network.socket.client.cosmetic.PacketRequestCosmetics;
import co.crystaldev.client.network.socket.client.cosmetic.PacketStartEmote;
import co.crystaldev.client.network.socket.client.cosmetic.PacketStopEmote;
import co.crystaldev.client.network.socket.client.cosmetic.PacketUpdateSelectedCosmetic;
import co.crystaldev.client.network.socket.client.group.*;
import co.crystaldev.client.network.socket.shared.PacketAuthInfo;
import co.crystaldev.client.network.socket.shared.PacketGroupInfo;
//import co.crystaldev.client.network.socket.shared.PacketServerList;
import co.crystaldev.client.network.socket.shared.PacketServerList;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class Packet {
    public Object getAttachment() {
        return this.attachment;
    }

    public static Packet handle(byte[] data) {
        return handle(data, null);
    }

    public static Packet handle(byte[] data, Object attachment) {
        ByteBufWrapper wrapper = new ByteBufWrapper(Unpooled.wrappedBuffer(data));
        int packetId = wrapper.readVarInt();
        Class<? extends Packet> packetClass = idToClass.get(Integer.valueOf(packetId));
        if (packetClass != null)
            try {
                Packet packet = packetClass.newInstance();
                packet.attach(attachment);
                packet.read(wrapper);
                return packet;
            } catch (IOException | InstantiationException | IllegalAccessException ex) {
//        WebClient.logger.error("Exception handling packet", ex);
            }
        return null;
    }

    public static byte[] getPacketData(Packet packet) {
        return getPacketBuf(packet).array();
    }

    public static ByteBuf getPacketBuf(Packet packet) {
        ByteBufWrapper wrappedBuffer = new ByteBufWrapper(Unpooled.buffer());
        wrappedBuffer.writeVarInt(classToId.get(packet.getClass()));
        try {
            packet.write(wrappedBuffer);
        } catch (IOException ex) {
            Reference.LOGGER.error("Unable to write packet", ex);
        }
        return wrappedBuffer.buf();
    }

    protected static void addPacket(int id, Class<? extends Packet> clazz) {
        if (classToId.containsKey(clazz))
            throw new IllegalArgumentException(String.format("Duplicate class entry found! %s is already being used by ID %d", clazz.getSimpleName(), classToId.get(clazz)));
        if (idToClass.containsKey(id))
            throw new IllegalArgumentException(String.format("Duplicate packet ID entry found! %d is already being used by class %s", Integer.valueOf(id), ((Class) idToClass.get(Integer.valueOf(id))).getSimpleName()));
        classToId.put(clazz, id);
        idToClass.put(id, clazz);
    }

    public <T> void attach(T obj) {
        this.attachment = obj;
    }

    protected void writeBlob(ByteBufWrapper b, byte[] bytes) {
        b.buf().writeShort(bytes.length);
        b.buf().writeBytes(bytes);
    }

    protected byte[] readBlob(ByteBufWrapper b) {
        short key = b.buf().readShort();
        if (key < 0)
            return null;
        byte[] blob = new byte[key];
        b.buf().readBytes(blob);
        return blob;
    }

    private static final Map<Class<? extends Packet>, Integer> classToId = new HashMap<>();

    private static final Map<Integer, Class<? extends Packet>> idToClass = new HashMap<>();

    private Object attachment;

    static {
        addPacket(0, (Class) PacketServerList.class);
        addPacket(1, (Class) PacketOnlineUsers.class);
        addPacket(2, (Class) PacketServerConnection.class);
        addPacket(3, (Class) PacketAuthInfo.class);
        addPacket(96, (Class) PacketStartEmote.class);
        addPacket(97, (Class) PacketStopEmote.class);
        addPacket(98, (Class) PacketRequestCosmetics.class);
        addPacket(99, (Class) PacketUpdateSelectedCosmetic.class);
        addPacket(200, (Class) PacketGroupInfo.class);
        addPacket(201, (Class) PacketGroupUpdate.class);
        addPacket(202, (Class) PacketCreateGroup.class);
        addPacket(203, (Class) PacketLeaveGroup.class);
        addPacket(204, (Class) PacketDeleteGroup.class);
        addPacket(208, (Class) PacketGroupMemberRankChange.class);
        addPacket(209, (Class) PacketGroupMemberAction.class);
        addPacket(210, (Class) PacketPendingGroupMemberAction.class);
        addPacket(211, (Class) PacketHighlightChunk.class);
        addPacket(212, (Class) PacketRemoveChunkHighlight.class);
        addPacket(213, (Class) PacketClearChunkHighlights.class);
        addPacket(280, (Class) PacketGroupPermissionUpdate.class);
        addPacket(281, (Class) PacketGroupSchematicAction.class);
        addPacket(282, (Class) PacketShareSchematic.class);
        addPacket(292, (Class) PacketGroupPrivateMessage.class);
        addPacket(293, (Class) PacketGroupChat.class);
        addPacket(294, (Class) PacketPatchcrumbUpdate.class);
        addPacket(295, (Class) PacketGroupInvitationAction.class);
        addPacket(296, (Class) PacketStatusUpdate.class);
        addPacket(297, (Class) PacketPingLocation.class);
        addPacket(298, (Class) PacketFocusPlayer.class);
        addPacket(299, (Class) PacketAdjHelper.class);
        addPacket(2000, (Class) PacketWaypointAdd.class);
        addPacket(2001, (Class) PacketWaypointRemove.class);
        addPacket(2100, (Class) PacketDisallowedModules.class);
        addPacket(2101, (Class) PacketCooldown.class);
        addPacket(2102, (Class) PacketUpdateWorld.class);
        addPacket(2103, (Class) PacketNotification.class);
    }

    public abstract void write(ByteBufWrapper paramByteBufWrapper) throws IOException;

    public abstract void read(ByteBufWrapper paramByteBufWrapper) throws IOException;

    public abstract void process(INetHandler paramINetHandler);
}
