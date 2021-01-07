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

    public boolean joinTeam(Client client) {
        if (team1.clientsSize() <= team2.clientsSize()) {
            if (team1.joinTeam(client)) return true;
            else return team2.joinTeam(client);
        }
        else {
            if (team2.joinTeam(client)) return true;
            else return team1.joinTeam(client);
        }
    }

    public boolean canStart() {
        return team1.clientsSize() >= 10 && team2.clientsSize() >= 10;
    }

    public boolean isHost(Client client) {
        return client == host;
    }

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
        if (team1.isClientInTeam(client) == true || team2.isClientInTeam(client) == true) return true;
        else return false;
    }

    public Team getTeamByClient(Client client) {
        if (team1.isClientInTeam(client) == true) return team1;
        if (team2.isClientInTeam(client) == true) return team2;
        return null;
    }

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
