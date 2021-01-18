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
    public Vehicle getVehicle() { return vehicle; }

    private String name;
    private InetAddress ipAddress;
    private Socket socket;
    private int id;

    public void setId(int id) {
        this.id = id;
    }

    private ClientStatus clientStatus;
    private Vehicle vehicle;

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public ClientStatus getClientStatus() { return clientStatus; }

    public void setClientStatus(ClientStatus clientStatus) {
        this.clientStatus = clientStatus;
    }

    public void enterWaitingRoomList() { this.clientStatus = ClientStatus.WAITING_ROOM_LIST; }

    public void enterWaitingRoom() { this.clientStatus = ClientStatus.WAITING_ROOM; }

    public void enterGame() { this.clientStatus = ClientStatus.IN_GAME; }

    public Client(String name, InetAddress ipAddress, Socket socket) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.socket = socket;
        this.clientStatus = ClientStatus.WAITING_ROOM_LIST;
    }
}
