import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Post {
    private String title;
    private String description;
    private String link;
    private List<String> attachments;

    public Post(String title, String description, String link) {
        this.title = title;
        this.description = description;
        this.link = link;
        attachments = new ArrayList<>();
    }

    public void addAttachment(String attachment) {
        attachments.add(attachment);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public List<String> getAttachments() {
        return attachments;
    }
}
