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

public class ZuccBotActions {
    private final MessageSender sender;
    private final SilentSender silent;

    protected ZuccBotActions(MessageSender sender, SilentSender silent) {
        this.sender = sender;
        this.silent = silent;
    }

    protected void subscribe(MessageContext ctx) {
        SubscribersDB.getInstance().setSubscribed(ctx.chatId(), true);
        System.out.println("Subscribed user: " + ctx.chatId());
    }

    protected void unsubscribe(MessageContext ctx) {
        SubscribersDB.getInstance().setSubscribed(ctx.chatId(), false);
        System.out.println("Unsubscribed user: " + ctx.chatId());
    }

    protected void sendDb(MessageContext ctx) {
        try {
            sender.sendDocument(new SendDocument()
                    .setDocument(new File("bot.db"))
                    .setChatId(ctx.chatId()));
        } catch (TelegramApiException e) {
            System.err.println("An exception has been caught while trying to send the database.");
        }
        System.out.println("Sent db to: " + ctx.chatId());
    }

    protected void updateCircolari(MessageContext ctx) {
        System.out.println("Asked newsletter update from: " + ctx.chatId());
        new Thread(new PeriodicTask()).start();
        silent.send("Aggiornamento delle circolari avviato.", ctx.chatId());
    }

    protected void startUser(MessageContext ctx) {
        long id = ctx.chatId();
        SubscribersDB db = SubscribersDB.getInstance();
        if (!db.contains(id)) db.addSubscriber(id);
        System.out.println("Started user: " + id);
    }

    protected void sendCircolari(MessageContext ctx) {
        String str;
        try {
            str = ctx.arguments()[0];
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            str = "0";
        }

        sendCircolari(ctx.chatId(), Integer.parseInt(str));
    }

    public void sendCircolari(long to, int howmany) {
        long lastRead = SubscribersDB.getInstance().getLastRead(to);
        List<Post> posts = PostsDB.getInstance().getPosts(lastRead, howmany);

        for (Post post : posts) {
            if (post.getId() > lastRead) lastRead = post.getId();
            sendText(buildMessage(post), to);
            for (String file : post.getAttachments()) if (!file.equals("")) sendDocument(file, to);
        }

        if (posts.isEmpty()) sendText("Niente di nuovo!", to);
        else SubscribersDB.getInstance().setLastRead(to, lastRead);

        System.out.println("Sent circolari to: " + to);
    }


    private void sendText(String text, long to) {
        try {
            sender.execute(new SendMessage()
                    .enableMarkdown(true)
                    .disableWebPagePreview()
                    .setText(text)
                    .setChatId(to));
        } catch (TelegramApiException e) {
            System.err.println("An exception has been caught while trying to send the following text: ");
            System.err.println(text);
        }
    }

    private void sendDocument(String file, long to) {
        try {
            sender.sendDocument(new SendDocument()
                    .setDocument(file)
                    .setChatId(to));
        } catch (TelegramApiException e) {
            System.err.println("An exception has been caught while trying to send the following document: ");
            System.err.println(file);
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
