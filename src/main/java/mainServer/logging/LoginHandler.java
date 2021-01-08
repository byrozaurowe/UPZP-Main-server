package mainServer.logging;
import mainServer.DatabaseHandler;
import mainServer.Main;
import mainServer.PacketHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;

/** Klasa obsługująca logowanie */
public class LoginHandler {
    /** Metoda weryfikująca poprawność danych logującego się klienta
     * @param logClient logujący się klient
     * @return czy zalogowano poprawnie?
     */
    public static boolean verifyIdentity(LoggingClient logClient) throws SQLException, IOException {
        String name = logClient.getName();
        String password = logClient.getPassword();
        InetAddress ip = logClient.getIpAddress();
        Object o = DatabaseHandler.getInstance().loggIn(name, password);
        if(o.toString().equals("1")) {
            signIn(logClient);
            return true;
        }
        else {
            System.out.println("Błędne logowanie");
            //Main.server.clientsCoordinator.disconnectLoggClient(logClient);
            return false;
        }
    }

    private static void signIn(LoggingClient loggingClient) {
        System.out.println("Dodano zweryfikowanego klienta " + loggingClient.getName());
        Main.server.clientsCoordinator.addClient(loggingClient);
    }
}
