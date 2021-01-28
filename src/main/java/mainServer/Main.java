package mainServer;

/** Main */
public class Main {
    /** Serwer główny nio */
    public static Server server;
    /** Serwer nio obsługujący trwające gry */
    public static GameServer gameServer;

    /** Main */
    public static void main(String[] args) {
        System.out.println("------Serwer start------");
        server = new Server();
        new Thread(server).start();

        gameServer = new GameServer();
        new Thread(gameServer).start();
    }
}
