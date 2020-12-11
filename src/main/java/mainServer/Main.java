package mainServer;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Main {
    public static Server server;

    public static void main(String[] args) {
        System.out.println("------Serwer start------");
        server = new Server();
        new Thread(server).start();
    }
}
