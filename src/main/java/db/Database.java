package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static Connection singleton;

    public static Connection getInstance() {
        if (singleton == null) connect();
        return singleton;
    }

    private static void connect() {
        try {
            String url = "jdbc:sqlite:bot.db";
            singleton = DriverManager.getConnection(url);
            System.out.println("Connection to database has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
