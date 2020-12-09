package mainServer.logging;
import mainServer.Client;
import mainServer.Main;

public class LoginHandler {
    public static void verifyIdentity(LoggingClient logClient) {
        String name = logClient.getName();
        String password = logClient.getPassword();
        String ip = logClient.getIpAddress();
        boolean isCorrect = true; // Weryfikacja w bazie danych
        if(isCorrect) {
            //Main.server.clientsCoordinator.sendTo(name, Packet.LOGIN_ACCEPTED);
            Client client = new Client(name, ip, logClient.getSocket());
            signIn(client);
        }
        else {
            //Main.server.clientsCoordinator.sendTo(name, Packet.LOGIN_REJECTED);
        }
    }
    private static void signIn(Client client) {
        Main.server.clientsCoordinator.addClient(client);
        System.out.println("Dodano zweryfikowanego klienta " + client.getName());
    }
}
