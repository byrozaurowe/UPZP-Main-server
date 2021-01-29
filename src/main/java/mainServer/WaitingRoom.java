package mainServer;

import mainServer.game.Game;
import mainServer.game.GamesHandler;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/** Obiekt pokój */
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
    /** Max liczba punktów */
    private int pointMax;
    /** Obiekt rozpoczętej gry */
    public Game game;

    /** Zwraca pointMax pokoju
     * @return pointMax pokoju
     */
    public int getPointMax() { return pointMax; }

    /** Ustawia id pokoju
     * @param id nowe id
     */
    public void setId(int id) {
        this.id = id;
    }

    /** Zwraca id pokoju
     * @return id pokoju
     */
    public int getId() { return id; }

    /** Zwraca tablicę drużyn
     * @return dwuelementowa tablica drużyn
     */
    public Team[] getTeams() {
        return new Team[]{ team1, team2 };
    }

    /** Zwraca string nazwy mapy
     * @return nazwa mapy w formie stringa
     */
    public String getCity() { return city; }

    /** Zwraca id osoby, która jest hostem
     * @return id hosta
     */
    public int getHostId() { return host.getId(); }

    /** Zwraca osobę, która jest hostem
     * @return host
     */
    public Client getHost() { return host; }

    /** Zwraca nazwę hosta
     * @return nazwa hosta w formie stringa
     */
    public String getHostName() { return host.getName(); }

    /** Zwraca liczbę graczy w pokoju
     * @return liczba graczy w pokoju jako int
     */
    public int getClientsLoggedVal() { return team1.clientsSize() + team2.clientsSize(); }

    /** Zwraca maksymalną liczbę graczy w drużynie
     * @return maksymalna liczba graczy w drużynie jako int
     */
    public int getClientsMax() { return clientsMax; }

    /** Status gry
     * @return czy gra jest rozpoczęta czy nie?
     */
    public boolean getStatus() { return status; }

    /** Zwraca socket podprocesu, który obsługuje grę
     * @return socket
     */
    public Socket getGameSocket() {
        return game.getSocket();
    }

    /** Ustawia socket podprocesu, który obsługuje grę
     * @param s socket
     */
    public void setGameSocket(Socket s) {
        game.setSocket(s);
    }

    /** Zwraca port UDP, na którym trwa gra
     * @return port udp
     */
    public int getUdpPort() {
        if(game != null) {
            return game.getUdpPort();
        }
        return -1;
    }

    /** Zwraca klientów danej drużyny
     * @param team drużyna jako 1 lub 2
     * @return lista klientów
     */
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
                if(game != null)
                    game.killGame();
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
        if(team1.clientsSize() <= team2.clientsSize()) {
            if(team1.joinTeam(client)) {
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

    /** Funkcja dołączająca klienta do drużyny
     * @param client klient
     * @return czy się powiodło?
     */
    public boolean joinTeam(Client client, int team) {
        if(getTeams()[team].joinTeam(client)) {
            return true;
        }
        else {
            return getTeams()[(team+1)%2].joinTeam(client);
        }
    }

    public int checkTeamToJoin() {
        if(team1.clientsSize() <= team2.clientsSize() && team1.canJoin()) {
            return 0;
        }
        else if(team1.clientsSize() > team2.clientsSize() && team2.canJoin()){
            return 1;
        }
        else {
            return -1;
        }
    }

    /** Sprawdza, czy jest wystarczająco dużo osób, by rozpocząć grę
     * @return True / False
     */
    private boolean canStart() {
        return team1.clientsSize() >= Math.ceil(clientsMax/2) && team2.clientsSize() >= Math.ceil(clientsMax/2);
    }

    /** Odpowiada za rozpoczęcie gry
     * @return Udało się wystartować czy nie
     */
    boolean startGame() {
        if(canStart()) {
            game = GamesHandler.newGame(city, id, getPointMax());
            for (Team t : getTeams()) {
                for (Client c : t.clients) {
                    c.enterGame();
                }
            }
            status = true;
            try {
                game.runGame();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;

        }
        return false;
    }

    /** Odpowiada za zakończenie rozpoczętej gry */
    public void endGame() {
        for(Iterator<Team> teamIt = Arrays.stream(getTeams()).iterator(); teamIt.hasNext();) {
            Team nextTeam = teamIt.next();
            for(Iterator<Client> clientIt = nextTeam.clients.iterator(); clientIt.hasNext();) {
                Client next = clientIt.next();
                System.out.println("Usunięto klienta z drużyny: " + next.getName());
                next.enterWaitingRoomList();
                clientIt.remove();
            }
        }
        game = null;
        status = false;
        Main.server.waitingRoomsCoordinator.removeWaitingRoom(this);
        System.out.println("Gra ukończona, id: " + id);
        try {
            Main.server.clientsCoordinator.sendToAllWaitingRoomList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void addClientToRunningGame(byte[] toSend, Client c, SocketChannel client, byte[] gameStarted) throws IOException {
        if(game != null) {
            game.getSocket().getChannel().write(ByteBuffer.wrap(toSend));
            joinTeam(c);
            client.write(ByteBuffer.wrap(gameStarted));
            c.enterGame();
        }
    }

    /** Sprawdza, czy klient jest hostem
     * @param client klient, który jest sprawdzany
     * @return True / False
     */
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

    /** Sprawdza, czy klient jest w danym pokoju
     * @param client klient
     * @return True / False
     */
    public boolean isClientInRoom (Client client) {
        return team1.isClientInTeam(client) || team2.isClientInTeam(client);
    }

    /** Zwraca drużynę, do której należy klient
     * @param client klient
     * @return drużyna klienta
     */
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
    public WaitingRoom(String city, Client host, int clientsMax, int pointMax) {
        this.city = city;
        this.host = host;
        this.clientsMax = clientsMax;
        this.pointMax = pointMax;
        team1 = new Team(clientsMax);
        team2 = new Team(clientsMax);
        status = false;
    }
}
