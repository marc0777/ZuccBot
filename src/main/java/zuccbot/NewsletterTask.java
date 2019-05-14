package zuccbot;

import zuccbot.zuccante.Parser;

public class NewsletterTask implements Runnable {
    @Override
    public void run() {
        Parser.parse();
        BatchSender.send();
    }
}
