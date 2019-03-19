package zuccbot.db;

import zuccbot.Constants;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventDB {
    private static EventDB singleton = null;
    private final Connection db;
    private final Logger logger;
    private Statement commands;
    private int eventID;
    private final String[] types = {"event","homework","activity"};
    public static EventDB getInstance() {
        if (singleton == null) singleton = new EventDB();
        return singleton;
    }


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

    public boolean addEvent(String type,String[] params) {
        try{
            eventID++;
            int p=0;
            char t = type.charAt(0);
            if(t=='e'){
                t = params[p++].charAt(0);
                int i = 0;
                switch(t){
                    case 'a':
                        i++;
                    case 'h':
                        i++;
                }
                type=types[i];
            }
            String classID = params[p++];
            String date=params[p++];
            String sqlEvent = "INSERT INTO Events(ID,type,class,date) VALUES(?,?,?,?);";
            String sqlType = "";
            PreparedStatement pstmt;
            if(notClass(classID)||notDate(date)){
                return false;
            }
            try {
                pstmt = db.prepareStatement(sqlEvent);
                pstmt.setInt(1, eventID);
                pstmt.setString(2, type);
                pstmt.setString(3, classID);
                pstmt.setString(4, date);
                pstmt.executeUpdate();
                //commands.executeUpdate(sqlEvent);
                switch(t){
                    case 'h':
                        String subject= params[p++];
                        if(notSubject(subject)){
                            return false;
                        }
                        String text = chain(params,p++);
                        sqlType = "INSERT INTO Homework(ID,subject,text) VALUES(?,?,?);";
                        pstmt = db.prepareStatement(sqlType);
                        pstmt.setInt(1, eventID);
                        pstmt.setString(2, subject);
                        pstmt.setString(3, text);
                        break;
                }
                pstmt.executeUpdate();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "EventDB: An exception has been caught while trying to add an event...", e);
                return false;
            }
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    public ArrayList<String> getHomework(String[] params){
        ArrayList<String> res = new ArrayList<>();
        String classID=params[0];
        try {
            PreparedStatement pstmt = db.prepareStatement("SELECT * FROM events join homework using(ID) WHERE  type=\"homework\" and class=?");
            pstmt.setString(1, classID);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                res.add(rs.getString("date")+" "+rs.getString("subject")+" "+rs.getString("text"));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "EventDB: An exception has been caught while trying to get homework...", e);
        }
        return res;
    }

    private static String chain(String[] str , int start){
        String res="";
        for(int i = start , l = str.length; i<l;i++)res+=str[i]+" ";
        return res;
    }

    private static boolean notDate(String date){
        return false;
    }

    private boolean notClass(String clas){
        try {
            PreparedStatement pstmt = db.prepareStatement("SELECT class FROM TimeTable WHERE class = ?");
            pstmt.setString(1, clas);
            ResultSet rs = pstmt.executeQuery();
            return !rs.wasNull();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "EventDB: An exception has been caught while trying to get homework...", e);
        }
        return true;
    }

    private static boolean notSubject(String subject){
        return false;
    }
}
