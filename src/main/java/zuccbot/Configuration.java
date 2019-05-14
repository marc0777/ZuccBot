package zuccbot;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Configuration {
    private static Configuration singleton = null;

    public static Configuration getInstance() {
        if (singleton == null) singleton = new Configuration();
        return singleton;
    }

    private final Properties properties;


    private final static String BOT_TOKEN = "botToken";
    private static final String BOT_NAME = "botName";
    private static final String CREATOR_ID = "botCreatorId";
    private static final String PARSER_PERIOD = "parserPeriod";
    private static final String RSS_URL = "rssUrl";
    private static final String LOG_FOLDER = "logFolder";
    private static final String TIMETABLE_PERIOD = "timeTablePeriod";


    private Configuration() {
        properties = new Properties();
        try {
            properties.load(new FileInputStream("app.properties"));
        } catch (IOException e) {
            Logger logger = Logger.getLogger(Constants.BOT_LOGGER);
            logger.log(Level.SEVERE, "Failed to open configuration file! Exiting...",e);
            System.exit(1);
        }
    }

    public String getBotToken() {
        return properties.getProperty(BOT_TOKEN);
    }

    public String getBotName() {
        return properties.getProperty(BOT_NAME);
    }

    public int getCreatorId() {
        return Integer.parseInt(properties.getProperty(CREATOR_ID));
    }

    public long getParserPeriod() {
        return Long.parseLong(properties.getProperty(PARSER_PERIOD));
    }

    public String getRssUrl() {
        return properties.getProperty(RSS_URL);
    }

    public String getLogFolder() {
        return properties.getProperty(LOG_FOLDER);
    }

    public long getTimeTablePeriod() {
        return Long.parseLong(properties.getProperty(TIMETABLE_PERIOD));
    }
}
