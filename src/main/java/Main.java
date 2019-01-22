import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.logging.BotLogger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class Main {

    public static void main(String[] args) {
        startParser();
        startBot();
        System.out.println("UP!");
    }

    /**
     * Initializes bot's API and register the bot.
     */
    private static void startBot() {
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        BotLogger.setLevel(Level.WARNING);

        try {
            botsApi.registerBot(new ZuccBot());
        } catch (TelegramApiRequestException e) {
            System.err.println("Bot: An exception has been caught while trying to register the bot...");
            e.printStackTrace();
        } finally {
            System.out.println("Bot: Registered!");
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
