package zuccbot.db;

import zuccbot.Constants;

import java.io.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static zuccbot.db.create.sql;

/**
 * Class responsible for the creation and connection to the database.
 * Single tables can then be accessed by using the Connection created by this class.
 */
public class Database {
    private static Connection singleton = null;

    /**
     * Singleton for connecting to bot.db.
     * If a Connection has already been made it just returns it,
     * otherwise it tries to connect.
     * @return Connection to the bot.db
     */
    public static Connection getInstance() {
        if (singleton == null) connect();
        return singleton;
    }

    /**
     * Verify if the bot.db already exists and eventually create it
     * Connects this class to the bot.db instance.
     */
    private static void connect() {
        Logger logger = Logger.getLogger(Constants.BOT_LOGGER);

        //DB type is SQLite
        //DB location is "bot.db" in the project root folder
        String url = "jdbc:sqlite:bot.db";

        //TODO improve performance
        try {
            boolean exist = new File("bot.db").exists();
            singleton = DriverManager.getConnection(url); //DB connection
            if (!exist) {
                for (String command : sql) {
                    Statement stmt = singleton.createStatement();
                    stmt.execute(command);
                }
                logger.info("Database: A new database has been created.");
            }
            logger.info("Database: Connection has been established.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database: An exception has been caught while performing an action...", e);
        }
    }

}
