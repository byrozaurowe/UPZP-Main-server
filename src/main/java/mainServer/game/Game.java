package mainServer.game;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Obiekt gra */
public class Game {
    private int id;
    /** Port połączenia UDP */
    private int udpPort;
    /** Port połączenia TCP */
    private int tcpPort;
    /** Punkty, przy których drużyna wygrywa */
    private int winPoints;
    /** Okres w sekundach spawnowania zbierajek na mapie */
    private int spawnPeriod;
    /** Miasto, w którym będzie się odbywać gra */
    private City city;
    /** Socket do połączenia */
    private Socket socket;

    /** Konstruktor gry
     * @param udpPort Port połączenia UDP
     * @param tcpPort Port połączenia TCP
     * @param winPoints Punkty, przy których drużyna wygrywa
     * @param spawnPeriod Okres w sekundach spawnowania zbierajek na mapie
     */
    public Game(int udpPort, int tcpPort, int winPoints, int spawnPeriod, City city, int id) {
        this.udpPort = udpPort;
        this.tcpPort = tcpPort;
        this.winPoints = winPoints;
        this.spawnPeriod = spawnPeriod;
        this.city = city;
        this.id = id;
    }

    /** Funkcja ustawiająca socket, przez który komunikuje się gra
     * @param s socket do komunikacji TCP
     */
    public void setSocket(Socket s) {
        this.socket = s;
    }

    /** Funkcja zwracjąca socket, przez który komunikuje się gra
     * @return socket do komunikacji TCP
     */
    public Socket getSocket() {
        return socket;
    }

    /** Funkcja zwracająca numer portu do komunikacji podprocesu z klientami
     * @return numer portu UDP
     */
    public int getUdpPort() {
        return udpPort;
    }

    /** Funkcja uruchamiająca podproces dla danej gry */
    public void runGame() throws IOException {

        String command = Paths.get(".").normalize().toAbsolutePath() + "/UPZP-GameProcess-sending_game_id/UPZP_GameProcess";
        String arg = " --udp " + udpPort + " --tcp " + tcpPort + " --lat_start " + city.getLatitude() +
                    " --long_start " + city.getLongitude() + " --radius " + city.getRadius() +
                    " --map " + city.getCityString() + " --id " + id + " --win_points " + winPoints +
                    " --spawn_period " + spawnPeriod;
        Process process = Runtime.getRuntime().exec(command+arg);
    }
}
