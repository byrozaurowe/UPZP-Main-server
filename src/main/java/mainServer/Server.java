package mainServer;

import mainServer.TestData.Client;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server implements Runnable {
    private static Selector selector = null;
    public ClientsCoordinator clientsCoordinator;
    public  void run() {
        clientsCoordinator = new ClientsCoordinator();
        try {
            selector = Selector.open();
            // We have to set connection host, port and non-blocking mode
            ServerSocketChannel socket = ServerSocketChannel.open();
            ServerSocket serverSocket = socket.socket();
            serverSocket.bind(new InetSocketAddress(4444));
            socket.configureBlocking(false);
            int ops = socket.validOps();
            socket.register(selector, ops, null);
            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> i = selectedKeys.iterator();
                while (i.hasNext()) {
                    SelectionKey key = i.next();
                    if (key.isAcceptable()) {
                    // New client has been accepted
                        handleAccept(socket, key);
                    } else if (key.isReadable()) {
                    // We can run non-blocking operation READ on our client
                        handleRead(key);
                    }
                    i.remove();
                }
            }
        } catch (IOException e) {
                e.printStackTrace();
        }
    }

    private void handleAccept(ServerSocketChannel mySocket, SelectionKey key) throws IOException {
        System.out.println("Connection Accepted...");
        // Accept the connection and set non-blocking mode
        SocketChannel client = mySocket.accept();
        client.configureBlocking(false);
        clientsCoordinator.connect(client.socket());
        // Register that client is reading this channel
        client.register(selector, SelectionKey.OP_READ);
    }

    private void handleRead(SelectionKey key) throws IOException {
        System.out.println("Reading...");
        // create a ServerSocketChannel to read the request
        SocketChannel client = (SocketChannel) key.channel();

        byte[] data = new byte[1024];
        client.read(ByteBuffer.wrap(data));
        Client clientobj = Client.getRootAsClient(ByteBuffer.wrap(data));

        String name = clientobj.name();
        String ip = clientobj.ipAddress();
        clientsCoordinator.verifyLoggingClient(client.socket(), name, ip, "");
    }
}
