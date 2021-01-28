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
    /** Zewnętrzne ip serwera głównego */
    private String serverIp;
    /** Koordynator klientów serwera */
    public ClientsCoordinator clientsCoordinator;
    /** Koordynator waiting roomów serwera */
    public WaitingRoomsCoordinator waitingRoomsCoordinator;

    /** Zwraca zewnętrzne ip serwera
     * @return zewnętrzne ip serwera głównego
     */
    public String getIp() {
        return serverIp;
    }

    /** Sprawdza zewnętrzne ip serwera */
    private void checkExternalIp() throws IOException {
        java.net.URL URL = new java.net.URL("http://checkip.amazonaws.com");
        java.net.HttpURLConnection Conn = (HttpURLConnection)URL.openConnection();
        java.io.InputStreamReader Isr = new java.io.InputStreamReader(Conn.getInputStream());
        java.io.BufferedReader Br = new java.io.BufferedReader(Isr);
        serverIp = Br.readLine();
        Conn.disconnect();
        Br.close();
        Isr.close();
    }

    public  void run() {
        clientsCoordinator = new ClientsCoordinator();
        waitingRoomsCoordinator = new WaitingRoomsCoordinator();
        DatabaseHandler.getInstance();
        ServerSocketChannel socket;
        try {
            checkExternalIp();
        } catch (IOException e) {
            System.out.println("Nie mogłem odczytać swojego ip");
        }
        try {
            selector = Selector.open();
            // We have to set connection host, port and non-blocking mode
            socket = ServerSocketChannel.open();

            ServerSocket serverSocket = socket.socket();
            serverSocket.bind(new InetSocketAddress(4444));
           
            socket.configureBlocking(false);
            int ops = socket.validOps();
            socket.register(selector, ops, null);
            System.out.println("Wewnętrzne IP: " + InetAddress.getLocalHost() + "\nZewnętrzne IP: " + serverIp + "\nPORT: " + "4444");
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
                        handleAccept(socket, key);
                    } else if (key.isReadable()) {
                        handleRead(key);
                    }
                    i.remove();
                }
            } catch (WrongPacketException | IOException | SQLException e) {
                System.out.println(e.getMessage());
            } finally {
                selector.selectedKeys().clear();
            }
        }
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
            if(clientsCoordinator.findClientBySocket(client.socket()) != null) {
                clientsCoordinator.disconnect(client.socket());
            } else {
                clientsCoordinator.disconnectLoggClient(client.socket());
            }
        }
        else {
            System.out.println("Reading...");
            PacketHandler.handleData(data, client);
        }
    }
}
