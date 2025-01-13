package com.jagrosh.discordipc.entities.pipe;

import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.entities.Packet;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class WindowsPipe extends Pipe {
    private final RandomAccessFile file;

    WindowsPipe(IPCClient ipcClient, String location) {
        super(ipcClient);
        try {
            this.file = new RandomAccessFile(location, "rw");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void write(byte[] b) throws IOException {
        this.file.write(b);
    }

    public Packet read() throws IOException, JSONException {
        while (this.file.length() == 0L && this.status == PipeStatus.CONNECTED) {
            try {
                Thread.sleep(50L);
            } catch (InterruptedException interruptedException) {
            }
        }
        if (this.status == PipeStatus.DISCONNECTED)
            throw new IOException("Disconnected!");
        if (this.status == PipeStatus.CLOSED)
            return new Packet(Packet.OpCode.CLOSE, null);
        Packet.OpCode op = Packet.OpCode.values()[Integer.reverseBytes(this.file.readInt())];
        int len = Integer.reverseBytes(this.file.readInt());
        byte[] d = new byte[len];
        this.file.readFully(d);
        Packet p = new Packet(op, new JSONObject(new String(d)));
        if (this.listener != null)
            this.listener.onPacketReceived(this.ipcClient, p);
        return p;
    }

    public void close() throws IOException {
        send(Packet.OpCode.CLOSE, new JSONObject());
        this.status = PipeStatus.CLOSED;
        this.file.close();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\com\jagrosh\discordipc\entities\pipe\WindowsPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */