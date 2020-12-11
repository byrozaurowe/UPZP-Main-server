package mainServer;

import mainServer.logging.LoggingClient;
import mainServer.logging.LoginHandler;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

public class ClientsCoordinator {

    private ArrayList<Client> clients;
    private ArrayList<LoggingClient> loggingClients;

    ClientsCoordinator() {
        clients = new ArrayList<>();
        loggingClients = new ArrayList<>();
    }

    public void sendTo(String name, String packetType) {
        Client client = findClientByName(name);
    }

    public void sendTo(Socket s, String packetType, Object o) {
        if(s.isClosed())
            return;

    }

    public void addClient(Client client, LoggingClient loggingClient) {
        clients.add(client);
        loggingClients.remove(loggingClient);
    }

    public boolean verifyLoggingClient(Socket s, String name, String pass) throws SQLException, IOException {
        LoggingClient client = findLogClientBySocket(s);
        client.setName(name);
        client.setPassword(pass);
        return LoginHandler.verifyIdentity(client);
    }

    public boolean connect(Socket s) {
        loggingClients.add(new LoggingClient(s));
        System.out.println("Połączono do " + s);

        return true;
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

    private Client findClientBySocket(Socket s) {
        for(Client client: clients) {
            if(client.getSocket() == s) {
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
        } catch (NullPointerException e) {}
    }

    public void disconnect(Socket s) throws IOException {
        try {
            Client client = findClientBySocket(s);
            System.out.println("Rozłączono " + client.getName() +": " + client.getSocket());
            client.getSocket().close();
            clients.remove(client);
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
    }

    public void disconnectLoggClient(LoggingClient loggingClient) {
        try {
            LoggingClient client = findLogClientBySocket(loggingClient.getSocket());
            System.out.println("Rozłączono " + client.getName() +": " + client.getSocket());
            client.getSocket().close();
            loggingClients.remove(client);
        } catch (NullPointerException | IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
