package zuccbot;

import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import zuccbot.db.SubscribersDB;
import zuccbot.zuccante.Post;
import zuccbot.zuccante.PostsDB;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ZuccBotActions {
    private final MessageSender sender;
    private final SilentSender silent;
    private final Logger logger;

    protected ZuccBotActions(MessageSender sender, SilentSender silent) {
        this.sender = sender;
        this.silent = silent;

        logger = Logger.getLogger(Constants.BOT_LOGGER);
    }

    protected void subscribe(MessageContext ctx) {
        SubscribersDB.getInstance().setSubscribed(ctx.chatId(), true);
        sendText("Da ora riceverai in automatico le nuove circolari.", ctx.chatId());
        logger.info("Subscribed user: " + ctx.chatId());
    }

    protected void unsubscribe(MessageContext ctx) {
        SubscribersDB.getInstance().setSubscribed(ctx.chatId(), false);
        sendText("Smetterai di ricevere in automatico le nuove circolari.", ctx.chatId());
        logger.info("Unsubscribed user: " + ctx.chatId());
    }

    protected void sendDb(MessageContext ctx) {
        sendDocument(new File("bot.db"), ctx.chatId());
        logger.info("Sent db to: " + ctx.chatId());
    }

    protected void updateCircolari(MessageContext ctx) {
        logger.info("Asked newsletter update from: " + ctx.chatId());
        new Thread(new PeriodicTask()).start();
        sendText("Aggiornamento delle circolari avviato.", ctx.chatId());
    }

    protected void startUser(MessageContext ctx) {
        long id = ctx.chatId();
        SubscribersDB db = SubscribersDB.getInstance();
        if (!db.contains(id)) db.addSubscriber(id);
        sendText("Benvenuto nel bot dello Zuccante! Usa /commands per vedere tutti i comandi.", ctx.chatId());
        logger.info("Started user: " + id);
    }

    protected void sendCircolari(MessageContext ctx) {
        String[] args = ctx.arguments();
        sendCircolari(ctx.chatId(), (args.length > 0) ? Integer.parseInt(args[0]) : -1, true);
    }

    protected void addEvent(MessageContext ctx) {

    }

    protected void homework(MessageContext ctx) {
        sendText("Ecco i tuoi compiti.", ctx.chatId());
        logger.info("Sent homework to: " + ctx.chatId());
    }



    public void sendCircolari(long to, int howmany, boolean tellIfEmpty) {
        long lastRead = SubscribersDB.getInstance().getLastRead(to);
        List<Post> posts = PostsDB.getInstance().getPosts(lastRead, howmany);

        for (Post post : posts) {
            sendText(buildMessage(post), to);
            for (String file : post.getAttachments()) if (!file.equals("")) sendDocument(file, to);
        }

        if (!posts.isEmpty()) SubscribersDB.getInstance().setLastRead(to, posts.get(posts.size() - 1).getId());
        else if (tellIfEmpty) sendText("Niente di nuovo!", to);

        logger.info("Sent circolari to: " + to);
    }


    private void sendText(String text, long to) {
        try {
            sender.execute(new SendMessage()
                    .enableMarkdown(true)
                    .disableWebPagePreview()
                    .setText(text)
                    .setChatId(to));
        } catch (TelegramApiException e) {
            logger.log(Level.SEVERE, "An exception has been caught while trying to send the following text: " + text, e);
        }
    }

    private void sendDocument(String file, long to) {
        try {
            sender.sendDocument(new SendDocument()
                    .setDocument(file)
                    .setChatId(to));
        } catch (TelegramApiException e) {
            logger.log(Level.SEVERE, "An exception has been caught while trying to send the following document: " + file, e);
        }
    }

    private void sendDocument(File file, long to) {
        try {
            sender.sendDocument(new SendDocument()
                    .setDocument(file)
                    .setChatId(to));
        } catch (TelegramApiException e) {
            logger.log(Level.SEVERE, "An exception has been caught while trying to send the following document: " + file, e);
        }
    }

    private static String buildMessage(Post post) {
        StringBuilder out = new StringBuilder()
                .append("*")
                .append(post.getTitle())
                .append("*\n\n")
                .append(truncate(post.getDescription(), 512))
                .append("\n\n")
                .append(post.getLink());

        int n = post.getAttachments().size();

        if (n == 1) out.append("\n\nSegue un allegato.");
        else if (n > 1) out.append("\n\nSeguono ").append(n).append(" allegati.");

        return out.toString();
    }

    private static String truncate(String input, int length) {
        if (input.length() > (length - 3)) input = input.substring(0, length) + "...";
        return input;
    }
}
