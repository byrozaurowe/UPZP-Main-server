package mainServer;

import java.util.*;

public class Team {
    ArrayList<Client> clients;
    Vehicle[] vehicles;
    private int maxSize;

    public Team(int maxSize) {
        clients = new ArrayList<>();
        this.maxSize = maxSize;
        initializeVehicles();
    }

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

    boolean joinTeam(Client client) {
        if (clients.size() < maxSize) {
            clients.add(client);
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

    boolean isClientInTeam(Client client) {
        for (Client c : clients) {
            if (c == client) return true;
        }
        return false;
    }

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

    void leaveTeam(Client client) {
        clients.remove(client);
        System.out.println("Usunięto klienta z drużyny: " + client.getName());
        client.setClientStatus(ClientStatus.WAITING_ROOM_LIST);
    }

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

    ArrayList<Client> getClients() { return clients; }
}
