package org.newsclub.net.unix;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

final class NativeUnixSocket {
    private static boolean loaded = false;

    private NativeUnixSocket() {
        throw new UnsupportedOperationException("No instances");
    }

    static {
        NativeLibraryLoader nll = new NativeLibraryLoader();
        try {
            nll.loadLibrary();
            nll.close();
        } catch (Throwable throwable) {
            try {
                nll.close();
            } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
            }
            throw throwable;
        }
        loaded = true;
    }

    static boolean isLoaded() {
        return loaded;
    }

    static void checkSupported() {
    }

    static void setPort1(AFUNIXSocketAddress addr, int port) throws IOException {
        if (port < 0)
            throw new IllegalArgumentException("port out of range:" + port);
        try {
            setPort(addr, port);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Could not set port", e);
        }
    }

    static native void init() throws Exception;

    static native void destroy() throws Exception;

    static native int capabilities();

    static native long bind(byte[] paramArrayOfbyte, FileDescriptor paramFileDescriptor, int paramInt) throws IOException;

    static native void listen(FileDescriptor paramFileDescriptor, int paramInt) throws IOException;

    static native void accept(byte[] paramArrayOfbyte, FileDescriptor paramFileDescriptor1, FileDescriptor paramFileDescriptor2, long paramLong, int paramInt) throws IOException;

    static native void connect(byte[] paramArrayOfbyte, FileDescriptor paramFileDescriptor, long paramLong) throws IOException;

    static native int read(AFUNIXSocketImpl paramAFUNIXSocketImpl, FileDescriptor paramFileDescriptor, byte[] paramArrayOfbyte, int paramInt1, int paramInt2, ByteBuffer paramByteBuffer) throws IOException;

    static native int write(AFUNIXSocketImpl paramAFUNIXSocketImpl, FileDescriptor paramFileDescriptor, byte[] paramArrayOfbyte, int paramInt1, int paramInt2, int[] paramArrayOfint) throws IOException;

    static native void close(FileDescriptor paramFileDescriptor) throws IOException;

    static native void shutdown(FileDescriptor paramFileDescriptor, int paramInt) throws IOException;

    static native int getSocketOptionInt(FileDescriptor paramFileDescriptor, int paramInt) throws IOException;

    static native void setSocketOptionInt(FileDescriptor paramFileDescriptor, int paramInt1, int paramInt2) throws IOException;

    static native int available(FileDescriptor paramFileDescriptor) throws IOException;

    static native AFUNIXSocketCredentials peerCredentials(FileDescriptor paramFileDescriptor, AFUNIXSocketCredentials paramAFUNIXSocketCredentials) throws IOException;

    static native void initServerImpl(AFUNIXServerSocket paramAFUNIXServerSocket, AFUNIXSocketImpl paramAFUNIXSocketImpl) throws IOException;

    static native void setCreated(AFUNIXSocket paramAFUNIXSocket);

    static native void setConnected(AFUNIXSocket paramAFUNIXSocket);

    static native void setBound(AFUNIXSocket paramAFUNIXSocket);

    static native void setCreatedServer(AFUNIXServerSocket paramAFUNIXServerSocket);

    static native void setBoundServer(AFUNIXServerSocket paramAFUNIXServerSocket);

    static native void setPort(AFUNIXSocketAddress paramAFUNIXSocketAddress, int paramInt);

    static native void initFD(FileDescriptor paramFileDescriptor, int paramInt) throws IOException;

    static native int getFD(FileDescriptor paramFileDescriptor) throws IOException;

    static native void attachCloseable(FileDescriptor paramFileDescriptor, Closeable paramCloseable);

    static native int maxAddressLength();

    static native Socket currentRMISocket();
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\org\newsclub\ne\\unix\NativeUnixSocket.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */