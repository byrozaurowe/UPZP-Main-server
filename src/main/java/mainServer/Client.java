package mainServer;

import java.net.Socket;

public class Client {
    public String getName() {
        return name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Socket getSocket() {
        return socket;
    }

    private String name;
    private String ipAddress;
    private Socket socket;
    public Client(String name, String ipAddress, Socket socket) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.socket = socket;
    }
}
