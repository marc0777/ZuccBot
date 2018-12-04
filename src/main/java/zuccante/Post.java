package zuccante;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Representation for a single Zuccante's post.
 */
public class Post {
    private final long id; // This value defaults to -1 and it's normally given by the DBMS
    private final String title;
    private final String description;
    private final String link;
    private List<String> attachments; // Link to each attachment, if any

    /**
     * Constructor to be used when retrieving data from the db.
     * Every parameter has to be set at construction time.
     * Attachments needs to be deserialized.
     *
     * @param id          DBMS given post id
     * @param title       post's title
     * @param description post's description
     * @param link        post's url
     * @param attachments serialized attachments
     */
    public Post(long id, String title, String description, String link, String attachments) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.id = id;
        deserializeAttachments(attachments);
    }

    /**
     * Constructor to be used when first creating the post.
     * The id is set to anon significant value, and attachments is initialized empty.
     * @param title post's title
     * @param description post's description
     * @param link post's url
     */
    public Post(String title, String description, String link) {
        this.title = title;
        this.description = description;
        this.link = link;
        attachments = new ArrayList<>();
        id = -1;
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

    public long getId() {
        return id;
    }

    public void addAttachment(String attachment) {
        attachments.add(attachment);
    }

    /**
     * Serializes the post's attachments.
     * @return String containing every attachment's url, separated by ';'
     */
    public String serializedAttachments() {
        StringBuilder out = new StringBuilder();
        for (String link : attachments) out.append(link).append(";");
        return out.toString();
    }

    /**
     * Deserializes post's attachments from a given String.
     * @param serialized String containing every attachment's url, separated by ';'
     */
    private void deserializeAttachments(String serialized) {
        attachments = Arrays.asList(serialized.split(";"));
    }
}
