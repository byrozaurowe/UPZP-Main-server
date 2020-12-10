package mainServer;

import com.google.flatbuffers.FlatBufferBuilder;
import mainServer.schemas.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

public class Server implements Runnable {
    private static Selector selector = null;
    public ClientsCoordinator clientsCoordinator;
    public  void run() {
        clientsCoordinator = new ClientsCoordinator();
        ServerSocketChannel socket;
        try {
            selector = Selector.open();
            // We have to set connection host, port and non-blocking mode
            socket = ServerSocketChannel.open();
            ServerSocket serverSocket = socket.socket();
            serverSocket.bind(new InetSocketAddress(4444));
            socket.configureBlocking(false);
            int ops = socket.validOps();
            socket.register(selector, ops, null);
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return;
        }
        while (true) {
            try {
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
            } catch (WrongPacketException e) {
                System.out.println(e.getMessage());

                continue;
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            finally {
                selector.selectedKeys().clear();
            }
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

    private void handleRead(SelectionKey key) throws WrongPacketException, IOException {

        // create a ServerSocketChannel to read the request
        SocketChannel client = (SocketChannel) key.channel();
        byte[] data = new byte[1024];

        client.read(ByteBuffer.wrap(data));
        byte[] a = new byte[1024];
        Arrays.fill(a,(byte)0);
        if(Arrays.equals(a, data)) {
            //clientsCoordinator.disconnect(client.socket());
            System.out.println("Rozłączono klienta " + client.socket().getInetAddress());
            client.socket().close();
        }
        else {
            System.out.println("Reading...");
            byte[] packet = PacketHandler.handleData(data, client.socket()); // powinno zwrócic odpowiedź
            client.write(ByteBuffer.wrap(packet));
        }
    }
}
