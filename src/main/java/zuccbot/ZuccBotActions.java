package zuccbot;

import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;							 
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import zuccbot.db.*;
import zuccbot.zuccante.Post;
import zuccbot.zuccante.PostsDB;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;

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
        new Thread(new NewsletterTask()).start();
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
    protected void addTest(MessageContext ctx) {

    }

    protected void addHomework(MessageContext ctx) {
        EventDB edb = EventDB.getInstance();
        if(edb.addEvent("homework",ctx.arguments())){
            sendText("Hai aggiunto dei compiti", ctx.chatId());
        }
        else{
            sendText("Il comando non è andato a buon fine", ctx.chatId());
        }
    }
    protected void addActivity(MessageContext ctx) {

    }

    protected void homework(MessageContext ctx) {
        sendText("Ecco i tuoi compiti.", ctx.chatId());
        logger.info("Sent homework to: " + ctx.chatId());
    }

    protected void feedback(Update upd) {
        long chatId= upd.getMessage().getChatId();
        long textDate= upd.getMessage().getDate();
        long lastDate= FeedbackDB.getInstance().getDate(chatId);

        if (textDate - lastDate > 86400) { // 86400 = 60sec*60min*24hour
            FeedbackDB.getInstance().addFeedback(chatId, upd.getMessage().getText(), textDate);
            sendText("Feedback inviato con successo!", chatId);
            logger.info(chatId + "'s feedback sent to db");
        } else {
            sendText("Feedback non inviato, tempo trascorso dall'ultimo feedback inferiore a 24 ore.", chatId);
            logger.info(chatId + "'s feedback not sent to db");
        }
    }

    protected void createPoll(Update questionUPD, Update optionUPD){
        String questionMSG = questionUPD.getMessage().getText();
        String optionMSG = optionUPD.getMessage().getText();
        PollDB poll = PollDB.getInstance();
    }

    protected void tellEverybody(Update upd) {
        String message = upd.getMessage().getText();
        List<Long> users = SubscribersDB.getInstance().getUsers();
        for(long user : users) {
            sendText(message,user);
            logger.info("sent to: "+user);
        }
        silent.send("Inviato a tutti il messaggio: "+message, upd.getMessage().getChatId());
    }


    public void sendCircolari(long to, int howmany, boolean tellIfEmpty) {
        long lastRead = SubscribersDB.getInstance().getLastRead(to);
        List<Post> posts = PostsDB.getInstance().getPosts(lastRead, howmany);

        for (Post post : posts) {
            sendText(post.buildMessage(), to);
            for (String file : post.getAttachments()) if (!file.equals("")) sendDocument(file, to);
        }

        if (!posts.isEmpty()) SubscribersDB.getInstance().setLastRead(to, posts.get(posts.size() - 1).getId());
        else if (tellIfEmpty) sendText("Niente di nuovo!", to);

        logger.info("Sent circolari to: " + to);
    }

    protected void getTime(MessageContext ctx) {
        TimeTablesDB timeTablesDB = TimeTablesDB.getInstance();
        String[] userMessage = clearMes(ctx.update().getMessage().getText().split(" ")[1]);
        try {
            timeTablesDB.printImage(timeTablesDB.getDate(Integer.parseInt(userMessage[0]), userMessage[1]));
        } catch (IOException e) {
            logger.log(SEVERE, "Failed to create time table picture.", e);
        }
        sendPhoto(new File("timeImage.png"), "Ecco il tuo orario", ctx.chatId());
    }

    protected void getTodaysTime(MessageContext ctx) {
        int day = LocalDate.now().getDayOfWeek().getValue() - 1;
        TimeTablesDB timeTablesDB = TimeTablesDB.getInstance();
        String[] userMessage = clearMes(ctx.update().getMessage().getText().split(" ")[1]);
        Records[] classes = timeTablesDB.getDayClasses(Integer.parseInt(userMessage[0]), userMessage[1], day);
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Records rec : classes) {
            if (rec != null) {
                if (first) first = false;
                else builder.append('\n');
                builder.append(rec.buildMessage());
            }
        }
        sendText(builder.toString(), ctx.chatId());
    }

    private String[] clearMes(String userMessage) {
        userMessage = userMessage.replace(" ", "");
        String[] output = new String[2];
        output[0] = userMessage.substring(0, 1);
        output[1] = userMessage.substring(1).toUpperCase();
        return output;
    }

    private void sendText(String text, long to) {
        try {
            sender.execute(new SendMessage()
                    .enableMarkdown(true)
                    .disableWebPagePreview()
                    .setText(text)
                    .setChatId(to));
        } catch (TelegramApiException e) {
            logger.log(SEVERE, "An exception has been caught while trying to send the following text: " + text, e);
        }
    }

    private void sendDocument(String file, long to) {
        try {
            sender.sendDocument(new SendDocument()
                    .setDocument(file)
                    .setChatId(to));
        } catch (TelegramApiException e) {
            logger.log(SEVERE, "An exception has been caught while trying to send the following document: " + file, e);
        }
    }

    private void sendDocument(File file, long to) {
        try {
            sender.sendDocument(new SendDocument()
                    .setDocument(file)
                    .setChatId(to));
        } catch (TelegramApiException e) {
            logger.log(SEVERE, "An exception has been caught while trying to send the following document: " + file, e);
        }
    }

    private void sendPhoto(File photo, String caption, long to) {
        try {
            sender.sendPhoto(new SendPhoto()
                    .setPhoto(photo)
                    .setCaption(caption)
                    .setChatId(to));
        } catch (TelegramApiException e) {
            logger.log(SEVERE, "An exception has been caught while trying to send the following photo: " + photo, e);
        }
    }

}
