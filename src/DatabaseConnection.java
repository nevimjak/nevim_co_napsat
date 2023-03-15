import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Trida realizujici pripojeni k databazi
 */
public class DatabaseConnection {

    private static Connection connection;

    /**
     * Ziskani pripojeni k databazi
     * @param host hostitelsky server databaze
     * @param user uzivatelske jmeno pristupu do databaze
     * @param pass heslo pro pristup do databaze
     * @param schema vychozi schema databaze
     * @return pripojeni k databazi
     * @throws SQLException
     */
    public static Connection getConnection(String host, String user, String pass, String schema) throws SQLException {
        if (connection == null) {
            String url = "jdbc:mysql://" + host + "/" + schema + "?useSSL=false";
            connection = DriverManager.getConnection(url, user, pass);
        }
        return connection;
    }
}
