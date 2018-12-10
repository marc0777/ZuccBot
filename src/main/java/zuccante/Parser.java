package zuccante;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private static final String LINK = "link", ITEM = "item";

    public static List<String> readFeed(String url)  throws XMLStreamException, IOException{
        List<String> feed = new ArrayList<>();
        XMLEventReader eventReader = XMLInputFactory.newInstance().createXMLEventReader(new URL(url).openStream());
        String link = "";
        XMLEvent event;

        while (eventReader.hasNext()) {
            event = eventReader.nextEvent();
            if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals(LINK)) {
                event = eventReader.nextEvent();

                if (event instanceof Characters) {
                    link = event.asCharacters().getData();
                }
            } else if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(ITEM)) {
                feed.add(link);
            }
        }
        return feed;
    }

    public static void updatePosts(List<String> feed) throws IOException{
        PostsDB postsDB = PostsDB.getInstance();

        for (String link : feed) {
            if (!postsDB.containsPost(link)) {
                Document document = Jsoup.connect(link).get();
                Elements files = document.select("span.file");
                String description = document.select("div.field.field-name-body.field-type-text-with-summary.field-label-hidden").get(0).text();
                String title = document.select("h1#page-title").get(0).text();
                Post post = new Post(title, description, link);

                for (Element file : files) {
                    post.addAttachment(file.child(1).attr("href"));
                }
                postsDB.addPost(post);
                System.out.println("Added: " + title);
            }
        }
    }

}
