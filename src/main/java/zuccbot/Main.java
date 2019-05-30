package zuccbot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.logging.BotLogger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

import static java.util.logging.Level.SEVERE;

public class Main {
    private final static Logger logger = Logger.getLogger(Constants.BOT_LOGGER);

    public static void main(String[] args) {
        boolean logToFile = args.length > 0 && args[0].contains("logToFile");
        setupLogger(logToFile);
        startBot();
        startParser();
        logger.info("UP!");
    }

    /**
     *
     * @param logToFile boolean if is true, creates log
     */
    public static void setupLogger(boolean logToFile) {
        if (logToFile) {
            String logFolder = Configuration.getInstance().getLogFolder();
            File directory = new File(logFolder);
            if (!directory.exists()) directory.mkdir();

            String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
            FileHandler fileHandler;
            try {
                fileHandler = new FileHandler(logFolder + "botlog_" + timeStamp + ".log");
                fileHandler.setFormatter(new SimpleFormatter());

                logger.addHandler(fileHandler);
                BotLogger.registerLogger(fileHandler);
            } catch (IOException e) {
                logger.log(SEVERE, "Error while trying to configure file logging...\n", e);
            }
        }
        BotLogger.setLevel(Level.WARNING);
    }

    /**
     * Initializes bot's API and register the bot.
     */
    private static void startBot() {
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(ZuccBot.getInstance());
        } catch (TelegramApiRequestException e) {
            logger.log(SEVERE, "Bot: An exception has been caught while trying to register the bot...\n", e);
        } finally {
            logger.info("Bot: Registered!");
        }
    }

    /**
     * Creates a scheduled executor to run the Parser once every given number of minutes.
     */
    private static void startParser() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Configuration conf = Configuration.getInstance();
        executor.scheduleAtFixedRate(new NewsletterTask(), 0, conf.getParserPeriod(), TimeUnit.MINUTES);
        executor.scheduleAtFixedRate(new TimeTableTask(), 0, conf.getTimeTablePeriod(), TimeUnit.DAYS);
    }


}
