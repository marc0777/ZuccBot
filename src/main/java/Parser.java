import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

public class Parser {
    private static final String LINK = "link";
    private static final String ITEM = "item";

    public static List<String> readFeed(String url) throws XMLStreamException, IOException {
        List<String> feed = new ArrayList<>();
        XMLEventReader eventReader = XMLInputFactory.newInstance().createXMLEventReader(new URL(url).openStream());
        String link = "";

        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals(LINK)) {
                event = eventReader.nextEvent();
                if (event instanceof Characters) link = event.asCharacters().getData();
            } else if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(ITEM)) {
                feed.add(link);
            }
        }

        return feed;
    }

    public static List<Post> parsePosts(List<String> feed) throws IOException {
        List<Post> posts = new ArrayList<>();

        for (String item : feed) {
            Document document = Jsoup.connect(item).get();
            Elements files =  document.select("span.file");
            String description = document.select("div.field.field-name-body.field-type-text-with-summary.field-label-hidden").get(0).text();
            String title = document.select("h1#page-title").get(0).text();

            Post post = new Post(title, description, item);

            for(Element file : files) post.addAttachment(file.child(1).attr("href"));

            posts.add(post);

        }

        return posts;
    }

}
