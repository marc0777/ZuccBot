package zuccbot.db;

import zuccbot.Constants;
import zuccbot.timeTables.ClassSection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SubscribersDB {
    private static SubscribersDB singleton = null;

    public static SubscribersDB getInstance() {
        if (singleton == null) singleton = new SubscribersDB();
        return singleton;
    }

    private final Connection db;
    private final Logger logger;

    public SubscribersDB() {
        logger = Logger.getLogger(Constants.BOT_LOGGER);
        db = Database.getInstance();
    }

    /**
     * add a user to the database
     * @param id long refers to a chatId
     */
    public void addSubscriber(long id) {
        String sqlUtente = "INSERT INTO User(idTelegram) VALUES(?)";
        String sqlPreferenze = "INSERT INTO Preferences(idTelegram) VALUES(?)";
        PreparedStatement pstmt;
        try {
            pstmt = db.prepareStatement(sqlUtente);
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
            pstmt = db.prepareStatement(sqlPreferenze);
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SubscribersDB: An exception has been caught while trying to add a subscriber...", e);
        }
    }

    /**
     * search a user in the database
     * @param id long refers to a chatId
     * @return boolean true is the user is in the database
     */
    public boolean contains(long id) {
        String sql = "SELECT * FROM User WHERE idTelegram = ?";
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SubscribersDB: An exception has been caught while searching for a subscriber...", e);
        }
        return false;
    }

    /**
     * getter for the last news read by a user
     * @param id long refers to a chatId
     * @return long
     */
    public long getLastRead(long id) {
        String sql = "SELECT lastReadNewsletter FROM Preferences WHERE idTelegram = ?";
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.getLong("lastReadNewsletter");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SubscribersDB: An exception has been caught while trying to read a subscriber's last read...", e);
        }
        return -1;
    }

    /**
     * setter for the last news read by a user
     * @param to long refers to a chatId
     * @param lastRead long refers to the last newa read by the user
     */
    public void setLastRead(long to, long lastRead) {
        String sql = "UPDATE Preferences SET lastReadNewsletter = ? WHERE idTelegram = ?";
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setLong(1, lastRead);
            pstmt.setLong(2, to);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SubscribersDB: An exception has been caught while trying to set a subscriber's last read...", e);
        }
    }

    /**
     * search if a user is subscribed
     * @param id long refers to a chatId
     * @return boolean true if the user is subscribed
     */
    public boolean isSubscribed(long id) {
        String sql = "SELECT newsletter FROM Preferences WHERE idTelegram = ?";
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt("newsletter") > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SubscribersDB: An exception has been caught while trying to check if subscribed...", e);
        }
        return false;
    }

    /**
     * setter for subscribe a user
     * @param to long refers to a chatId
     * @param subscribed boolean refers to the status "subscribed" of a user
     */
    public void setSubscribed(long to, boolean subscribed) {
        String sql = "UPDATE Preferences SET newsletter = ? WHERE idTelegram = ?";
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setInt(1, subscribed ? 1 : 0);
            pstmt.setLong(2, to);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SubscribersDB: An exception has been caught while trying to set subscription...", e);
        }
    }

    public List<Long> getSubscribers() {
        String sql = "SELECT idTelegram FROM Preferences where newsletter = 1";
        return getLongs(sql);
    }

    public List<Long> getUsers() {
        String sql = "SELECT idTelegram FROM Preferences";
        return getLongs(sql);
    }

    private List<Long> getLongs(String sql) {
        List<Long> subscribers = new LinkedList<>();
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) subscribers.add(rs.getLong("idTelegram"));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "SubscribersDB: An exception has been caught while reading all subscribers...", e);
        }
        return subscribers;
    }

    /**
     * @param cs   the user class and section
     * @param user the userId
     *             This method sets the class and the section in the User table in the database
     */
    public void setUserClass(ClassSection cs, long user) {
        String sql = "UPDATE User SET class = ?, section = ? WHERE idTelegram = ?";
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setInt(1, cs.getClas());
            pstmt.setString(2, cs.getSection());
            pstmt.setLong(3, user);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "an error occured while setting the user class in the database.", e);
        }
    }

    /**
     * @param user the UserId from telegram
     * @return a string with the class and the section of the user
     */
    public ClassSection getUserClass(long user) {
        ClassSection out = new ClassSection();
        String sql = "SELECT class, section FROM User WHERE idTelegram = ?";
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setLong(1, user);
            ResultSet rs = pstmt.executeQuery();
            out.setClas(rs.getInt("class"));
            out.setSection(rs.getString("section"));
            if (out.getClas() == 0 || out.getSection() == null) out = null;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "An error occured while reading the user class.", e);
        }
        return out;
    }
}
