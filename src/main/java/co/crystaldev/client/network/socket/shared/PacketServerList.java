package co.crystaldev.client.network.socket.shared;

import co.crystaldev.client.Reference;
import co.crystaldev.client.network.ByteBufWrapper;
import co.crystaldev.client.network.INetHandler;
import co.crystaldev.client.network.Packet;
import co.crystaldev.client.util.objects.partners.PartneredServer;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;

public class PacketServerList extends Packet {
    public static LinkedList<PartneredServer> partneredServers = new LinkedList<>();

    public PacketServerList() {
        partneredServers.clear();
    }

    public void write(ByteBufWrapper out) throws IOException {
    }

    public void read(ByteBufWrapper in) throws IOException {
        partneredServers.clear();
        partneredServers.addAll((Collection<? extends PartneredServer>) Reference.GSON.fromJson(in.readString(), (new TypeToken<LinkedList<PartneredServer>>() {

        }).getType()));
        partneredServers.sort(Comparator.comparing(s -> s.name.toLowerCase()));
    }

    public void process(INetHandler handler) {
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\network\socket\shared\PacketServerList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */