package mainServer.game;

public class Game {
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

    /** Konstruktor gry
     * @param udpPort Port połączenia UDP
     * @param tcpPort Port połączenia TCP
     * @param winPoints Punkty, przy których drużyna wygrywa
     * @param spawnPeriod Okres w sekundach spawnowania zbierajek na mapie
     */
    public Game(int udpPort, int tcpPort, int winPoints, int spawnPeriod, City city) {
        this.udpPort = udpPort;
        this.tcpPort = tcpPort;
        this.winPoints = winPoints;
        this.spawnPeriod = spawnPeriod;
        this.city = city;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public int getUdpPort() {
        return udpPort;
    }
}
