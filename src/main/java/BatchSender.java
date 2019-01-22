import db.SubscribersDB;

import java.util.List;

public class BatchSender {
    public static void send() {
        System.out.println("BatchSender: Started.");
        ZuccBot senderBot = ZuccBot.getInstance();
        List<Long> subscribers = SubscribersDB.getInstance().getSubscribers();

        for (long subscriber : subscribers) {
            senderBot.sendCircolari(subscriber, 0);
        }
        System.out.println("BatchSender: Finished.");
    }
}
