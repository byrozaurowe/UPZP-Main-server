package mainServer.game;

import java.util.ArrayList;
import java.util.Arrays;

/** Statyczna klasa tworząca nowe gry */
public class GamesHandler {

    /** Lista map */
    public static ArrayList<String> cities = new ArrayList<>(Arrays.asList(
            "Wrocław", "Nowy Jork", "Amsterdam", "Kopenhaga", "Helsinki"));

    /** Znajduje nowy port dla gry */
    private static int findNewPort(String connection, int gameId) {
        if(connection.equals("TCP")) {
            return 4445;
        }
        else {
            return 4446 + gameId;
        }
    }

    /** Tworzy nową grę
     * @param cityName nazwa miasta
     * @param gameId id gry
     * @return obiekt typu Game
     */
    public static Game newGame(String cityName, int gameId) {
        int tcp = findNewPort("TCP", gameId);
        int udp = findNewPort("UDP", gameId);
        City city;
        switch (cityName) {
            case "Wrocław":
                city = new City(51.10000, 17.03333, 15, cityName);
                break;
            case "Nowy Jork":
                city = new City(40.7142700, -74.0059700, 20, cityName);
                break;
            case "Amsterdam":
                city = new City(52.3740300, 4.8896900, 20, cityName);
                break;
            case "Kopenhaga":
                city = new City(55.6759400, 12.5655300, 20, cityName);
                break;
            case "Helsinki":
                city = new City(60.1695200, 24.9354500, 20, cityName);
                break;
            default:
                return null;
        }
        return new Game(udp, tcp, 20, 10, city, gameId);
    }
}
