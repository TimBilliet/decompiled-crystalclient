package com.jagrosh.discordipc.entities;

import org.json.JSONObject;

import java.nio.ByteBuffer;

public class Packet {
    private final OpCode op;

    private final JSONObject data;

    public Packet(OpCode op, JSONObject data) {
        this.op = op;
        this.data = data;
    }

    public byte[] toBytes() {
        byte[] d = this.data.toString().getBytes();
        ByteBuffer packet = ByteBuffer.allocate(d.length + 8);
        packet.putInt(Integer.reverseBytes(this.op.ordinal()));
        packet.putInt(Integer.reverseBytes(d.length));
        packet.put(d);
        return packet.array();
    }

    public OpCode getOp() {
        return this.op;
    }

    public JSONObject getJson() {
        return this.data;
    }

    public String toString() {
        return "Pkt:" + getOp() + getJson().toString();
    }

    public enum OpCode {
        HANDSHAKE, FRAME, CLOSE, PING, PONG;
    }
}
