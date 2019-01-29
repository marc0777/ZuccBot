package zuccbot.zuccante;

import zuccbot.Constants;
import zuccbot.db.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostsDB {
    private static PostsDB singleton = null;

    public static PostsDB getInstance() {
        if (singleton == null) singleton = new PostsDB();
        return singleton;
    }

    private final Connection db;
    private final Logger logger;

    public PostsDB() {
        logger = Logger.getLogger(Constants.BOT_LOGGER);
        db = Database.getInstance();
    }

    public void addPost(Post post) {
        String sql = "INSERT INTO Newsletter(title, description, link, attachments) VALUES(?,?,?,?)";
        try {
           PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setString(1, post.getTitle());
            pstmt.setString(2, post.getDescription());
            pstmt.setString(3, post.getLink());
            pstmt.setString(4, post.serializedAttachments());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "PostsDB: An exception has been caught while trying to add a post...", e);
        }
    }

    public List<Post> getPosts(long from, long howmany) {
        List<Post> posts = new LinkedList<>();
        try {
            PreparedStatement pstmt = db.prepareStatement("SELECT max(id) FROM Newsletter");
            ResultSet rs = pstmt.executeQuery();
            long max = rs.getLong(1);

            if (howmany != -1 && max - howmany > from) from = max - howmany;

            pstmt = db.prepareStatement("SELECT * FROM Newsletter WHERE id > ?");
            pstmt.setLong(1, from);
            rs = pstmt.executeQuery();
            while (rs.next()) posts.add(new Post(
                    rs.getLong("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("link"),
                    rs.getString("attachments")));
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "PostsDB: An exception has been caught while retrieving all posts...", e);
        }

        return posts;
    }

    public boolean containsPost(String link) {
        String sql = "SELECT * FROM Newsletter WHERE link = ?";
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setString(1, link);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "PostsDB: An exception has been caught while searching for a post...", e);
        }
        return false;
    }
}
