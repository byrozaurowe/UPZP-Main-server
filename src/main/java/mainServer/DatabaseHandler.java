package mainServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {

    // JDBC URL, username and password of MySQL server
    private static final String url = "jdbc:mysql://sql2.freemysqlhosting.net:3306/sql2381159";
    private static final String user = "sql2381159";
    private static final String password = "wI7*hQ2%";

    private static DatabaseHandler DATABASE_HANDLER;

    public static DatabaseHandler getInstance() {
        if(DATABASE_HANDLER == null) {
            DATABASE_HANDLER = new DatabaseHandler();
        }
        return DATABASE_HANDLER;
    }

    private DatabaseHandler() {
        if(DATABASE_HANDLER == null) {
            try {
                // opening database connection to MySQL server
                con = DriverManager.getConnection(url, user, password);
                // getting Statement object to execute query
                stmt = con.createStatement();
            } catch (SQLException sqlEx) {
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

    public Object loggIn(String login, String haslo) throws SQLException {
//        String query = "insert into main (login, password, mail, date_reg) VALUES ('test', 'test', 'test', '2020-11-19')";
        // exist
        //String account_login_1 = "zxcv";
        //String account_pass_1 = "123456";

        String query_loggin = "SELECT count(id) FROM user_details WHERE login='" + login + "'AND password=md5('" + haslo +  "')";
        ResultSet rs = stmt.executeQuery(query_loggin);
        rs.next();
        return rs.getObject(1);
    }

}