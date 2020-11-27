package mainServer;

import java.util.ArrayList;

public class Team {
    ArrayList<Client> clients;
    Vehicle[] vehicles;

    public Team () {
        clients = new ArrayList<Client>();
        initializeVehicles();
    }

    private void initializeVehicles() {
        //tutaj trzeba będzie stworzyć konkretne 20 pojazdów, ale jeszcze nie wiem jakich
    }

    boolean joinTeam(Client client) {
        if (clients.size() < 20) {
            clients.add(client);
            //tutaj trzeba tez przypisać pojazd klientowi i zająć ten pojazd w tablicy pojazdów
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
}
