package db;

import java.sql.*;

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
        String sql = "INSERT INTO Utente(idTelegram) VALUES(?)";
        PreparedStatement pstmt;
        try {
            pstmt = db.prepareStatement(sql);
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
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
        }
    }

}
