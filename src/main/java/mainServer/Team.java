package mainServer;

import java.util.*;

/** Klasa obiektu drużyna */
public class Team {
    /** Lista członków danej drużyny */
    ArrayList<Client> clients;
    /** Lista pojazdów, które drużyna ma do dyspozycji */
    Vehicle[] vehicles;
    /** Maksymalny rozmiar drużyny */
    private int maxSize;

    /** Konstruktor obiektu drużyna
     * @param maxSize maksymalny rozmiar drużyny
     */
    public Team(int maxSize) {
        clients = new ArrayList<>();
        this.maxSize = maxSize;
        initializeVehicles();
    }

    /** Funkcja, która generuje losowo pojazdy dla danej drużyny */
    private void initializeVehicles() {
        int cars = maxSize/3;
        int bikes = maxSize/3 + maxSize%3;
        int pedestrians = maxSize/3;
        vehicles = new Vehicle[cars+bikes+pedestrians];
        for(int i = 0; i < cars; i++) {
            vehicles[i] = new Vehicle(Vehicle.VehicleType.Car, 40);
        }
        for(int i = cars; i < cars+bikes; i++) {
            vehicles[i] = new Vehicle(Vehicle.VehicleType.Cyclist, 20);
        }
        for(int i = cars+bikes; i < cars+bikes+pedestrians; i++) {
            vehicles[i] = new Vehicle(Vehicle.VehicleType.Pedestrian, 5);
        }
    }

    /** Funkcja obsługująca dołączenie do drużyny
     * @param client klient, który chce dołączyć do drużyny
     * @return czy się powiodło?
     */
    boolean joinTeam(Client client) {
        if(clients.size() < maxSize) {
            clients.add(client);
            client.enterWaitingRoom();
            List<Vehicle> vehicleList = Arrays.asList(vehicles);
            Collections.shuffle(vehicleList);
            vehicleList.toArray(vehicles);
            Vehicle vehicle = null;
            for(Vehicle v : vehicles) {
                if(v.free) {
                    vehicle = v;
                }
            }
            client.setVehicle(vehicle);
            return true;
        }
        else return false;
    }

    /** Czy użytkownik może dołączyć do drużyny?
     * @return True / False
     */
    boolean canJoin() {
        return clients.size() <= maxSize;
    }

    /** Czy podany klient jest członkiem drużyny?
     * @param client klient
     * @return True / False
     */
    boolean isClientInTeam(Client client) {
        for (Client c : clients) {
            if (c == client) return true;
        }
        return false;
    }

    /** Funkcja obsługująca zmianę pojazdu w drużynie
     * @param type rodzaj docelowego pojazdu
     * @param velocity prędkość docelowego pojazdu
     * @param client klient, który chce zmienić pojazd
     * @return czy udało się dokonać zmiany?
     */
    public boolean changeVehicle(Vehicle.VehicleType type, int velocity, Client client) {
        for (Vehicle vehicle : vehicles) {
            if (vehicle.free && vehicle.type == type) {
                vehicle.free = false;
                client.setVehicle(vehicle);
                return true;
            }
        }
        return false;
    }

    /** Funkcja obsługująca opuszczanie drużyny
     * @param client klient, który chce opuścić drużynę
     */
    void leaveTeam(Client client) {
        clients.remove(client);
        System.out.println("Usunięto klienta z drużyny: " + client.getName());
        client.enterWaitingRoomList();
    }

    /** Funkcja zwracająca liczebność drużyny
     * @return liczba graczy w drużynie
     */
    int clientsSize() {
        return clients.size();
    }

    /** Szuka nowego admina gry
     * @param host nowy admin gry
     * @return nowy admin gry typu klient
     */
    Client getNewHost(Client host) {
        for(Client c : clients) {
            if(c != null && c != host)
                return c;
        }
        return null;
    }

    /** Zwraca listę członków drużyny
     * @return lista członków drużyny
     */
    ArrayList<Client> getClients() { return clients; }
}
