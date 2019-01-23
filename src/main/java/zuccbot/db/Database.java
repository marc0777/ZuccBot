package zuccbot.db;

import zuccbot.Constants;

import java.io.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class responsible for the creation and connection to the database.
 * Single tables can then be accessed by using the Connection created by this class.
 */
public class Database {
    private static Connection singleton = null;
    private static Logger logger = Logger.getLogger(Constants.BOT_LOGGER);
    private static BufferedReader reader;
    static {
        try {
            reader = new BufferedReader(new FileReader("create.sql"));
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "Database: An exception has been caught while trying to open commands file...", e);
        }
    }

    private static final File db = new File("bot.db");
    private static final String url = "jdbc:sqlite:bot.db"; // parameters

    /**
     * Singleton for connecting to zuccbot.db.
     * If a Connection has already been made it just returns it,
     * otherwise it tries to connect.
     * @return Connection to the zuccbot.db
     */
    public static Connection getInstance() {
        if (singleton == null) connect();
        return singleton;
    }

    /**
     * Verify if the zuccbot.db already exists and eventually create it
     * Connects this class to the zuccbot.db instance.
     */
    private static void connect() {
        //DB type is SQLite
        //DB location is "bot.zuccbot.db" in the project root folder
        try {
            boolean exist = db.exists();
            singleton = DriverManager.getConnection(url); //DB connection
            if (!exist) {
                String line;
                String file= "";
                while ((line = reader.readLine()) != null){
                    file += line;
                }
                String[] commands = file.split(";");
                for (String command : commands) {
                    action(command);
                }
                logger.info("Database: A new database has been created.");
            }
            logger.info("Database: Connection has been established.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database: An exception has been caught while performing an action...", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Database: An exception has been caught while trying to read commands from file...", e);
        }
    }

    /**
     * do all actions written to the sql script (create and delete tables, select, insert, update, delete)
     * use this also with transactions and BLOB type
     * @param sql script
     */
    public static void action(String sql) {
        try(Statement stmt= singleton.createStatement()){
            stmt.execute(sql);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database: An exception has been caught while performing an action...", e);
        }
    }
}
