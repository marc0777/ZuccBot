package zuccbot.db;

import zuccbot.Constants;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PollDB {
    private static PollDB singleton = null;

    public static PollDB getInstance() {
        if (singleton == null) singleton = new PollDB();
        return singleton;
    }

    private final Connection db;
    private final Logger logger;
    private int pollId;

    public PollDB() {
        logger = Logger.getLogger(Constants.BOT_LOGGER);
        db = Database.getInstance();
        try{
            Statement commands = db.createStatement();
            ResultSet rs = commands.executeQuery("SELECT pollId FROM Polls ORDER BY pollId DESC"); //TODO fizxxxxx
            pollId=rs.getInt("pollId");
        }
        catch(SQLException e){
            logger.log(Level.SEVERE, "PollDB: An exception has been caught while getting last PollId...", e);
        }
    }

    public void addPoll(String question, String options){
        pollId++;
        String[] params= options.split("/");

        String sql = "INSERT INTO Pools(pollId,question) VALUES(?,?)";
        PreparedStatement pstmt;
        try {
            pstmt = db.prepareStatement(sql);
            pstmt.setLong(1, pollId);
            pstmt.setString(2, question);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "PollDB: An exception has been caught while trying to add a poll...", e);
        }

        sql= "INSERT INTO PollOptions(pollId, pollOption) VALUES(?,?)";
        try {
            for(int i= 0; i<params.length; i++){
                pstmt = db.prepareStatement(sql);
                pstmt.setLong(1, pollId);
                pstmt.setString(2, params[i]);
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "PollDB: An exception has been caught while trying to add an option...", e);
        }
    }
}
