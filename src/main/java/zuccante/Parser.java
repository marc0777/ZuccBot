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

public class Parser implements Runnable {
    private static final String LINK = "link";
    private static final String ITEM = "item";

    //TODO avoid eventual resource sharing problems.
    @Override
    public void run() {
        System.out.println("Parser: Started.");
        try {
            Parser.updatePosts(Parser.readFeed("https://www.itiszuccante.gov.it/rss.xml"));
        } catch (IOException e) {
            System.err.println("Parser: An exception has been caught while trying to retrieve the RSS feed...");
            e.printStackTrace();
        } catch (XMLStreamException e) {
            System.err.println("Parser: An exception has been caught while trying to decode the RSS feed...");
            e.printStackTrace();
        } finally {
            System.out.println("Parser: Finished.");
        }
    }

    /**
     *
     * @param url URL of a file in the rss
     * @return
     * @throws XMLStreamException
     * @throws IOException
     */
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

    /**
     * check new feeds
     * @param feed URLs of the rss
     * @throws IOException
     */
    public static void updatePosts(List<String> feed) throws IOException {
        PostsDB postsDB = PostsDB.getInstance();

        for (String link : feed) {
            if (!postsDB.containsPost(link)) {
                Document document = Jsoup.connect(link).get();
                Elements files = document.select("span.file");
                String description = document.select("div.field.field-name-body.field-type-text-with-summary.field-label-hidden").get(0).text();
                String title = document.select("h1#page-title").get(0).text();

                Post post = new Post(title, description, link);
                for (Element file : files) post.addAttachment(file.child(1).attr("href"));
                postsDB.addPost(post);
                System.out.println("Parser: Added: " + title);
            }
        }
    }
}
