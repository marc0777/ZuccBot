package zuccbot.db;

import zuccbot.Constants;
import zuccbot.timeTables.ClassSection;
import zuccbot.timeTables.PDFParsing;

import java.io.IOException;
import java.sql.*;
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

    /**
     * @param url the url of the timetable pdf file
     */
    public void addTimeTables(String url) {
        String sql = "INSERT INTO TimeTable(class,section,day,hourNumber,subject,room) VALUES(?,?,?,?,?,?)";
        PreparedStatement pstmt;
        try {
            PDFParsing pdf = new PDFParsing(url);
            for (int i = 0; i < pdf.getexitTable().size(); i++) {
                for (int j = 0; j < 6; j++) {
                    for (int k = 0; k < 6; k++) {

                        pstmt = db.prepareStatement(sql);
                        pstmt.setInt(1, Integer.parseInt(pdf.getexitTable().get(i).getClas()));//class number
                        pstmt.setString(2, pdf.getexitTable().get(i).getCourse());//section
                        pstmt.setInt(3, j);//day
                        pstmt.setInt(4, k);//hourNumber
                        pstmt.setString(5, pdf.getexitTable().get(i).getMatrix(k, j).getSubject());//subject
                        pstmt.setString(6, pdf.getexitTable().get(i).getMatrix(k, j).getRoom());//room
                        if (!pdf.getexitTable().get(i).getMatrix(k, j).getRoom().equals("") & !pdf.getexitTable().get(i).getMatrix(k, j).getSubject().equals("")) {
                            pstmt.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException | IOException e) {
            logger.log(Level.SEVERE, "TimeTablesDB: An exception has been caught while trying to add a TimeTable...", e);
        }
    }

    /**
     * @param cs    the user class and section
     * @param day     the current day
     * @return the today subjects
     */
    public Records[] getDayClasses(ClassSection cs, int day) {
        String sql = "SELECT * FROM TimeTable WHERE class = ? AND section =? AND day=? ORDER BY hourNumber";
        Records[] out = new Records[6];
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setInt(1, cs.getClas());
            pstmt.setString(2, cs.getSection());
            pstmt.setInt(3, day);
            ResultSet rs = pstmt.executeQuery();
            int i = 0;
            while (rs.next()) {
                out[i++] = new Records(rs.getInt("class"),
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

    /**
     * @param cs    the user class and section
     * @return a boolean which is false if there is not the class
     */
    public boolean containsClass(ClassSection cs) {
        String sql = "SELECT COUNT(*) AS num FROM TimeTable WHERE class = ? AND section =?";
        boolean out = false;
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setInt(1, cs.getClas());
            pstmt.setString(2, cs.getSection());
            ResultSet rs = pstmt.executeQuery();
            out = rs.getInt("num") > 0;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "TimeTables: An exception has been caught while trying to get the time table...", e);
        }
        return out;
    }

    /**
     * this method deletes all the records from TimeTable table
     */
    public void deleteTimeTable() {
        String sql = "DELETE FROM TimeTable";
        try {
            Statement stmt = db.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "an error occurred while deleting the records from TimeTable table.");
        }
    }

    public Records[][] getDate(ClassSection cs) {
        Records[][] out = new Records[6][6];
        for (int i = 0; i < 6; i++) out[i] = getDayClasses(cs, i);
        return out;
    }

}

