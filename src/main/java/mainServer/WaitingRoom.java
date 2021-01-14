package mainServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class WaitingRoom {

    private int id;
    private Team team1;
    private Team team2;
    private String city;
    private Chat chat;
    private Client host;

    public void setId(int id) {
        this.id = id;
    }

    public void setClientsMax(int clientsMax) {
        this.clientsMax = clientsMax;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    private int clientsMax;
    private boolean status;

    public int getId() { return id; }
    public Team[] getTeams() { Team[] teams = new Team[]{team1, team2}; return teams; }
    public String getCity() { return city; }
    public int getHost() { return host.getId(); }
    public String getHostName() { return host.getName(); }
    public int getClientsLoggedVal() { return team1.clientsSize() + team2.clientsSize(); }
    public int getClientsMax() { return clientsMax; }
    public boolean getStatus() { return status; }

    public ArrayList<Client> getClients(int team) {
        if (team == 1) {
            return team1.getClients();
        }
        if (team == 2) {
            return team2.getClients();
        }
        return null;
    }

    /** Funkcja usuwająca klienta z Waiting roomu
     * @param client klient
     * @return czy się powiodło
     */
    public boolean leaveWaitingRoom(Client client) {
        if(isHost(client)){
            if(!changeHost()) {
                Main.server.waitingRoomsCoordinator.removeWaitingRoom(this);
                return true;
            }
        }
        if(team1.clients.contains(client)) {
            team1.leaveTeam(client);
            return true;
        }
        else if(team2.clients.contains(client)) {
            team2.leaveTeam(client);
            return true;
        }
        return false;
    }

    /** Funkcja dołączająca klienta do drużyny
     * @param client klient
     * @return czy się powiodło?
     */
    public boolean joinTeam(Client client) {
        client.setClientStatus(ClientStatus.WAITING_ROOM);
        if (team1.clientsSize() <= team2.clientsSize()) {
            if (team1.joinTeam(client)) {
                return true;
            }
            else {
                return team2.joinTeam(client);
            }
        }
        else {
            if (team2.joinTeam(client)) {
                return true;
            }
            else {
                return team1.joinTeam(client);
            }
        }
    }

    public boolean canStart() {
        return team1.clientsSize() >= clientsMax/2 && team2.clientsSize() >= clientsMax/2;
    }

    public boolean isHost(Client client) {
        return client == host;
    }


    /** Funkcja zmieniająca hosta
     * @return czy się powiodło?
     */
    public boolean changeHost() {
        if (team1.clientsSize() > 0) {
            host = team1.getFirstClient();
            return true;
        }
        else if (team2.clientsSize() > 0) {
            host = team2.getFirstClient();
            return true;
        }
        else return false;
    }

    /** Wysyła wiadomość to wszystkich w tym waiting roomie
     * @param toSend wiadomość do wysłania
     */
    public void sendToPlayersInRoom(byte[] toSend) throws IOException {
        for(Client c : team1.clients) {
            c.getSocket().getChannel().write(ByteBuffer.wrap(toSend));
        }
        for(Client c : team2.clients) {
            c.getSocket().getChannel().write(ByteBuffer.wrap(toSend));
        }
    }

    public boolean isClientInRoom (Client client) {
        return team1.isClientInTeam(client) || team2.isClientInTeam(client);
    }

    public Team getTeamByClient(Client client) {
        if (team1.isClientInTeam(client)) return team1;
        if (team2.isClientInTeam(client)) return team2;
        return null;
    }

    /** Konstruktor Waiting roomu (poczekalni)
     * @param city miasto, gdzie odbędzie się rozgrywka
     * @param host klient, który jest administratorem gry
     * @param clientsMax maksymalna liczba graczy w grze
     */
    public WaitingRoom(String city, Client host, int clientsMax) {
        this.city = city;
        this.host = host;
        this.clientsMax = clientsMax;

        team1 = new Team();
        team2 = new Team();
        chat = new Chat();
        status = false;
    }
}
