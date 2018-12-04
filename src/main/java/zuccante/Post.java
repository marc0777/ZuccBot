package zuccante;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Post {
    private final long id;
    private final String title;
    private final String description;
    private final String link;
    private List<String> attachments;

    public Post(long id, String title, String description, String link, String attachments) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.id = id;
        deserializeAttachments(attachments);
    }

    public Post(String title, String description, String link) {
        this.title = title;
        this.description = description;
        this.link = link;
        attachments = new ArrayList<>();
        id = -1;
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

    public String serializedAttachments() {
        StringBuilder out = new StringBuilder();
        for (String link : attachments) out.append(link).append(";");
        return out.toString();
    }

    private void deserializeAttachments(String serialized) {
        attachments = Arrays.asList(serialized.split(";"));
    }

    public long getId() {
        return id;
    }
}
