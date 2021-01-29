package mainServer;

import mainServer.logging.LoggingClient;
import mainServer.logging.LoginHandler;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.ArrayList;

/** Klasa zarządzająca klientami podpiętymi pod serwer główny */
public class ClientsCoordinator {

    /** Lista zalogowanych klientów */
    private ArrayList<Client> clients;
    /** Lista klientów oczekujących na weryfikacje danych */
    private ArrayList<LoggingClient> loggingClients;

    /** Konstrukor koordynatora klientów */
    ClientsCoordinator() {
        clients = new ArrayList<>();
        loggingClients = new ArrayList<>();
    }

    /** Funkcja wysyłająca pakiety do klientów na liście */
    public void sendToAllWaitingRoomList() throws IOException {
        byte[] toSend = PacketHandler.buildWaitingRoomsList();
        for(Client c : clients) {
            if(c.getClientStatus() == ClientStatus.WAITING_ROOM_LIST)
                c.getSocket().getChannel().write(ByteBuffer.wrap(toSend));
        }
    }

    /** Dodaje klenta po zweryfikowaniu danych
     * @param loggingClient klient, którego zweryfikowano
     */
    public void addClient(LoggingClient loggingClient, int id) {
        Client client = new Client(loggingClient.getName(),
                                   loggingClient.getIpAddress(),
                                   loggingClient.getSocket());
        client.setId(id);
        clients.add(client);
        loggingClients.remove(loggingClient);
    }

    /** Uzupełnia dane loggingClienta
     * @param s socket logującego się klienta
     * @param name nazwa logującego się klienta
     * @param pass hasło logującego się klienta
     * @return czy poprawnie zweryfikowano hasło i login w bazie?
     */
    public boolean verifyLoggingClient(Socket s, String name, String pass) throws SQLException, IOException {
        LoggingClient client = findLogClientBySocket(s);
        client.setName(name);
        client.setPassword(pass);
        return LoginHandler.verifyIdentity(client);
    }

    /** Podłącza próbującego się połączyć, niezweryfikowanego klienta
     * @param s socket, próbującego się połączyć klienta
     */
    public void connect(Socket s) {
        loggingClients.add(new LoggingClient(s));
        System.out.println("Połączono do " + s);
    }

    /** Znajduje obiekt logging client na podstawie socketa
     * @param s socket
     * @return szukany logging client
     */
    private LoggingClient findLogClientBySocket(Socket s) {
        for(LoggingClient client: loggingClients) {
            if(client.getSocket() == s) {
                return client;
            }
        }
        return null;
    }

    /** Znajduje obiekt klient na podstawie socketa
     * @param s socket
     * @return szukany klient
     */
    public Client findClientBySocket(Socket s) {
        for(Client client: clients) {
            if(client.getSocket() == s) {
                return client;
            }
        }
        return null;
    }

    /** Metoda rozłączająca klienta
     * @param s socket klienta, którego chcemy rozłączyć
     */
    public void disconnect(Socket s) throws IOException {
        try {
            Client client = findClientBySocket(s);
            System.out.println("Rozłączono " + client.getName() +": " + client.getSocket());
            if(client.getClientStatus() == ClientStatus.IN_GAME)
                Main.server.waitingRoomsCoordinator.kickFromWaitingRoom(client);
            client.getSocket().close();
            DatabaseHandler.getInstance().playerDisconnected(findClientBySocket(s).getId());
            clients.remove(client);
        } catch (NullPointerException | SQLException e) {
            System.out.println("Disconnect Client error " + e.getMessage());
        }
    }

    /** Metoda rozłączająca klienta, który chce się zalogować
     * @param s socket klienta, który chce się zalogować
     */
    public void disconnectLoggClient(Socket s) {
        try {
            LoggingClient client = findLogClientBySocket(s);
            System.out.println("Rozłączono lc " + client.getName() +": " + client.getSocket());
            client.getSocket().close();
            loggingClients.remove(client);
        } catch (NullPointerException | IOException e) {
            System.out.println("Disconnect loggClient error " + e.getMessage());
        }
    }
}
