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

    /** Funkcja wysyłająca pakiet do klienta
     * @param s socket klienta, do którego chcemy wysłać pakiet
     * @param toSend pakiet do wysłania
     */
    public void sendTo(Socket s, byte[] toSend) throws IOException {
        s.getChannel().write(ByteBuffer.wrap(toSend));
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
    public void addClient(LoggingClient loggingClient) {
        Client client = new Client(loggingClient.getName(),
                                   loggingClient.getIpAddress(),
                                   loggingClient.getSocket());
        // przydzielanie ip, bedzie do zmiany bo baza musi nam wyslac
        if(clients.isEmpty()) {
            client.setId(1);
        }
        else {
            client.setId(clients.get(clients.size()-1).getId() + 1);
        }
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

    Client findClientByName(String name) {
        for(Client client: clients) {
            if(client.getName().equals(name)) {
                return client;
            }
        }
        return null;
    }

    private LoggingClient findLogClientBySocket(Socket s) {
        for(LoggingClient client: loggingClients) {
            if(client.getSocket() == s) {
                return client;
            }
        }
        return null;
    }

    public Client findClientBySocket(Socket s) {
        for(Client client: clients) {
            if(client.getSocket() == s) {
                return client;
            }
        }
        return null;
    }

    public Client findClientById(int id) {
        for (Client client : clients) {
            if (client.getId() == id) {
                return client;
            }
        }
        return null;
    }

    public void disconnect(String name) throws IOException {
        try {
            Client client = findClientByName(name);
            System.out.println("Rozłączono " + name +": " + client.getSocket());

            client.getSocket().close();
            clients.remove(client);
        } catch (NullPointerException e) {
            System.out.println("Disconnect Client error " + e.getMessage());
        }
    }

    /** Metoda rozłączająca klienta
     * @param s socket klienta, którego chcemy rozłączyć
     */
    public void disconnect(Socket s) throws IOException {
        try {
            Client client = findClientBySocket(s);
            System.out.println("Rozłączono " + client.getName() +": " + client.getSocket());
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
