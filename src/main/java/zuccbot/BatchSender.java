package zuccbot;

import zuccbot.db.SubscribersDB;

import java.util.List;

public class BatchSender {
    public static void send() {
        System.out.println("zuccbot.BatchSender: Started.");
        ZuccBot senderBot = ZuccBot.getInstance();
        List<Long> subscribers = SubscribersDB.getInstance().getSubscribers();

        for (long subscriber : subscribers) {
            senderBot.sendCircolari(subscriber, 0);
        }
        System.out.println("zuccbot.BatchSender: Finished.");
    }
}
