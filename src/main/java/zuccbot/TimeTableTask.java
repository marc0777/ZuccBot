package zuccbot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import zuccbot.db.TimeTablesDB;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TimeTableTask implements Runnable {
    @Override
    public void run() {
        Logger logger = Logger.getLogger(Constants.BOT_LOGGER);
        Configuration conf = Configuration.getInstance();
        Document document = null;
        try {
            document = Jsoup.connect(conf.getTimeTableUrl()).get();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "An exception has been caught while trying to download timetables web page.", e);
        }
        Elements span = document.select("span.file");
        String link = span.get(0).child(1).attr("href");
        if (!link.equals(conf.getTimeTableLast())) {
            logger.info("New timetables found! Updating DB...");
            try {
                TimeTablesDB.getInstance().deleteTimeTable();
                TimeTablesDB.getInstance().addTimeTables(link);
            } catch (IOException|SQLException e) {
                logger.log(Level.SEVERE, "An exception has been caught while trying to add timetables to the DB.", e);
            }
            conf.setTimeTableLast(link);
        }
    }
}
