package zuccbot.db;

import zuccbot.Constants;

import java.sql.*;
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
}
