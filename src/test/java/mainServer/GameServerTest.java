package mainServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameServerTest {

    private static final int PORT = 6666;

    private GameServer gameServer;

    @Before
    public void init() {
        gameServer = new GameServer();
        gameServer.createGameSocket(PORT);
    }

    @After
    public void destroy() {
        gameServer.stopGameSocket(PORT);
    }

    @Test
    public void givenGreetingClient_whenServerRespondsWhenStarted_thenCorrect() {
        GreetClient client = new GreetClient();
        client.startConnection("127.0.0.1", PORT);
        String response = client.sendMessage("hello server");
        assertEquals("hello client", response);
    }

    private class GreetClient {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public void startConnection(String ip, int port) {
            try {
                clientSocket = new Socket(ip, port);
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String sendMessage(String msg) {
            out.println(msg);
            String resp = null;
            try {
                resp = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return resp;
        }

        public void stopConnection() {
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}