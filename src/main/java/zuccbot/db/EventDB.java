package zuccbot.db;

import zuccbot.Constants;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class EventDB {
    private static EventDB singleton = null;
    private final Connection db;
    private final Logger logger;
    private Statement commands;
    private int eventID;
    private final String[] types = {"event","homework","activity","test","misshour"};
    private final long week = 60*60*24*7*1000;
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
    /**
     * The function adds an event in the database by type, String[] params contains the params the user inserted when the function is called.
     * @return true if the insert is successful
     */
    public boolean addEvent(String type,String[] params) {
        try{
            eventID++;
            int p=0;
            char t = type.charAt(0);
            if(t=='e'){
                t = params[p++].charAt(0);
                int i = 0;
                switch(t){
                    case'm':
                        i++;
                    case't':
                        i++;
                    case 'a':
                        i++;
                    case 'h':
                        i++;
                }
                type=types[i];
            }
            String classID = params[p++];
            String date= params[p++];
            String sqlEvent = "INSERT INTO Events(ID,type,class,section,date) VALUES(?,?,?,?,?);";
            String sqlType;
            PreparedStatement pstmt;
            if(notDate(date) || notClass(classID)){
                return false;
            }
            try {
                pstmt = db.prepareStatement(sqlEvent);
                pstmt.setInt(1, eventID);
                pstmt.setString(2, type);
                pstmt.setString(3, classID.substring(0,1));
                pstmt.setString(4, classID.substring(1));
                pstmt.setLong(5, dateToInt(date));
                pstmt.executeUpdate();
                String subject;
                String argument;
                //commands.executeUpdate(sqlEvent);
                switch(t){
                    case 'h':
                        subject= params[p++];
                        if(notSubject(subject)){
                            return false;
                        }
                        String text = chain(params,p);
                        sqlType = "INSERT INTO Homework(ID,subject,text) VALUES(?,?,?);";
                        pstmt = db.prepareStatement(sqlType);
                        pstmt.setInt(1, eventID);
                        pstmt.setString(2, subject);
                        pstmt.setString(3, text);
                        break;
                    case 'a':
                        argument = chain(params,p);
                        sqlType = "INSERT INTO Activities(ID,argument) VALUES(?,?);";
                        pstmt = db.prepareStatement(sqlType);
                        pstmt.setInt(1, eventID);
                        pstmt.setString(2, argument);
                        break;
                    case 't':
                        subject= params[p++];
                        argument = p<params.length ? chain(params,p) : "";
                        sqlType = "INSERT INTO Tests(ID,subject,arguments) VALUES(?,?,?);";
                        pstmt = db.prepareStatement(sqlType);
                        pstmt.setInt(1, eventID);
                        pstmt.setString(2, subject);
                        pstmt.setString(3, argument);
                        break;
                    case 'm':
                        int hourN = Integer.parseInt(params[p++]);
                        subject =p<params.length ? params[p] : "";
                        sqlType = "INSERT INTO MissHours(ID,hournumber,subject) VALUES(?,?,?);";
                        pstmt = db.prepareStatement(sqlType);
                        pstmt.setInt(1, eventID);
                        pstmt.setInt(2, hourN);
                        pstmt.setString(3, subject);
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

    /*
     * @return This function returns an ArrayList, it contains all the homework found
     */
    public ArrayList<String> getHomework(String[] params){
        ArrayList<String> res = new ArrayList<>();
        String classID=params[0];
        long time = System.currentTimeMillis();
        try {
            PreparedStatement pstmt = db.prepareStatement("SELECT *  FROM Events join Homework using(ID) WHERE  type=\"homework\" and class=?  and section=? and date BETWEEN ? and ?");
            pstmt.setString(1,classID.substring(0,1));
            pstmt.setString(2,classID.substring(1));
            pstmt.setLong(3, time);
            pstmt.setLong(4, time+week);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                res.add(dateToString( rs.getLong("date") ) +" "+rs.getString("subject")+" "+rs.getString("text"));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "EventDB: An exception has been caught while trying to get homework...", e);
        }
        return res;
    }

    /**
     * This function gets the activities stored in the database
     * @param params An array of strings containing the class ID and the date
     * @return An ArrayList cointaining the future Activities
     */
    public ArrayList<String> getActivity(String[] params){
        ArrayList<String> res = new ArrayList<>();
        String classID = params[0];
        long time = dateToInt(params[1]);
        try{
            PreparedStatement pstmt = db.prepareStatement("SELECT *  FROM Events join Activities using(ID) WHERE  type=\"activity\" and class=? and section=? and date>?");
            pstmt.setString(1,classID.substring(0,1));
            pstmt.setString(2,classID.substring(1));
            pstmt.setLong(3,time);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                res.add(dateToString( rs.getLong("date") ) +" "+rs.getString("argument"));
            }
        }catch (SQLException e) {
            logger.log(Level.SEVERE, "EventDB: An exception has been caught while trying to get activities...", e);
        }
        return res;
    }

    /**
     * This function gets the Tests stored in the databese
     * @param params An array of string containing the classID and the date
     * @return An ArrayList cointaining the future Tests
     */
    public ArrayList<String> getTest(String[] params){
        ArrayList<String> res = new ArrayList<>();
        String classID = params[0];
        long time = System.currentTimeMillis();
        try{
            PreparedStatement pstmt = db.prepareStatement("SELECT *  FROM Events join Tests using(ID) WHERE  type=\"test\" and class=? and section=? and date>?");
            pstmt.setString(1,classID.substring(0,1));
            pstmt.setString(2,classID.substring(1));
            pstmt.setLong(3,time);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                res.add(dateToString( rs.getLong("date") ) +" "+rs.getString("subject")+" "+rs.getString("arguments"));
            }
        }catch (SQLException e) {
            logger.log(Level.SEVERE, "EventDB: An exception has been caught while trying to get tests...", e);
        }
        return res;
    }

    public ArrayList<String> getMissH(String[] params){
        ArrayList<String> res = new ArrayList<>();
        String classID = params[0];
        long time =System.currentTimeMillis();
        try{
            PreparedStatement pstmt = db.prepareStatement("SELECT *  FROM Events join MissHours using(ID) WHERE  type=\"misshour\" and class=? and section=? and date>?");
            pstmt.setString(1,classID.substring(0,1));
            pstmt.setString(2,classID.substring(1));
            pstmt.setLong(3,time);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                res.add(dateToString( rs.getLong("date") ) +" alla "+rs.getString("hournumber")+" ora "+rs.getString("subject"));
            }
        }catch (SQLException e) {
            logger.log(Level.SEVERE, "EventDB: An exception has been caught while trying to get MissHours...", e);
        }
        return res;
    }

    /**
     * Concatenates all the Strings together from the position start
     */
    private static String chain(String[] str , int start){
        StringBuilder res= new StringBuilder();
        for(int i = start , l = str.length; i<l;i++) res.append(str[i]).append(" ");
        return res.toString();
    }

    /**
     * This function checks if the date String is typed correctly
     * @param date the date String to check
     * @return true if the date String is wrong
     */
    private boolean notDate(String date){
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            format.parse(date);
            return false;
        } catch (ParseException e) {
            return true;
        }
    }

    /**
     * This function checks if the class String exists in the database
     * @param clas the class to check
     * @return true if the class String is wrong
     */
    private boolean notClass(String clas){
        try {
            PreparedStatement pstmt = db.prepareStatement("SELECT count(*) FROM TimeTable WHERE class = ? and section = ?");
            pstmt.setString(1, clas.substring(0,1));
            pstmt.setString(1, clas.substring(1));
            ResultSet rs = pstmt.executeQuery();
            return (rs.getInt(0)==0);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "EventDB: An exception has been caught while trying to get homework...", e);
        }
        return false;
    }

    /**
     * This function checks if the subject String exists in the database
     * @param subject the subject to check
     * @return true if the subject String is wrong
     */
    private boolean notSubject(String subject){
        try {
            PreparedStatement pstmt = db.prepareStatement("SELECT count(*) FROM TimeTable WHERE subject = ?");
            pstmt.setString(1, subject);
            ResultSet rs = pstmt.executeQuery();
            return (rs.getInt(0)==0);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "EventDB: An exception has been caught while trying to get subject...", e);
        }
        return false;
    }

    private long dateToInt(String date){
        String[] s = date.split("-");
        return new Date(Integer.parseInt(s[2])-1900, Integer.parseInt(s[1])-1, Integer.parseInt(s[0])).getTime();
    }

    private String dateToString(long date){
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date(date));
    }
}
