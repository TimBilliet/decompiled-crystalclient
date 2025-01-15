package co.crystaldev.client.network;

import co.crystaldev.client.Reference;
import co.crystaldev.client.network.plugin.server.PacketCooldown;
import co.crystaldev.client.network.plugin.server.PacketDisallowedModules;
import co.crystaldev.client.network.plugin.server.PacketNotification;
import co.crystaldev.client.network.plugin.server.PacketUpdateWorld;
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
        Class<? extends Packet> packetClass = idToClass.get(packetId);
        if (packetClass != null)
            try {
                Packet packet = packetClass.newInstance();
                packet.attach(attachment);
                packet.read(wrapper);
                return packet;
            } catch (IOException | InstantiationException | IllegalAccessException ex) {
                WebClient.logger.error("Exception handling packet", ex);
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
            throw new IllegalArgumentException(String.format("Duplicate packet ID entry found! %d is already being used by class %s", id, idToClass.get(id).getSimpleName()));
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
        addPacket(0, PacketServerList.class);
        addPacket(1, PacketOnlineUsers.class);
        addPacket(2, PacketServerConnection.class);
        addPacket(3, PacketAuthInfo.class);
        addPacket(96, PacketStartEmote.class);
        addPacket(97, PacketStopEmote.class);
        addPacket(98, PacketRequestCosmetics.class);
        addPacket(99, PacketUpdateSelectedCosmetic.class);
        addPacket(200, PacketGroupInfo.class);
        addPacket(201, PacketGroupUpdate.class);
        addPacket(202, PacketCreateGroup.class);
        addPacket(203, PacketLeaveGroup.class);
        addPacket(204, PacketDeleteGroup.class);
        addPacket(208, PacketGroupMemberRankChange.class);
        addPacket(209, PacketGroupMemberAction.class);
        addPacket(210, PacketPendingGroupMemberAction.class);
        addPacket(211, PacketHighlightChunk.class);
        addPacket(212, PacketRemoveChunkHighlight.class);
        addPacket(213, PacketClearChunkHighlights.class);
        addPacket(280, PacketGroupPermissionUpdate.class);
        addPacket(281, PacketGroupSchematicAction.class);
        addPacket(282, PacketShareSchematic.class);
        addPacket(292, PacketGroupPrivateMessage.class);
        addPacket(293, PacketGroupChat.class);
        addPacket(294, PacketPatchcrumbUpdate.class);
        addPacket(295, PacketGroupInvitationAction.class);
        addPacket(296, PacketStatusUpdate.class);
        addPacket(297, PacketPingLocation.class);
        addPacket(298, PacketFocusPlayer.class);
        addPacket(299, PacketAdjHelper.class);
        addPacket(2000, PacketWaypointAdd.class);
        addPacket(2001, PacketWaypointRemove.class);
        addPacket(2100, PacketDisallowedModules.class);
        addPacket(2101, PacketCooldown.class);
        addPacket(2102, PacketUpdateWorld.class);
        addPacket(2103, PacketNotification.class);
    }

    public abstract void write(ByteBufWrapper paramByteBufWrapper) throws IOException;

    public abstract void read(ByteBufWrapper paramByteBufWrapper) throws IOException;

    public abstract void process(INetHandler paramINetHandler);
}
