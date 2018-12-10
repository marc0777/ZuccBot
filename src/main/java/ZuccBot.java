import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.MessageContext;
import zuccante.Parser;
import zuccante.PostsDB;
import db.SubscribersDB;
import zuccante.Post;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.*;

public class ZuccBot extends AbilityBot {

    public ZuccBot() {
        super(Constants.BOT_TOKEN, Constants.BOT_NAME);
    }

    public Ability circolari() {
        return Ability
                .builder()
                .name("circolari")
                .info("Ricevi le nuove circolari!")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(this::sendCircolari)
                .build();
    }

    public Ability updatecircolari() {
        return Ability
                .builder()
                .name("updatecircolari")
                .info("Forza aggiornamento circolari.")
                .locality(ALL)
                .privacy(ADMIN)
                .action(this::updateCircolari)
                .build();
    }

    public Ability start() {
        return Ability
                .builder()
                .name("start")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx -> startUser(ctx.chatId()))
                .build();
    }

    private void updateCircolari(MessageContext ctx) {
        new Thread(new Parser()).start();
        silent.send("Aggiornamento delle circolari avviato.", ctx.chatId());
    }

    private void startUser(long id) {
        SubscribersDB db = SubscribersDB.getInstance();
        if (!db.contains(id)) db.addSubscriber(id);
    }

    private void sendCircolari(MessageContext ctx) {
        long to = ctx.chatId();
        String str;
        try {
            str = ctx.arguments()[0];
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            str = "0";
        }

        int howmany = Integer.parseInt(str);
        long lastRead = SubscribersDB.getInstance().getLastRead(to);
        List<Post> posts = PostsDB.getInstance().getPosts(lastRead, howmany);

        for (Post post : posts) {
            if (post.getId() > lastRead) lastRead = post.getId();
            sendText(buildMessage(post), to);
            for (String file : post.getAttachments()) if (!file.equals("")) sendDocument(file, to);
        }

        if (posts.isEmpty()) sendText("Niente di nuovo!", to);
        else SubscribersDB.getInstance().setLastRead(to, lastRead);

        System.out.println("Sent to: " + to);
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

    @Override
    public int creatorId() {
        return Constants.CREATOR_ID;
    }
}
