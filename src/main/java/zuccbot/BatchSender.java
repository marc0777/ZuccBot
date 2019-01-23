package zuccbot;

import zuccbot.db.SubscribersDB;

import java.util.List;

public class BatchSender {
    public static void send() {
        System.out.println("BatchSender: Started.");
        ZuccBotActions actions = ZuccBot.getInstance().getBotActions();
        List<Long> subscribers = SubscribersDB.getInstance().getSubscribers();

        for (long subscriber : subscribers) {
            actions.sendCircolari(subscriber, 0);
        }
        System.out.println("BatchSender: Finished.");
    }
}
