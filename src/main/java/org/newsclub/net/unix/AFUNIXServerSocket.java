package org.newsclub.net.unix;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.SocketException;

public class AFUNIXServerSocket extends ServerSocket {
    private final AFUNIXSocketImpl implementation;

    private AFUNIXSocketAddress boundEndpoint;

    private final Closeables closeables = new Closeables();

    protected AFUNIXServerSocket() throws IOException {
        setReuseAddress(true);
        this.implementation = new AFUNIXSocketImpl();
        NativeUnixSocket.initServerImpl(this, this.implementation);
        NativeUnixSocket.setCreatedServer(this);
    }

    public static AFUNIXServerSocket newInstance() throws IOException {
        return new AFUNIXServerSocket();
    }

    public static AFUNIXServerSocket bindOn(AFUNIXSocketAddress addr) throws IOException {
        AFUNIXServerSocket socket = newInstance();
        socket.bind(addr);
        return socket;
    }

    public static AFUNIXServerSocket forceBindOn(final AFUNIXSocketAddress forceAddr) throws IOException {
        return new AFUNIXServerSocket() {
            public void bind(SocketAddress ignored, int backlog) throws IOException {
                super.bind(forceAddr, backlog);
            }
        };
    }

    public void bind(SocketAddress endpoint, int backlog) throws IOException {
        if (isClosed())
            throw new SocketException("Socket is closed");
        if (isBound())
            throw new SocketException("Already bound");
        if (!(endpoint instanceof AFUNIXSocketAddress))
            throw new IOException("Can only bind to endpoints of type " + AFUNIXSocketAddress.class
                    .getName());
        this.implementation.bind(endpoint, getReuseAddress() ? -1 : 0);
        this.boundEndpoint = (AFUNIXSocketAddress) endpoint;
        this.implementation.listen(backlog);
    }

    public boolean isBound() {
        return (this.boundEndpoint != null);
    }

    public boolean isClosed() {
        return (super.isClosed() || (isBound() && !this.implementation.getFD().valid()));
    }

    public AFUNIXSocket accept() throws IOException {
        if (isClosed())
            throw new SocketException("Socket is closed");
        AFUNIXSocket as = newSocketInstance();
        this.implementation.accept(as.impl);
        as.addr = this.boundEndpoint;
        NativeUnixSocket.setConnected(as);
        return as;
    }

    protected AFUNIXSocket newSocketInstance() throws IOException {
        return AFUNIXSocket.newInstance();
    }

    public String toString() {
        if (!isBound())
            return "AFUNIXServerSocket[unbound]";
        return "AFUNIXServerSocket[" + this.boundEndpoint.toString() + "]";
    }

    public synchronized void close() throws IOException {
        if (isClosed())
            return;
        IOException superException = null;
        try {
            super.close();
        } catch (IOException e) {
            superException = e;
        }
        if (this.implementation != null)
            try {
                this.implementation.close();
            } catch (IOException e) {
                if (superException == null) {
                    superException = e;
                } else {
                    superException.addSuppressed(e);
                }
            }
        this.closeables.close(superException);
    }

    public void addCloseable(Closeable closeable) {
        this.closeables.add(closeable);
    }

    public void removeCloseable(Closeable closeable) {
        this.closeables.remove(closeable);
    }

    public static boolean isSupported() {
        return NativeUnixSocket.isLoaded();
    }

    public SocketAddress getLocalSocketAddress() {
        return this.boundEndpoint;
    }

    public int getLocalPort() {
        if (this.boundEndpoint == null)
            return -1;
        return this.boundEndpoint.getPort();
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\org\newsclub\ne\\unix\AFUNIXServerSocket.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */