package mainServer;

import java.net.InetAddress;
import java.net.Socket;

/** Klasa obiektu klient */
public class Client {
    /** Id klienta */
    private int id;
    /** Nazwa użytkownika klienta */
    private String name;
    /** Adres ip klienta */
    private InetAddress ipAddress;
    /** Socket używany przez klienta */
    private Socket socket;
    /** Status klienta, czyli na jakim etapie się znajduje*/
    private ClientStatus clientStatus;
    /** Pojazd, którego używa klient */
    private Vehicle vehicle;

    /** Zwraca nazwę użytkownika klienta
     * @return nazwę użytkownika klienta jako string
     */
    public String getName() {
        return name;
    }

    /** Zwraca IPAddress klienta
     * @return IPAddress klienta
     */
    public InetAddress getIpAddress() {
        return ipAddress;
    }

    /** Zwraca socket przez, który połączony jest klient
     * @return socket
     */
    public Socket getSocket() {
        return socket;
    }

    /** Zwraca id klienta
     * @return id klienta
     */
    public int getId() { return id; }

    /** Zwraca pojazd gracza w grze
     * @return obiekt typu Vehicle
     */
    public Vehicle getVehicle() { return vehicle; }

    /** Ustawia id klienta
     * @param id docelowe id klienta
     */
    public void setId(int id) {
        this.id = id;
    }

    /** Przypisuje klientowi nowy pojazd
     * @param vehicle nowy pojazd
     */
    public void setVehicle(Vehicle vehicle) {
        if(getVehicle() != null) {
            this.vehicle.free = true;
        }
        this.vehicle = vehicle;
    }

    /** Zwraca status klienta
     * @return status klienta
     */
    public ClientStatus getClientStatus() { return clientStatus; }

    /** Zmienia status klienta na listę waiting roomów */
    public void enterWaitingRoomList() { this.clientStatus = ClientStatus.WAITING_ROOM_LIST; }

    /** Zmienia status klienta na waiting room */
    public void enterWaitingRoom() { this.clientStatus = ClientStatus.WAITING_ROOM; }

    /** Zmienia status klienta na 'w grze' */
    public void enterGame() { this.clientStatus = ClientStatus.IN_GAME; }

    /** Konstruktor klienta
     * @param name nazwa klienta
     * @param ipAddress adres ip klienta
     * @param socket socket używany do komunikacji z klientem
     */
    public Client(String name, InetAddress ipAddress, Socket socket) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.socket = socket;
        this.clientStatus = ClientStatus.WAITING_ROOM_LIST;
    }
}
