package co.crystaldev.client.gui.screens.override.multiplayer;

import co.crystaldev.client.network.socket.shared.PacketServerList;
import co.crystaldev.client.util.objects.partners.PartneredServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ServerListExt {
    private static final Logger logger = LogManager.getLogger();

    private final Minecraft mc;

    private final List<ServerData> servers = new LinkedList<>();

    public ServerListExt(Minecraft mcIn) {
        this.mc = mcIn;
        loadServerList();
    }

    public void loadServerList() {
        this.servers.clear();
//        for (PartneredServer server : PacketServerList.partneredServers)
//            this.servers.add(server.getData());
        try {
            NBTTagCompound nbttagcompound = CompressedStreamTools.read(new File(this.mc.mcDataDir, "servers.dat"));
            if (nbttagcompound == null)
                return;
            NBTTagList nbttaglist = nbttagcompound.getTagList("servers", 10);
            for (int i = 0; i < nbttaglist.tagCount(); i++)
                this.servers.add(ServerData.getServerDataFromNBTCompound(nbttaglist.getCompoundTagAt(i)));
        } catch (Exception exception) {
            logger.error("Couldn't load server list", exception);
        }
    }

    public void saveServerList() {
        try {
            NBTTagList nbttaglist = new NBTTagList();
            for (ServerData serverdata : this.servers) {
//                if (serverdata instanceof co.crystaldev.client.util.objects.partners.PartneredServerData)
//                    continue;
                nbttaglist.appendTag((NBTBase) serverdata.getNBTCompound());
            }
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setTag("servers", (NBTBase) nbttaglist);
            CompressedStreamTools.safeWrite(nbttagcompound, new File(this.mc.mcDataDir, "servers.dat"));
        } catch (Exception exception) {
            logger.error("Couldn't save server list", exception);
        }
    }

    public ServerData getServerData(int index) {
        return this.servers.get(index);
    }

    public void removeServerData(int index) {
        this.servers.remove(index);
    }

    public void addServerData(ServerData server) {
        this.servers.add(server);
    }

    public int countServers() {
        return this.servers.size();
    }

    public void swapServers(int p_78857_1_, int p_78857_2_) {
        ServerData serverdata = getServerData(p_78857_1_);
        ServerData serverdata1 = getServerData(p_78857_2_);
//        if (serverdata instanceof co.crystaldev.client.util.objects.partners.PartneredServerData || serverdata1 instanceof co.crystaldev.client.util.objects.partners.PartneredServerData)
//            return;
        this.servers.set(p_78857_1_, serverdata1);
        this.servers.set(p_78857_2_, serverdata);
        saveServerList();
    }

    public void set(int index, ServerData server) {
        this.servers.set(index, server);
    }

    public static void func_147414_b(ServerData p_147414_0_) {
        ServerListExt serverlist = new ServerListExt(Minecraft.getMinecraft());
        serverlist.loadServerList();
        for (int i = 0; i < serverlist.countServers(); i++) {
            ServerData serverdata = serverlist.getServerData(i);
            if (serverdata.serverName.equals(p_147414_0_.serverName) && serverdata.serverIP.equals(p_147414_0_.serverIP)) {
                serverlist.set(i, p_147414_0_);
                break;
            }
        }
        serverlist.saveServerList();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\co\crystaldev\client\gui\screens\override\multiplayer\ServerListExt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */