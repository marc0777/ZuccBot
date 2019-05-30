package zuccbot.db;

import zuccbot.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * class to handle Feedback's table of the database
 */
public class FeedbackDB {
    private static FeedbackDB singleton = null;

    public static FeedbackDB getInstance() {
        if (singleton == null) singleton = new FeedbackDB();
        return singleton;
    }

    private final Connection db;
    private final Logger logger;

    public FeedbackDB() {
        logger = Logger.getLogger(Constants.BOT_LOGGER);
        db = Database.getInstance();
    }

    /**
     * geetter for the date of the last feedback
     * @param id long refers to a chatId
     * @return long number of past seconds
     */
    public long getDate(long id) {
        String sql = "SELECT date FROM Feedback WHERE idTelegram = ?";
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.getLong("date");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "FeedbackDB: An exception has been caught while trying to read the date of an user's last feedback...", e);
        }
        return -1;
    }

    /**
     * add a feedback to the database
     * @param id long refers to a chatId
     * @param text String refers to the message of a feedback
     * @param date long refers to the date of a feedback
     */
    public void addFeedback(long id, String text, long date){
        String sql = "INSERT INTO Feedback(idTelegram,text,date) VALUES(?,?,?)";
        PreparedStatement pstmt;
        try {
            pstmt = db.prepareStatement(sql);
            pstmt.setLong(1, id);
            pstmt.setString(2, text);
            pstmt.setLong(3, date);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "FeedbackDB: An exception has been caught while trying to add an user's feedback...", e);
        }
    }
}
