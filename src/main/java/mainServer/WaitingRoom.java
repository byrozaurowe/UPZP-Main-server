package mainServer;

import mainServer.game.Game;
import mainServer.game.GamesHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class WaitingRoom {
    /** Id pokoju */
    private int id;
    /** Obiekt drużyny pierwszej */
    private Team team1;
    /** Obiekt drużyny drugiej */
    private Team team2;
    /** Nazwa mapy */
    private String city;
    /** Gracz, który jest założycielem gry */
    private Client host;
    /** Max liczba osób w drużynie */
    private int clientsMax;
    /** Czy gra jest rozpoczęta */
    private boolean status;



    public void setId(int id) {
        this.id = id;
    }

    public void setClientsMax(int clientsMax) {
        this.clientsMax = clientsMax;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getId() { return id; }
    public Team[] getTeams() { Team[] teams = new Team[]{team1, team2}; return teams; }
    public String getCity() { return city; }
    public int getHost() { return host.getId(); }
    public String getHostName() { return host.getName(); }
    public int getClientsLoggedVal() { return team1.clientsSize() + team2.clientsSize(); }
    public int getClientsMax() { return clientsMax; }
    public boolean getStatus() { return status; }
    public Game game;

    public int getUdpPort() {
        if(game != null) {
            return game.getUdpPort();
        }
        return -1;
    }

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
                client.enterWaitingRoomList();
                return true;
            }
        }
        if(team1.clients.contains(client)) {
            team1.leaveTeam(client);
            client.enterWaitingRoomList();
            return true;
        }
        else if(team2.clients.contains(client)) {
            team2.leaveTeam(client);
            client.enterWaitingRoomList();
            return true;
        }
        return false;
    }

    /** Funkcja dołączająca klienta do drużyny
     * @param client klient
     * @return czy się powiodło?
     */
    public boolean joinTeam(Client client) {
        client.enterWaitingRoom();
        if(team1.clientsSize() <= team2.clientsSize()) {
            if (team1.joinTeam(client)) {
                return true;
            }
            else {
                return team2.joinTeam(client);
            }
        }
        else {
            if(team2.joinTeam(client)) {
                return true;
            }
            else {
                return team1.joinTeam(client);
            }
        }
    }

    private boolean canStart() {
        return team1.clientsSize() >= clientsMax/2 && team2.clientsSize() >= clientsMax/2;
    }

    boolean startGame() {
        if(canStart()) {
            game = GamesHandler.newGame(city, id);
            for (Team t : getTeams()) {
                for (Client c : t.clients) {
                    c.enterGame();
                }
            }
            status = true;
            return true;
        }
        return false;
    }

    void endGame() {
        for(Team t : getTeams()) {
            for(Client c : t.clients) {
                c.enterWaitingRoomList();
            }
        }
        game = null;
        status = false;
    }

    public boolean isHost(Client client) {
        return client == host;
    }


    /** Funkcja zmieniająca hosta
     * @return czy się powiodło?
     */
    public boolean changeHost() {
        Client h = team1.getNewHost(host);
        if(h != null) {
            host = team1.getNewHost(host);
            return true;
        }
        h = team2.getNewHost(host);
        if(h != null) {
            host = team2.getNewHost(host);
            return true;
        }
        return false;
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
        if(team1.isClientInTeam(client)) return team1;
        if(team2.isClientInTeam(client)) return team2;
        return null;
    }

    /** Konstruktor Waiting roomu (poczekalni)
     * @param city miasto, gdzie odbędzie się rozgrywka
     * @param host klient, który jest administratorem gry
     * @param clientsMax maksymalna liczba graczy w drużynie
     */
    public WaitingRoom(String city, Client host, int clientsMax) {
        this.city = city;
        this.host = host;
        this.clientsMax = clientsMax;

        team1 = new Team(clientsMax);
        team2 = new Team(clientsMax);
        status = false;
    }
}
