package zuccbot;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static zuccbot.Constants.CONFIGURATION_FILE;

public class Configuration {
    private static Configuration singleton = null;

    public static Configuration getInstance() {
        if (singleton == null) singleton = new Configuration();
        return singleton;
    }

    private static final String BOT_TOKEN = "botToken";
    private static final String BOT_NAME = "botName";
    private static final String CREATOR_ID = "botCreatorId";
    private static final String PARSER_PERIOD = "parserPeriod";
    private static final String RSS_URL = "rssUrl";
    private static final String TIMETABLE_URL = "timeTableUrl";
    private static final String TIMETABLE_PERIOD = "timeTablePeriod";
    private static final String TIMETABLE_LAST = "timeTableLast";
    private static final String LOG_FOLDER = "logFolder";

    private final Properties properties;
    private final Logger logger;

    private Configuration() {
        properties = new Properties();
        logger = Logger.getLogger(Constants.BOT_LOGGER);
        load();
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

    public String getTimeTableUrl() {
        return properties.getProperty(TIMETABLE_URL);
    }

    public long getTimeTablePeriod() {
        return Long.parseLong(properties.getProperty(TIMETABLE_PERIOD));
    }

    public String getTimeTableLast() {
        return properties.getProperty(TIMETABLE_LAST);
    }

    public void setTimeTableLast(String value) {
        properties.setProperty(TIMETABLE_LAST, value);
        save();
    }

    private void load() {
        try {
            properties.load(new FileInputStream(CONFIGURATION_FILE));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to open configuration file! Exiting...", e);
            System.exit(1);
        }
    }

    private void save() {
        try {
            properties.store(new FileOutputStream(CONFIGURATION_FILE), "");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to save configuration file!", e);
        }
    }
}
