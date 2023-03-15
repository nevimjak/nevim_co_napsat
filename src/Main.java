import java.io.FileNotFoundException;
import java.sql.Connection;
import java.nio.file.Paths;
import java.io.File;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Main trida a "spravce zavislosti"
 */
public class Main {

    private static ConfigManager configManager;
    private static Connection databaseConnection;
    private static DatabaseModel databaseModel;

    public static final String CONFIG_FILE_NAME = "config.txt";

    /**
     * Vstupni bod programu
     * @param args
     */
    public static void main(String[] args)
    {
        new Window();
    }

    /**
     * Navrati spravce konfigurace
     * @return instance manazera konfigurace
     */
    public static ConfigManager getConfigManager()
    {
        if(configManager == null)
        {
            configManager = new ConfigManager();
            File configFile = Paths.get(CONFIG_FILE_NAME).toFile();
            try {
                Scanner scan = new Scanner(configFile);
                configManager.loadConfig(scan);
                scan.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Nepodarilo najit konfiguracni soubor.");
            }
        }

        return configManager;
    }

    /**
     * Navrati pripojeni k databazi
     * @return instance pripojeni k databazi
     */
    public static Connection getDatabaseConnection()
    {
        if(databaseConnection == null)
        {
            ConfigManager configManager = getConfigManager();
            String host = configManager.getString(ConfigManager.CONFIG_DATABASE_HOST);
            String user = configManager.getString(ConfigManager.CONFIG_DATABASE_USER);
            String pass = configManager.getString(ConfigManager.CONFIG_DATABASE_PASS);
            String schema = configManager.getString(ConfigManager.CONFIG_DATABASE_SCHEMA);
            try {
                databaseConnection = DatabaseConnection.getConnection(host, user, pass, schema);
            } catch (SQLException e) {
                throw new RuntimeException("Nepodarilo se navazat spojeni s DB.");
            }
        }

        return databaseConnection;
    }

    /**
     * Vrati model databazove vrstvy
     * @return instance databazoveho modelu
     */
    public static DatabaseModel getDatabaseModel()
    {
        if(databaseModel == null)
        {
            Connection connection = getDatabaseConnection();
            databaseModel = new DatabaseModel(connection);
        }

        return databaseModel;
    }
}
