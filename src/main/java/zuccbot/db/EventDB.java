package zuccbot.db;

import zuccbot.Constants;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventDB {
    private static EventDB singleton = null;

    public static EventDB getInstance() {
        if (singleton == null) singleton = new EventDB();
        return singleton;
    }

    private final Connection db;
    private final Logger logger;
    private Statement commands;
    private int eventID;

    public EventDB() {
        logger = Logger.getLogger(Constants.BOT_LOGGER);
        db = Database.getInstance();
        try{
            commands = db.createStatement();
            ResultSet rs = commands.executeQuery("SELECT ID FROM Events ORDER BY ID DESC");
            eventID=rs.getInt("ID");
        }
        catch(SQLException e){
            logger.log(Level.SEVERE, "EventDB: An exception has been caught while getting last eventID...", e);
        }
    }

    public void addEvent(String type,String[] params) {
        eventID++;
        String classID=params[0];
        String date=params[1];
        String sqlEvent = "INSERT INTO Events(type,class,date) VALUES("+type+classID+date+");";
        String sqlType = "";
        try {
            commands.executeUpdate(sqlEvent);
            switch(type.charAt(0)){
                case 'h':
                    String subject= params[2];
                    String text = concat(params,3);
                    sqlType = "INSERT INTO Homework(ID,subject,text) VALUES("+Integer.toString(eventID)+subject+text+");";
                    break;
            }
            commands.executeUpdate(sqlType);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "EventDB: An exception has been caught while trying to add an event...", e);
        }
    }
    private static String concat(String str[] , int start){
        String res="";
        for(int i = start , l = str.length; i<l;i++)res+=str[i];
        return res;
    }
}
