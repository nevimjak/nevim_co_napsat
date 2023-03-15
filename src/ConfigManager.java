import java.util.HashMap;
import java.util.Scanner;

/**
 * Spravce konfigurace
 */
public class ConfigManager {

    public static final String CONFIG_DATABASE_HOST = "database_host";
    public static final String CONFIG_DATABASE_USER = "database_user";
    public static final String CONFIG_DATABASE_PASS = "database_pass";
    public static final String CONFIG_DATABASE_SCHEMA = "database_schema";

    private HashMap<String, Object> config;

    /**
     * Ziska retezec zadaneho nastaveni
     * @param path identifikator nastaveni
     * @return hodnota nastaveni
     */
    public String getString(String path) {
        return (String) this.config.get(path);
    }

    /**
     * Nacteni konfigurace do pameti
     * @param scan
     */
    public void loadConfig(Scanner scan) {
        this.config = new HashMap<String, Object>();
        while (scan.hasNext()) {
            String line = scan.nextLine();

            String[] config = line.split("=", 2);
            if (config.length == 1) {
                this.config.put(config[0], null);
            } else {
                this.config.put(config[0], config[1]);
            }
        }
    }

}
