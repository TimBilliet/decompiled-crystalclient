package com.github.timmekeclient.network.socket.client;

import com.github.timmekeclient.Client;
import com.github.timmekeclient.Reference;
import com.github.timmekeclient.network.ByteBufWrapper;
import com.github.timmekeclient.network.INetHandler;
import com.github.timmekeclient.network.Packet;
import com.google.gson.JsonObject;

import java.io.IOException;

public class PacketServerConnection extends Packet {
    public Type connectionType;

    public PacketServerConnection(Type connectionType) {
        this.connectionType = connectionType;
    }

    public void write(ByteBufWrapper out) throws IOException {
        JsonObject obj = new JsonObject();
        obj.addProperty("server", (this.connectionType == Type.LEAVE) ? "null" :
                Client.formatConnectedServerIp());
        obj.addProperty("connectionType", this.connectionType.toString());
        out.writeString(Reference.GSON.toJson(obj, JsonObject.class));
    }

    public void read(ByteBufWrapper in) throws IOException {
    }

    public void process(INetHandler handler) {
    }

    public enum Type {
        JOIN("JOIN"),
        LEAVE("LEAVE");

        private final String serializationString;

        Type(String serializationString) {
            this.serializationString = serializationString;
        }

        public String toString() {
            return this.serializationString;
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\client\PacketServerConnection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */