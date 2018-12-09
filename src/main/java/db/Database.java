package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class responsible for the creation and connection to the database.
 * Single tables can then be accessed by using the Connection created by this class.
 */
public class Database {
    private static Connection singleton;

    /**
     * Singleton for connecting to db.
     * If a Connection has already been made it just returns it,
     * otherwise it tries to connect.
     * @return Connection to the db
     */
    public static Connection getInstance() {
        if (singleton == null) connect();
        return singleton;
    }

    /**
     * Connects this class to the db instance.
     */
    private static void connect() {
        //TODO check if the database already exists and eventually create it
        //DB type is SQLite
        //DB location is "bot.db" in the project root folder
        String url = "jdbc:sqlite:bot.db";
        try {
            singleton = DriverManager.getConnection(url); //DB connection
            System.out.println("Database: Connection has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
