package mainServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.*;

class PServer {

    private Map<Integer, GameSocket> gameSocketMap = new HashMap<>();

    public void createGameSocket(int port) {
        if (gameSocketMap.containsKey(port)) {
            throw new IllegalArgumentException(String.format("Server with port %s already exists", port));
        }

        gameSocketMap.put(port, new GameSocket(port));
    }

    public void stopGameSocket(int port) {
        if (!gameSocketMap.containsKey(port)) {
            throw new IllegalArgumentException(String.format("Server with port %s not exists", port));
        }

        gameSocketMap.get(port).stop();
        gameSocketMap.remove(port);
    }

    private class GameSocket {
        private ServerSocket serverSocket;
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public GameSocket(int port) {
            Thread thread = new Thread(() -> {
                try {
                    serverSocket = new ServerSocket(port);
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String greeting = in.readLine();
                    if ("hello server".equals(greeting)) {
                        out.println("hello client");
                    } else {
                        out.println("unrecognised greeting");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }

        public void stop() {
            try {
                in.close();
                out.close();
                clientSocket.close();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
public class GameServer implements Runnable {
    private static Selector selector = null;

    public void run() {
        ServerSocketChannel socket;
        try {
            selector = Selector.open();
            // We have to set connection host, port and non-blocking mode
            socket = ServerSocketChannel.open();

            ServerSocket serverSocket = socket.socket();
            serverSocket.bind(new InetSocketAddress(4445));

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

    /**
     * Obsługa próby podłączenia klienta pod serwer
     */
    private void handleAccept(ServerSocketChannel mySocket, SelectionKey key) throws IOException {
        System.out.println("Connection Accepted...");
        // Accept the connection and set non-blocking mode
        SocketChannel client = mySocket.accept();
        client.configureBlocking(false);
        System.out.println(client.getLocalAddress());
        // Register that client is reading this channel
        client.register(selector, SelectionKey.OP_READ);
    }

    /**
     * Obsługa wiadomości otrzymanej od klienta
     */
    private void handleRead(SelectionKey key) throws WrongPacketException, IOException, SQLException {
        // create a ServerSocketChannel to read the request
        SocketChannel client = (SocketChannel) key.channel();
        byte[] data = new byte[1024];

        client.read(ByteBuffer.wrap(data));
        byte[] a = new byte[1024];
        Arrays.fill(a, (byte) 0);
        if(Arrays.equals(a, data)) {
            System.out.println("Disconnecting game...");
            client.socket().close();
        } else {
            System.out.println("Reading game...");
            PacketHandler.handleData(data, client);
        }
    }
}