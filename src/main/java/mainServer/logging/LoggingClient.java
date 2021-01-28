package mainServer.logging;

import java.net.InetAddress;
import java.net.Socket;

/** Klasa obiektu klienta, który chce się zalogować */
public class LoggingClient {
    /** Adres ip użytkownika */
    private InetAddress ipAddress;
    /** Nazwa użytkownika */
    private String name;
    /** Socket użytkownika */
    private Socket socket;
    /** Zahaszowane hasło */
    private String password;

    /** Ustawia nazwę użytkownika
     * @param name nazwa użytkownika
     */
    public void setName(String name) {
        this.name = name;
    }

    /** Ustawia hasło użytkownika
     * @param password hasło użytkownika
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /** Zwraca adres ip użytkownika
     * @return ip
     */
    public InetAddress getIpAddress() {
        return ipAddress;
    }

    /** Zwraca nazwę użytkownika
     * @return nazwa
     */
    public String getName() {
        return name;
    }

    /** Zwraca socket używany do komunikacji z logging klientem
     * @return socket
     */
    public Socket getSocket() {
        return socket;
    }

    /** Zwraca hasło logging klienta
     * @return hasło
     */
    public String getPassword() {
        return password;
    }

    /** Konstruktor osoby próbującej się zalogować
     * @param socket socket kandydata na bycie klientem
     */
    public LoggingClient(Socket socket) {
        this.socket = socket;
        this.ipAddress = socket.getInetAddress();
    }
}
