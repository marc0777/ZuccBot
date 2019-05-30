package zuccbot;

import zuccbot.db.SubscribersDB;

import java.util.List;
import java.util.logging.Logger;

public class BatchSender {
    public static void send() {
        Logger logger = Logger.getLogger(Constants.BOT_LOGGER);
        logger.info("BatchSender: Started.");
        ZuccBotActions actions = ZuccBot.getInstance().getBotActions();
        List<Long> subscribers = SubscribersDB.getInstance().getSubscribers();
        for (long subscriber : subscribers) {
            actions.sendCircolari(subscriber, -1, false);
        }
        logger.info("BatchSender: Finished.");
    }
}
