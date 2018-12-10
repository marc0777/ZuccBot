package db;

import java.io.File;
import java.sql.*;

/**
 * Class responsible for the creation and connection to the database.
 * Single tables can then be accessed by using the Connection created by this class.
 */
public class Database {
    private static Connection singleton;
    private static final File db= new File("bot.db");
    private static final String url = "jdbc:sqlite:bot.db"; // parameters

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
     * Verify if the db already exists and eventually create it
     * Connects this class to the db instance.
     */
    private static void connect() {
        //DB type is SQLite
        //DB location is "bot.db" in the project root folder
        try {
            singleton = DriverManager.getConnection(url); //DB connection
            if(db.exists()){
                System.out.println("Database: Connection has been established.");
            }else{
                if (singleton != null) {
                    DatabaseMetaData meta = singleton.getMetaData();
                    System.out.println("The driver name is " + meta.getDriverName());
                    System.out.println("A new database has been created and a connection has been established.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * create a table in the db
     * @param sql script for creating a table
     */
    private static void createTable(String sql){
        try(Statement stmt= singleton.createStatement()){
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * select values of a table ofthe db
     * @param sql script to select content of a table
     */
    private static void select(String sql){
        try(PreparedStatement pstmt= singleton.prepareStatement(sql)){
            pstmt.executeQuery();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void insert(String sql, ){
        try(PreparedStatement pstmt= singleton.prepareStatement(sql)){
            pstmt.executeQuery();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
