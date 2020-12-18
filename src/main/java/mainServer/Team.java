package mainServer;

import java.util.*;

public class Team {
    ArrayList<Client> clients;
    Vehicle[] vehicles;

    public Team () {
        clients = new ArrayList<>();
        initializeVehicles();
    }

    private void initializeVehicles() {
        int cars = 10;
        int bikes = 10;
        int pedestrians = 10;
        vehicles = new Vehicle[cars+bikes+pedestrians];
        for(int i = 0; i < cars; i++) {
            vehicles[i] = new Vehicle(Vehicle.VehicleType.Car, 40);
        }
        for(int i = cars; i < cars+bikes; i++) {
            vehicles[i] = new Vehicle(Vehicle.VehicleType.Car, 20);
        }
        for(int i = cars+bikes; i < cars+bikes+pedestrians; i++) {
            vehicles[i] = new Vehicle(Vehicle.VehicleType.Car, 5);
        }
    }

    boolean joinTeam(Client client) {
        if (clients.size() < 20) {
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

    int clientsSize() {
        return clients.size();
    }

    Client getFirstClient() {
        return clients.get(1);
    }

    ArrayList<Client> getClients() { return clients; }
}
