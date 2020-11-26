package mainServer.logging;

import java.net.Socket;

/** Klasa obiektu klienta, który chce się zalogować */
public class LoggingClient {
    /** Adres ip użytkownika */
    private String ipAddress;
    /** Nazwa użytkownika */
    private String name;
    /** Socket użytkownika */
    private Socket socket;

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /** Zahaszowane hasło */
    private String password;

    public String getIpAddress() {
        return ipAddress;
    }

    public String getName() {
        return name;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getPassword() {
        return password;
    }

    public LoggingClient(Socket socket) {
        this.socket = socket;
    }
}
