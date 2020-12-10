package mainServer;

import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public String getName() {
        return name;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public Socket getSocket() {
        return socket;
    }

    public int getId() { return id; }

    private String name;
    private InetAddress ipAddress;
    private Socket socket;
    private int id;

    public Client(String name, InetAddress ipAddress, Socket socket) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.socket = socket;
    }
}
