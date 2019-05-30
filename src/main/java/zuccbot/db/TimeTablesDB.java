package zuccbot.db;

import zuccbot.Constants;
import zuccbot.timeTables.PDFParsing;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TimeTablesDB {
    private static TimeTablesDB singleton = null;

    public static TimeTablesDB getInstance() {
        if (singleton == null) singleton = new TimeTablesDB();
        return singleton;
    }

    private final Connection db;
    private final Logger logger;

    public TimeTablesDB() {
        logger = Logger.getLogger(Constants.BOT_LOGGER);
        db = Database.getInstance();
    }

    public void addTimeTables(String url) throws IOException {

        String sql = "INSERT INTO TimeTable(class,section,day,hourNumber,subject,room) VALUES(?,?,?,?,?,?)";
        PreparedStatement pstmt;
        PDFParsing pdf = new PDFParsing(url);

        try {

            for(int i = 0; i < pdf.getexitTable().size();i++){
                for(int j = 0; j < 6; j++){
                    for(int k = 0; k < 6; k++){

                        pstmt = db.prepareStatement(sql);
                        pstmt.setInt(1, Integer.parseInt(pdf.getexitTable().get(i).getClas()));//class number
                        pstmt.setString(2, pdf.getexitTable().get(i).getCourse());//section
                        pstmt.setInt(3, j);//day
                        pstmt.setInt(4,k);//hourNumber
                        pstmt.setString(5,pdf.getexitTable().get(i).getMatrix(k,j).getSubject());//subject
                        pstmt.setString(6,pdf.getexitTable().get(i).getMatrix(k,j).getRoom());//room
                        if(!pdf.getexitTable().get(i).getMatrix(k,j).getRoom().equals("") & !pdf.getexitTable().get(i).getMatrix(k,j).getSubject().equals("")){
                            pstmt.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "TimeTablesDB: An exception has been caught while trying to add a TimeTable...", e);
        }
    }

    public Records[] getDayClasses(int clas, String section, int day) {
        String sql = "SELECT * FROM TimeTable WHERE class = ? AND section =? AND day=? ORDER BY hourNumber";
        Records[] out = new Records[6];

        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setInt(1, clas);
            pstmt.setString(2,section);
            pstmt.setInt(3,day);
            ResultSet rs = pstmt.executeQuery();
            int i = 0;
            while (rs.next()){
                out[i++] =new Records(rs.getInt("class"),
                        rs.getString("section"),
                        rs.getInt("day"),
                        rs.getInt("hourNumber"),
                        rs.getString("subject"),
                        rs.getString("room"));
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "TimeTables: An exception has been caught while trying to get the time table...", e);
        }

        return out;
    }

    public boolean containsClass(int clas, String section) {
        String sql = "SELECT COUNT(*) AS num FROM TimeTable WHERE class = ? AND section =?";
        boolean out = false;
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setInt(1, clas);
            pstmt.setString(2, section);
            ResultSet rs = pstmt.executeQuery();
            out = rs.getInt("num") > 0;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "TimeTables: An exception has been caught while trying to get the time table...", e);
        }

        return out;
    }

    public void deleteTimeTable(){
        String sql = "DELETE FROM TimeTable";
        PreparedStatement pstmt;
        try{
            pstmt = db.prepareStatement(sql);
            pstmt.executeQuery();
        }catch (SQLException e){
            logger.log(Level.SEVERE, "an error occurred while deleting the records from TimeTable table.");
        }

    }

    /**
     *
     * @param clas the user class
     * @param section the sectoin of the user class
     * @param user the userId
     * @throws SQLException
     * This method sets the class and the section in the User table in the database
     */
    public void setUserClass(int clas, String section, String user){
        String sql = "UPDATE User SET class = ?, section = ? WHERE idTelegram = ?";
        try{
            PreparedStatement pstmt;
            pstmt = db.prepareStatement(sql);
            pstmt.setInt(1, clas);
            pstmt.setString(2, section);
            pstmt.setString(3,user);
            pstmt.executeQuery();
        }catch (SQLException e){
            logger.log(Level.SEVERE, "an error occured while setting the user class in the database.");
        }

    }

    /**
     *
     * @param user the UserId from telegram
     * @return a string with the class and the section of the user
     * @throws SQLException
     */
    public String getUserClass(String user){
        String out ="";
        String sql = "SELECT class, section FROM User WHERE idTelegram = ?";
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setString(1,user);
            ResultSet rs = pstmt.executeQuery();
            out += rs.getString("class");
            out += rs.getString("section");
        }catch(SQLException e){
            logger.log(Level.SEVERE, "an error occured while reading the user class.");
        }
        return out;
    }

    public Records[][] getDate(int clas, String section){
        Records[][] out= new Records[6][6];
        for(int i =0; i<6;i++) out[i] = getDayClasses(clas, section, i);
        return out;
    }

}

