package db;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class SubscribersDB {
    private static SubscribersDB singleton = null;

    public static SubscribersDB getInstance() {
        if (singleton == null) singleton = new SubscribersDB();
        return singleton;
    }

    private final Connection db;

    public SubscribersDB() {
        db = Database.getInstance();
    }

    public void addSubscriber(long id) {
        String sqlUtente = "INSERT INTO Utente(idTelegram) VALUES(?)";
        String sqlPreferenze = "INSERT INTO Preferenze(idTelegram) VALUES(?)";
        PreparedStatement pstmt;
        try {
            pstmt = db.prepareStatement(sqlUtente);
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
            pstmt = db.prepareStatement(sqlPreferenze);
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean contains(long id) {
        String sql = "SELECT * FROM Utente WHERE idTelegram = ?";
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public long getLastRead(long id) {
        String sql = "SELECT ultimaCircolareLetta FROM Preferenze WHERE idTelegram = ?";
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.getLong("ultimaCircolareLetta");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void setLastRead(long to, long lastRead) {
        String sql = "UPDATE Preferenze SET ultimaCircolareLetta = ? WHERE idTelegram = ?";
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setLong(1, lastRead);
            pstmt.setLong(2, to);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isSubscribed(long id) {
        String sql = "SELECT circolari FROM Preferenze WHERE idTelegram = ?";
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt("circolari") > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void setSubscribed(long to, boolean subscribed) {
        String sql = "UPDATE Preferenze SET circolari = ? WHERE idTelegram = ?";
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setInt(1, subscribed ? 1 : 0);
            pstmt.setLong(2, to);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Long> getSubscribers() {
        String sql = "SELECT idTelegram FROM Preferenze where circolari = 1";
        List<Long> subscribers = new LinkedList<>();
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) subscribers.add(rs.getLong("idTelegram"));

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return subscribers;
    }
}
