package mainServer.logging;
import mainServer.Client;
import mainServer.Main;

import java.net.InetAddress;

public class LoginHandler {
    public static boolean verifyIdentity(LoggingClient logClient) {
        String name = logClient.getName();
        String password = logClient.getPassword();
        InetAddress ip = logClient.getIpAddress();
        boolean isCorrect = true; // Weryfikacja w bazie danych
        if(isCorrect) {
            //Main.server.clientsCoordinator.sendTo(name, Packet.LOGIN_ACCEPTED);
            Client client = new Client(name, ip, logClient.getSocket());
            signIn(client, logClient);
            return true;
        }
        else {
            //Main.server.clientsCoordinator.sendTo(name, Packet.LOGIN_REJECTED);
            return false;
        }
    }

    private static void signIn(Client client, LoggingClient loggingClient) {
        Main.server.clientsCoordinator.addClient(client, loggingClient);
        System.out.println("Dodano zweryfikowanego klienta " + client.getName());
    }
}
