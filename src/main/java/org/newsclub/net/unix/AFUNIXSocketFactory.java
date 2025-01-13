package org.newsclub.net.unix;

import javax.net.SocketFactory;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.Objects;

public abstract class AFUNIXSocketFactory extends SocketFactory {
    protected abstract AFUNIXSocketAddress addressFromHost(String paramString, int paramInt) throws IOException;

    protected boolean isHostnameSupported(String host) {
        return (host != null);
    }

    protected boolean isInetAddressSupported(InetAddress address) {
        return (address != null && isHostnameSupported(address.getHostName()));
    }

    public Socket createSocket() throws IOException {
        return AFUNIXSocket.newInstance(this);
    }

    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        if (!isHostnameSupported(host))
            throw new UnknownHostException();
        if (port < 0)
            throw new IllegalArgumentException("Illegal port");
        AFUNIXSocketAddress socketAddress = addressFromHost(host, port);
        return AFUNIXSocket.connectTo(socketAddress);
    }

    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        if (!isHostnameSupported(host))
            throw new UnknownHostException();
        if (localPort < 0)
            throw new IllegalArgumentException("Illegal local port");
        return createSocket(host, port);
    }

    public Socket createSocket(InetAddress address, int port) throws IOException {
        if (!isInetAddressSupported(address))
            throw new UnknownHostException();
        String hostname = address.getHostName();
        if (!isHostnameSupported(hostname))
            throw new UnknownHostException();
        return createSocket(hostname, port);
    }

    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        if (!isInetAddressSupported(address))
            throw new UnknownHostException();
        Objects.requireNonNull(localAddress, "Local address was null");
        if (localPort < 0)
            throw new IllegalArgumentException("Illegal local port");
        return createSocket(address, port);
    }

    private static abstract class DefaultSocketHostnameSocketFactory extends AFUNIXSocketFactory {
        private static final String PROP_SOCKET_HOSTNAME = "org.newsclub.net.unix.socket.hostname";

        private DefaultSocketHostnameSocketFactory() {
        }

        protected final boolean isHostnameSupported(String host) {
            return getDefaultSocketHostname().equals(host);
        }

        private static String getDefaultSocketHostname() {
            return System.getProperty("org.newsclub.net.unix.socket.hostname", "localhost");
        }
    }

    public static final class FactoryArg extends DefaultSocketHostnameSocketFactory {
        private final File socketFile;

        public FactoryArg(String socketPath) {
            Objects.requireNonNull(socketPath, "Socket path was null");
            this.socketFile = new File(socketPath);
        }

        public FactoryArg(File file) {
            Objects.requireNonNull(file, "File was null");
            this.socketFile = file;
        }

        protected AFUNIXSocketAddress addressFromHost(String host, int port) throws IOException {
            return new AFUNIXSocketAddress(this.socketFile, port);
        }
    }

    public static final class SystemProperty extends DefaultSocketHostnameSocketFactory {
        private static final String PROP_SOCKET_DEFAULT = "org.newsclub.net.unix.socket.default";

        protected AFUNIXSocketAddress addressFromHost(String host, int port) throws IOException {
            String path = System.getProperty("org.newsclub.net.unix.socket.default");
            if (path == null || path.isEmpty())
                throw new IllegalStateException("Property not configured: org.newsclub.net.unix.socket.default");
            File socketFile = new File(path);
            return new AFUNIXSocketAddress(socketFile, port);
        }
    }

    public static final class URIScheme extends AFUNIXSocketFactory {
        private static final String FILE_SCHEME_PREFIX = "file://";

        private static final String FILE_SCHEME_PREFIX_ENCODED = "file%";

        private static final String FILE_SCHEME_LOCALHOST = "localhost";

        private static String stripBrackets(String host) {
            if (host.startsWith("["))
                if (host.endsWith("]")) {
                    host = host.substring(1, host.length() - 1);
                } else {
                    host = host.substring(1);
                }
            return host;
        }

        protected boolean isHostnameSupported(String host) {
            host = stripBrackets(host);
            return (host.startsWith("file://") || host.startsWith("file%"));
        }

        protected AFUNIXSocketAddress addressFromHost(String host, int port) throws IOException {
            host = stripBrackets(host);
            if (host.startsWith("file%"))
                try {
                    host = URLDecoder.decode(host, "UTF-8");
                } catch (Exception e) {
                    throw (UnknownHostException) (new UnknownHostException()).initCause(e);
                }
            if (!host.startsWith("file://"))
                throw new UnknownHostException();
            String path = host.substring("file://".length());
            if (path.isEmpty())
                throw new UnknownHostException();
            if (path.startsWith("localhost"))
                path = path.substring("localhost".length());
            if (!path.startsWith("/"))
                throw new UnknownHostException();
            File socketFile = new File(path);
            return new AFUNIXSocketAddress(socketFile, port);
        }
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\org\newsclub\ne\\unix\AFUNIXSocketFactory.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */