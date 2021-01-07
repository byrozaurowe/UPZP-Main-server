package mainServer;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/** Klasa główna serwera */
public class Server implements Runnable {
    private static Selector selector = null;
    public ClientsCoordinator clientsCoordinator;
    public WaitingRoomsCoordinator waitingRoomsCoordinator;
    public  void run() {
        clientsCoordinator = new ClientsCoordinator();
        waitingRoomsCoordinator = new WaitingRoomsCoordinator();
        DatabaseHandler.getInstance();
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
        try {
            test();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> i = selectedKeys.iterator();
                while (i.hasNext()) {
                    SelectionKey key = i.next();
                    if (key.isAcceptable()) {
                        handleAccept(socket, key);
                    } else if (key.isReadable()) {
                        handleRead(key);
                    }
                    i.remove();
                }
            } catch (WrongPacketException e) {
                System.out.println(e.getMessage());
            } catch (IOException | SQLException e) {
                System.out.println(e.getMessage());
            }
            finally {
                selector.selectedKeys().clear();
            }
        }
    }

    void test() throws IOException, SQLException {
        WaitingRoom w = new WaitingRoom("Wrocław", new Client("Wojtek", InetAddress.getByName("127.0.0.1"), new Socket()), 20);
        waitingRoomsCoordinator.addWaitingRoom(w);
        WaitingRoom wr = new WaitingRoom("Wrochrław", new Client("Worjtek", InetAddress.getByName("127.0.1.1"), new Socket()), 20);
        waitingRoomsCoordinator.addWaitingRoom(wr);
        System.out.println(DatabaseHandler.getInstance().loggIn("player1", "player1"));

    }

    /** Obsługa próby podłączenia klienta pod serwer */
    private void handleAccept(ServerSocketChannel mySocket, SelectionKey key) throws IOException {
        System.out.println("Connection Accepted...");
        // Accept the connection and set non-blocking mode
        SocketChannel client = mySocket.accept();
        client.configureBlocking(false);
        clientsCoordinator.connect(client.socket());
        // Register that client is reading this channel
        client.register(selector, SelectionKey.OP_READ);
    }

    /** Obsługa wiadomości otrzymanej od klienta */
    private void handleRead(SelectionKey key) throws WrongPacketException, IOException, SQLException {
        // create a ServerSocketChannel to read the request
        SocketChannel client = (SocketChannel) key.channel();
        byte[] data = new byte[1024];

        client.read(ByteBuffer.wrap(data));
        byte[] a = new byte[1024];
        Arrays.fill(a, (byte)0);
        if(Arrays.equals(a, data)) {
            clientsCoordinator.disconnect(client.socket());
            //System.out.println("Rozłączono klienta " + client.socket().getInetAddress());
            //client.socket().close();
        }
        else {
            System.out.println("Reading...");
            PacketHandler.handleData(data, client);
        }
    }
}
