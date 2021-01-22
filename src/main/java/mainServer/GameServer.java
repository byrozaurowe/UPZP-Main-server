package mainServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class GameServer {

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
