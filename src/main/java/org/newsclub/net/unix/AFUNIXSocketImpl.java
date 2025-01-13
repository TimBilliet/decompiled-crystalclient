package org.newsclub.net.unix;

import java.io.*;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

class AFUNIXSocketImpl extends SocketImpl {
    private static final int SHUT_RD = 0;

    private static final int SHUT_WR = 1;

    private static final int SHUT_RD_WR = 2;

    private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0);

    private AFUNIXSocketAddress socketAddress;

    private long inode = -1L;

    private volatile boolean closed = false;

    private volatile boolean bound = false;

    private boolean connected = false;

    private volatile boolean closedInputStream = false;

    private volatile boolean closedOutputStream = false;

    private final AFUNIXInputStream in = newInputStream();

    private final AFUNIXOutputStream out = newOutputStream();

    private final AtomicInteger pendingAccepts = new AtomicInteger(0);

    private boolean reuseAddr = true;

    private ByteBuffer ancillaryReceiveBuffer = EMPTY_BUFFER;

    private final List<FileDescriptor[]> receivedFileDescriptors = (List) Collections.synchronizedList(new LinkedList<>());

    private int[] pendingFileDescriptors = null;

    private final Map<FileDescriptor, Integer> closeableFileDescriptors = Collections.synchronizedMap(new HashMap<>());

    private int timeout = 0;

    protected AFUNIXSocketImpl() {
        this.fd = new FileDescriptor();
        this.address = InetAddress.getLoopbackAddress();
    }

    protected AFUNIXInputStream newInputStream() {
        return new AFUNIXInputStream();
    }

    protected AFUNIXOutputStream newOutputStream() {
        return new AFUNIXOutputStream();
    }

    FileDescriptor getFD() {
        return this.fd;
    }

    protected final void finalize() {
        try {
            close();
        } catch (Throwable throwable) {
        }
        try {
            synchronized (this.closeableFileDescriptors) {
                for (FileDescriptor fd : this.closeableFileDescriptors.keySet())
                    NativeUnixSocket.close(fd);
            }
        } catch (Throwable throwable) {
        }
    }

    protected void accept(SocketImpl socket) throws IOException {
        FileDescriptor fdesc = validFdOrException();
        AFUNIXSocketImpl si = (AFUNIXSocketImpl) socket;
        try {
            if (this.pendingAccepts.incrementAndGet() >= Integer.MAX_VALUE)
                throw new SocketException("Too many pending accepts");
            if (!this.bound || this.closed)
                throw new SocketException("Socket is closed");
            NativeUnixSocket.accept(this.socketAddress.getBytes(), fdesc, si.fd, this.inode, this.timeout);
            if (!this.bound || this.closed) {
                try {
                    NativeUnixSocket.shutdown(si.fd, 2);
                } catch (Exception exception) {
                }
                try {
                    NativeUnixSocket.close(si.fd);
                } catch (Exception exception) {
                }
                throw new SocketException("Socket is closed");
            }
        } finally {
            this.pendingAccepts.decrementAndGet();
        }
        si.socketAddress = this.socketAddress;
        si.connected = true;
        si.port = this.socketAddress.getPort();
        si.address = this.socketAddress.getAddress();
    }

    protected int available() throws IOException {
        FileDescriptor fdesc = validFdOrException();
        return NativeUnixSocket.available(fdesc);
    }

    protected void bind(SocketAddress addr) throws IOException {
        bind(addr, -1);
    }

    protected void bind(SocketAddress addr, int options) throws IOException {
        if (!(addr instanceof AFUNIXSocketAddress))
            throw new SocketException("Cannot bind to this type of address: " + addr.getClass());
        this.socketAddress = (AFUNIXSocketAddress) addr;
        this.address = this.socketAddress.getAddress();
        this.inode = NativeUnixSocket.bind(this.socketAddress.getBytes(), this.fd, options);
        validFdOrException();
        this.bound = true;
        this.localport = this.socketAddress.getPort();
    }

    protected void bind(InetAddress host, int port) throws IOException {
        throw new SocketException("Cannot bind to this type of address: " + InetAddress.class);
    }

    private void checkClose() throws IOException {
        if (this.closedInputStream && this.closedOutputStream)
            close();
    }

    private void unblockAccepts() {
        while (this.pendingAccepts.get() > 0) {
            try {
                FileDescriptor tmpFd = new FileDescriptor();
                try {
                    NativeUnixSocket.connect(this.socketAddress.getBytes(), tmpFd, this.inode);
                } catch (IOException e) {
                    return;
                }
                try {
                    NativeUnixSocket.shutdown(tmpFd, 2);
                } catch (Exception exception) {
                }
                try {
                    NativeUnixSocket.close(tmpFd);
                } catch (Exception exception) {
                }
            } catch (Exception exception) {
            }
        }
    }

    protected final synchronized void close() throws IOException {
        boolean wasBound = this.bound;
        this.bound = false;
        FileDescriptor fdesc = validFd();
        if (fdesc != null) {
            NativeUnixSocket.shutdown(fdesc, 2);
            this.closed = true;
            if (wasBound && this.socketAddress != null && this.socketAddress.getBytes() != null && this.inode >= 0L)
                unblockAccepts();
            NativeUnixSocket.close(fdesc);
        }
        this.closed = true;
    }

    protected void connect(String host, int port) throws IOException {
        throw new SocketException("Cannot bind to this type of address: " + InetAddress.class);
    }

    protected void connect(InetAddress address, int port) throws IOException {
        throw new SocketException("Cannot bind to this type of address: " + InetAddress.class);
    }

    protected void connect(SocketAddress addr, int connectTimeout) throws IOException {
        if (!(addr instanceof AFUNIXSocketAddress))
            throw new SocketException("Cannot bind to this type of address: " + addr.getClass());
        this.socketAddress = (AFUNIXSocketAddress) addr;
        NativeUnixSocket.connect(this.socketAddress.getBytes(), this.fd, -1L);
        validFdOrException();
        this.address = this.socketAddress.getAddress();
        this.port = this.socketAddress.getPort();
        this.localport = 0;
        this.connected = true;
    }

    protected void create(boolean stream) throws IOException {
    }

    protected InputStream getInputStream() throws IOException {
        if (!this.connected && !this.bound)
            throw new IOException("Not connected/not bound");
        validFdOrException();
        return this.in;
    }

    protected OutputStream getOutputStream() throws IOException {
        if (!this.connected && !this.bound)
            throw new IOException("Not connected/not bound");
        validFdOrException();
        return this.out;
    }

    protected void listen(int backlog) throws IOException {
        FileDescriptor fdesc = validFdOrException();
        if (backlog <= 0)
            backlog = 50;
        NativeUnixSocket.listen(fdesc, backlog);
    }

    protected void sendUrgentData(int data) throws IOException {
        FileDescriptor fdesc = validFdOrException();
        NativeUnixSocket.write(this, fdesc, new byte[]{(byte) (data & 0xFF)}, 0, 1, this.pendingFileDescriptors);
    }

    private class AFUNIXInputStream extends InputStream {
        private volatile boolean streamClosed = false;

        private boolean eofReached = false;

        public int read(byte[] buf, int off, int len) throws IOException {
            if (this.streamClosed)
                throw new IOException("This InputStream has already been closed.");
            FileDescriptor fdesc = AFUNIXSocketImpl.this.validFdOrException();
            if (len == 0)
                return 0;
            if (off < 0 || len < 0 || len > buf.length - off)
                throw new IndexOutOfBoundsException();
            return NativeUnixSocket.read(AFUNIXSocketImpl.this, fdesc, buf, off, len, AFUNIXSocketImpl.this
                    .ancillaryReceiveBuffer);
        }

        public int read() throws IOException {
            if (this.eofReached)
                return -1;
            byte[] buf1 = new byte[1];
            int numRead = read(buf1, 0, 1);
            if (numRead <= 0) {
                this.eofReached = true;
                return -1;
            }
            return buf1[0] & 0xFF;
        }

        public synchronized void close() throws IOException {
            this.streamClosed = true;
            FileDescriptor fdesc = AFUNIXSocketImpl.this.validFd();
            if (fdesc != null)
                NativeUnixSocket.shutdown(fdesc, 0);
            AFUNIXSocketImpl.this.closedInputStream = true;
            AFUNIXSocketImpl.this.checkClose();
        }

        public int available() throws IOException {
            if (this.streamClosed)
                throw new IOException("This InputStream has already been closed.");
            FileDescriptor fdesc = AFUNIXSocketImpl.this.validFdOrException();
            return NativeUnixSocket.available(fdesc);
        }

        private AFUNIXInputStream() {
        }
    }

    private class AFUNIXOutputStream extends OutputStream {
        private volatile boolean streamClosed = false;

        public void write(int oneByte) throws IOException {
            byte[] buf1 = {(byte) oneByte};
            write(buf1, 0, 1);
        }

        public void write(byte[] buf, int off, int len) throws IOException {
            if (this.streamClosed)
                throw new SocketException("This OutputStream has already been closed.");
            if (len < 0 || off < 0 || len > buf.length - off)
                throw new IndexOutOfBoundsException();
            FileDescriptor fdesc = AFUNIXSocketImpl.this.validFdOrException();
            int writtenTotal = 0;
            while (len > 0) {
                if (Thread.interrupted()) {
                    InterruptedIOException ex = new InterruptedIOException("Thread interrupted during write");
                    ex.bytesTransferred = writtenTotal;
                    Thread.currentThread().interrupt();
                    throw ex;
                }
                int written = NativeUnixSocket.write(AFUNIXSocketImpl.this, fdesc, buf, off, len, AFUNIXSocketImpl.this
                        .pendingFileDescriptors);
                if (written < 0)
                    throw new IOException("Unspecific error while writing");
                len -= written;
                off += written;
                writtenTotal += written;
            }
        }

        public synchronized void close() throws IOException {
            if (this.streamClosed)
                return;
            this.streamClosed = true;
            FileDescriptor fdesc = AFUNIXSocketImpl.this.validFd();
            if (fdesc != null)
                NativeUnixSocket.shutdown(fdesc, 1);
            AFUNIXSocketImpl.this.closedOutputStream = true;
            AFUNIXSocketImpl.this.checkClose();
        }

        private AFUNIXOutputStream() {
        }
    }

    private FileDescriptor validFdOrException() throws SocketException {
        FileDescriptor fdesc = validFd();
        if (fdesc == null)
            throw new SocketException("Not open");
        return fdesc;
    }

    private synchronized FileDescriptor validFd() {
        if (this.closed)
            return null;
        FileDescriptor descriptor = this.fd;
        if (descriptor != null &&
                descriptor.valid())
            return descriptor;
        return null;
    }

    public String toString() {
        return super.toString() + "[fd=" + this.fd + "; addr=" + this.socketAddress + "; connected=" + this.connected + "; bound=" + this.bound + "]";
    }

    private static int expectInteger(Object value) throws SocketException {
        try {
            return ((Integer) value).intValue();
        } catch (ClassCastException e) {
            throw (SocketException) (new SocketException("Unsupported value: " + value)).initCause(e);
        } catch (NullPointerException e) {
            throw (SocketException) (new SocketException("Value must not be null")).initCause(e);
        }
    }

    private static int expectBoolean(Object value) throws SocketException {
        try {
            return ((Boolean) value).booleanValue() ? 1 : 0;
        } catch (ClassCastException e) {
            throw (SocketException) (new SocketException("Unsupported value: " + value)).initCause(e);
        } catch (NullPointerException e) {
            throw (SocketException) (new SocketException("Value must not be null")).initCause(e);
        }
    }

    public Object getOption(int optID) throws SocketException {
        if (optID == 4)
            return Boolean.valueOf(this.reuseAddr);
        FileDescriptor fdesc = validFdOrException();
        try {
            switch (optID) {
                case 1:
                case 8:
                    return Boolean.valueOf((NativeUnixSocket.getSocketOptionInt(fdesc, optID) != 0));
                case 4102:
                    return Integer.valueOf(Math.max(this.timeout, Math.max(NativeUnixSocket.getSocketOptionInt(fdesc, 4101),
                            NativeUnixSocket.getSocketOptionInt(fdesc, 4102))));
                case 128:
                case 4097:
                case 4098:
                    return Integer.valueOf(NativeUnixSocket.getSocketOptionInt(fdesc, optID));
            }
            throw new SocketException("Unsupported option: " + optID);
        } catch (SocketException e) {
            throw e;
        } catch (Exception e) {
            throw (SocketException) (new SocketException("Error while getting option")).initCause(e);
        }
    }

    public void setOption(int optID, Object value) throws SocketException {
        if (optID == 4) {
            this.reuseAddr = !(expectBoolean(value) == 0);
            return;
        }
        FileDescriptor fdesc = validFdOrException();
        try {
            switch (optID) {
                case 128:
                    if (value instanceof Boolean) {
                        boolean b = ((Boolean) value).booleanValue();
                        if (b)
                            throw new SocketException("Only accepting Boolean.FALSE here");
                        NativeUnixSocket.setSocketOptionInt(fdesc, optID, -1);
                        return;
                    }
                    NativeUnixSocket.setSocketOptionInt(fdesc, optID, expectInteger(value));
                    return;
                case 4102:
                    this.timeout = expectInteger(value);
                    NativeUnixSocket.setSocketOptionInt(fdesc, 4101, this.timeout);
                    NativeUnixSocket.setSocketOptionInt(fdesc, 4102, this.timeout);
                    return;
                case 4097:
                case 4098:
                    NativeUnixSocket.setSocketOptionInt(fdesc, optID, expectInteger(value));
                    return;
                case 1:
                case 8:
                    NativeUnixSocket.setSocketOptionInt(fdesc, optID, expectBoolean(value));
                    return;
            }
            throw new SocketException("Unsupported option: " + optID);
        } catch (SocketException e) {
            throw e;
        } catch (Exception e) {
            throw (SocketException) (new SocketException("Error while setting option")).initCause(e);
        }
    }

    protected void shutdownInput() throws IOException {
        FileDescriptor fdesc = validFd();
        if (fdesc != null)
            NativeUnixSocket.shutdown(fdesc, 0);
    }

    protected void shutdownOutput() throws IOException {
        FileDescriptor fdesc = validFd();
        if (fdesc != null)
            NativeUnixSocket.shutdown(fdesc, 1);
    }

    static final class Lenient extends AFUNIXSocketImpl {
        public void setOption(int optID, Object value) throws SocketException {
            try {
                super.setOption(optID, value);
            } catch (SocketException e) {
                switch (optID) {
                    case 1:
                        return;
                }
                throw e;
            }
        }

        public Object getOption(int optID) throws SocketException {
            try {
                return super.getOption(optID);
            } catch (SocketException e) {
                switch (optID) {
                    case 1:
                    case 8:
                        return Boolean.valueOf(false);
                }
                throw e;
            }
        }
    }

    AFUNIXSocketCredentials getPeerCredentials() throws IOException {
        return NativeUnixSocket.peerCredentials(this.fd, new AFUNIXSocketCredentials());
    }

    int getAncillaryReceiveBufferSize() {
        return this.ancillaryReceiveBuffer.capacity();
    }

    void setAncillaryReceiveBufferSize(int size) {
        if (size == this.ancillaryReceiveBuffer.capacity())
            return;
        this.ancillaryReceiveBuffer = (size <= 0) ? EMPTY_BUFFER : ByteBuffer.allocateDirect(size);
    }

    public final void ensureAncillaryReceiveBufferSize(int minSize) {
        if (minSize <= 0)
            return;
        if (this.ancillaryReceiveBuffer.capacity() < minSize)
            setAncillaryReceiveBufferSize(minSize);
    }

    public final FileDescriptor[] getReceivedFileDescriptors() {
        if (this.receivedFileDescriptors.isEmpty())
            return null;
        List<FileDescriptor[]> copy = (List) new ArrayList<>(this.receivedFileDescriptors);
        if (copy.isEmpty())
            return null;
        this.receivedFileDescriptors.removeAll(copy);
        int count = 0;
        for (FileDescriptor[] fds : copy)
            count += fds.length;
        if (count == 0)
            return null;
        FileDescriptor[] oneArray = new FileDescriptor[count];
        int offset = 0;
        for (FileDescriptor[] fds : copy) {
            System.arraycopy(fds, 0, oneArray, offset, fds.length);
            offset += fds.length;
        }
        return oneArray;
    }

    public final void clearReceivedFileDescriptors() {
        this.receivedFileDescriptors.clear();
    }

    final void receiveFileDescriptors(int[] fds) throws IOException {
        if (fds == null || fds.length == 0)
            return;
        int fdsLength = fds.length;
        FileDescriptor[] descriptors = new FileDescriptor[fdsLength];
        for (int i = 0; i < fdsLength; i++) {
            final FileDescriptor fdesc = new FileDescriptor();
            NativeUnixSocket.initFD(fdesc, fds[i]);
            descriptors[i] = fdesc;
            this.closeableFileDescriptors.put(fdesc, Integer.valueOf(fds[i]));
            Closeable cleanup = new Closeable() {
                public void close() throws IOException {
                    AFUNIXSocketImpl.this.closeableFileDescriptors.remove(fdesc);
                }
            };
            NativeUnixSocket.attachCloseable(fdesc, cleanup);
        }
        this.receivedFileDescriptors.add(descriptors);
    }

    final void setOutboundFileDescriptors(int... fds) {
        this.pendingFileDescriptors = (fds == null || fds.length == 0) ? null : fds;
    }

    public final void setOutboundFileDescriptors(FileDescriptor... fdescs) throws IOException {
        if (fdescs == null || fdescs.length == 0) {
            setOutboundFileDescriptors((int[]) null);
        } else {
            int numFdescs = fdescs.length;
            int[] fds = new int[numFdescs];
            for (int i = 0; i < numFdescs; i++) {
                FileDescriptor fdesc = fdescs[i];
                fds[i] = NativeUnixSocket.getFD(fdesc);
            }
            setOutboundFileDescriptors(fds);
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\org\newsclub\ne\\unix\AFUNIXSocketImpl.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */