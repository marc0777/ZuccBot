package zuccbot.zuccante;

import zuccbot.db.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class PostsDB {
    private static PostsDB singleton = null;

    public static PostsDB getInstance() {
        if (singleton == null) singleton = new PostsDB();
        return singleton;
    }

    private final Connection db;

    public PostsDB() {
        db = Database.getInstance();
    }

    public void addPost(Post post) {
        String sql = "INSERT INTO Circolari(titolo, descrizione, link, allegati) VALUES(?,?,?,?)";
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setString(1, post.getTitle());
            pstmt.setString(2, post.getDescription());
            pstmt.setString(3, post.getLink());
            pstmt.setString(4, post.serializedAttachments());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Post> getPosts(long from, int howmany) {
        if(howmany == -1){
            from=0;
        }
        String sql = "SELECT * FROM Circolari WHERE id > ?";
        List<Post> posts = new LinkedList<>();
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setLong(1, from - howmany);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) posts.add(new Post(
                    rs.getLong("id"),
                    rs.getString("titolo"),
                    rs.getString("descrizione"),
                    rs.getString("link"),
                    rs.getString("allegati")));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return posts;
    }

    public boolean containsPost(String link) {
        String sql = "SELECT * FROM Circolari WHERE link = ?";
        try {
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setString(1, link);
            ResultSet rs = pstmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
