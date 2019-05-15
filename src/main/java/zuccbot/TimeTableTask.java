package zuccbot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import zuccbot.db.TimeTablesDB;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static zuccbot.Main.timeTableURL;

public class TimeTableTask implements Runnable {

    @Override
    public void run() {
        Logger logger = Logger.getLogger(Constants.BOT_LOGGER);
        Document document = null;
        try {
            document = Jsoup.connect("https://www.itiszuccante.gov.it/orario-delle-lezioni").get();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An exception has been caught while trying to download timetables web page.", e);
        }
        Elements span = document.select("span.file");
        String link = span.get(0).child(1).attr("href");
        if (!link.equals(timeTableURL)) {
            logger.info("New timetables found! Updating DB...");
            try {
                TimeTablesDB.getInstance().addTimeTables(link);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "An exception has been caught while trying to add timetables to the DB.", e);
            }
            timeTableURL = link;
        }
    }
}
