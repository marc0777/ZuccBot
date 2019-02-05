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
        //startBot();
        startParser();
        logger.info("UP!");
    }

    public static void setupLogger(boolean logToFile) {
        if (logToFile) {
            File directory = new File(Constants.LOG_FOLDER);
            if (!directory.exists()) directory.mkdir();

            String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
            FileHandler fileHandler;
            try {
                fileHandler = new FileHandler(Constants.LOG_FOLDER + "botlog_" + timeStamp + ".log");
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
     * Creates a scheduled executor to run the Parser once every {@value Constants#PARSER_PERIOD} minutes.
     */
    private static void startParser() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new PeriodicTask(), 0, Constants.PARSER_PERIOD, TimeUnit.MINUTES);
    }


}
