package mainServer;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseHandler {

    // JDBC URL, username and password of MySQL server
    private static final String url = "jdbc:mysql://35.246.226.222:3306/Website";
    private static final String user = "dev";
    private static final String password = "pz2021project";

    private static DatabaseHandler DATABASE_HANDLER;

    public static DatabaseHandler getInstance() {
        if(DATABASE_HANDLER == null) {
            DATABASE_HANDLER = new DatabaseHandler();
        }
        return DATABASE_HANDLER;
    }

    /** Konnstruktor obslugi bazy danych */
    private DatabaseHandler() {
        if(DATABASE_HANDLER == null) {
            try {
                Properties props = new Properties();
                // opening database connection to MySQL server*/
                props.setProperty("user", user);
                props.setProperty("password", password);
                props.setProperty("useSSL", "true");
                props.setProperty("requireSSL", "true");
                props.setProperty("verifyServerCertificate", "true");

                System.setProperty("javax.net.ssl.trustStoreType", "JKS");
                URL res = getClass().getClassLoader().getResource("truststore.jks");
                File file = Paths.get(res.toURI()).toFile();
                String absolutePath = file.getAbsolutePath();
                System.setProperty("javax.net.ssl.trustStore", absolutePath);
                //System.setProperty("javax.net.ssl.trustStorePassword", "changeit");

                System.setProperty("javax.net.ssl.keyStoreType", "JKS");
                res = getClass().getClassLoader().getResource("keystore.jks");
                file = Paths.get(res.toURI()).toFile();
                absolutePath = file.getAbsolutePath();
                System.setProperty("javax.net.ssl.keyStore", absolutePath);
                System.setProperty("javax.net.ssl.keyStorePassword", "password");

                con = DriverManager.getConnection(url, props);
                // getting Statement object to execute query
                stmt = con.createStatement();
            } catch (SQLException | URISyntaxException sqlEx) {
                sqlEx.printStackTrace();
            }
        }
    }

    // JDBC variables for opening and managing connection
    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    void closeConnection() {
        try {
            con.close();
        } catch (SQLException se) { /*can't do anything */ }
        try {
            stmt.close();
        } catch (SQLException se) { /*can't do anything */ }
    }

    /** Funkcja logowanie do bazy danych
     * @param login login klienta
     * @param haslo hasło klienta
     * @return 1 jesli poprawnie zalogowanie, 0 jesli nie
     */
    public Object loggIn(String login, String haslo) throws SQLException {
        //String account_login_1 = "zxcv";
        //String account_pass_1 = "123456";

        String query_loggin = "SELECT Login_in_app('" + login + "', '" + haslo +"')";
        ResultSet rs = stmt.executeQuery(query_loggin);
        rs.next();
        return rs.getObject(1);
    }

    /** Funkcja informująca bazę o rozłączeniu użytkownika
     * @param clientId id klienta
     */
    public void playerDisconnected(int clientId) throws SQLException {
        String query_online = "";
        //ResultSet rs = stmt.executeQuery(query_online);
        //rs.next();
    }

    /** Funkcja czytająca z bazy id wolnej gry */
    public int getFreeGameId() throws SQLException {
        String query_loggin = "SELECT * FROM stat_map_game ORDER BY id DESC LIMIT 1";
        ResultSet rs = stmt.executeQuery(query_loggin);
        rs.next();
        return (int) rs.getObject(1);
    }
}