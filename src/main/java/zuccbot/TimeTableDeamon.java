package zuccbot;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import zuccbot.db.TimeTablesDB;
import zuccbot.timeTables.PDFParsing;
import java.io.IOException;
import static zuccbot.Main.timeTableURL;

public class TimeTableDeamon {
    private String url;
    TimeTableDeamon(String url){
        this.url=url;
    }
    public void chechkTimeTable() throws IOException {
        Document document = Jsoup.connect("https://www.itiszuccante.gov.it/orario-delle-lezioni").get();
        Elements span = document.select("span.file");
        String link = span.get(0).child(1).attr("href");
        System.out.println(link);
        if(!link.equals(url)){
            TimeTablesDB temp = new TimeTablesDB();
            temp.addTimeTables(link);
            timeTableURL = link;
        }
        System.out.println(link);
    }
}
