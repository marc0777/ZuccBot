package db;

import java.io.*;
import java.sql.*;

/**
 * Class responsible for the creation and connection to the database.
 * Single tables can then be accessed by using the Connection created by this class.
 */
public class Database {
    private static Connection singleton;
    private static BufferedReader reader;
    static {
        try {
            reader = new BufferedReader(new FileReader("create.sql"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

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
            if(!db.exists()) {
                singleton = DriverManager.getConnection(url); //DB connection

                if (singleton != null) {
                    DatabaseMetaData meta = singleton.getMetaData();
                    System.out.println("The driver name is " + meta.getDriverName());
                    System.out.println("A new database has been created");
                }
                String line;
                String file= "";
                while ((line = reader.readLine()) != null){
                    file += line;
                }
                String[] commands = file.split(";");
                for (String command : commands) {
                    action(command, "create tables");
                }
            }
            System.out.println("Database: Connection has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * do all actions written to the sql script (create and delete tables, select, insert, update, delete)
     * use this also with transactions and BLOB type
     * @param sql script
     * @param typeOfAction action to do
     */
    public static void action(String sql, String typeOfAction){
        try(Statement stmt= singleton.createStatement()){
            System.out.println(typeOfAction + "in progress");
            stmt.execute(sql);
            System.out.println(typeOfAction + "completed");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
