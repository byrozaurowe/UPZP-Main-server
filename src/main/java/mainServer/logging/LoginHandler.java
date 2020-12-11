package mainServer.logging;
import mainServer.Client;
import mainServer.DatabaseHandler;
import mainServer.Main;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;

public class LoginHandler {
    public static boolean verifyIdentity(LoggingClient logClient) throws SQLException, IOException {
        String name = logClient.getName();
        String password = logClient.getPassword();
        InetAddress ip = logClient.getIpAddress();
        Object o = DatabaseHandler.getInstance().loggIn(name, password);
        if(o.toString().equals("1")) {
            Client client = new Client(name, ip, logClient.getSocket());
            signIn(client, logClient);
            return true;
        }
        else {
            System.out.println("Błędne logowanie");
            //Main.server.clientsCoordinator.disconnectLoggClient(logClient);
            return false;
        }
    }

    private static void signIn(Client client, LoggingClient loggingClient) {
        Main.server.clientsCoordinator.addClient(client, loggingClient);
        System.out.println("Dodano zweryfikowanego klienta " + client.getName());
    }
}
