package id.thony.android.quranlite.utils.network;

import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class TLSSocketFactory extends SSLSocketFactory {
    private final SSLSocketFactory internalSSLSocketFactory;

    public TLSSocketFactory() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        internalSSLSocketFactory = context.getSocketFactory();
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return internalSSLSocketFactory.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return internalSSLSocketFactory.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket() throws IOException {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket());
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return enableTLSOnSocket(internalSSLSocketFactory.createSocket(address, port, localAddress, localPort));
    }

    private Socket enableTLSOnSocket(Socket socket) {
        if (socket instanceof SSLSocket) {
            SSLSocket sslSocket = (SSLSocket) socket;

            List<String> protocolsToEnable = new ArrayList<>();
            String[] supportedProtocols = sslSocket.getSupportedProtocols();

            // Prioritize TLSv1.3 (available API 29+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                for (String protocol : supportedProtocols) {
                    if (protocol.equals("TLSv1.3")) {
                        protocolsToEnable.add(protocol);
                        break;
                    }
                }
            }
            // Add TLSv1.2 (available widely, default enabled on API 20+)
            for (String protocol : supportedProtocols) {
                if (protocol.equals("TLSv1.2")) {
                    if (!protocolsToEnable.contains("TLSv1.2")) { // Avoid duplicates
                        protocolsToEnable.add(protocol);
                    }
                    break;
                }
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
                boolean strongerProtocolFound = false;
                for (String enabled : protocolsToEnable) {
                    // Check if TLSv1.3 or TLSv1.2 already added
                    if ("TLSv1.3".equals(enabled) || "TLSv1.2".equals(enabled)) {
                        strongerProtocolFound = true;
                        break;
                    }
                }
                if (!strongerProtocolFound) {
                    for (String protocol : supportedProtocols) {
                        if ("TLSv1.1".equals(protocol)) {
                            protocolsToEnable.add(protocol);
                            break;
                        }
                    }
                }
            }

            if (!protocolsToEnable.isEmpty()) {
                try {
                    sslSocket.setEnabledProtocols(protocolsToEnable.toArray(new String[0]));
                } catch (IllegalArgumentException unused) {
                    // If setting fails, the socket will use its default enabled protocols,
                    // which are likely already good on modern Android.
                }
            }
        }
        return socket;
    }
}
