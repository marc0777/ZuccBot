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
import zuccbot.graphics.TimeTableGraphic;
import zuccbot.zuccante.Post;
import zuccbot.zuccante.PostsDB;

import java.io.File;
import java.sql.PreparedStatement;
import java.util.ArrayList;
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


    protected void addEvent(Update upd) {
        EventDB edb = EventDB.getInstance();
        Long id = upd.getMessage().getChatId();
        String[] param = upd.getMessage().getText().toLowerCase().split("\\s");
        if(edb.addEvent("event",param)){
            sendText("Hai aggiunto un evento", id);
        }
        else{
            sendText("Il comando non è andato a buon fine", id);
        }
    }
    protected void addHomework(Update upd) {
        EventDB edb = EventDB.getInstance();
        Long id = upd.getMessage().getChatId();
        String[] param = upd.getMessage().getText().toLowerCase().split("\\s");
        if(edb.addEvent("homework",param)){
            sendText("Hai aggiunto dei compiti", id);
        }
        else{
            sendText("Il comando non è andato a buon fine", id);
        }
    }
    protected void addActivity(Update upd) {
        EventDB edb = EventDB.getInstance();
        Long id = upd.getMessage().getChatId();
        String[] param = upd.getMessage().getText().toLowerCase().split("\\s");
        if(edb.addEvent("activity",param)){
            sendText("Hai aggiunto un attività", id);
        }
        else{
            sendText("Il comando non è andato a buon fine", id);
        }
    }

    protected  void addTest(Update upd){
        EventDB edb = EventDB.getInstance();
        Long id = upd.getMessage().getChatId();
        String[] param = upd.getMessage().getText().toLowerCase().split("\\s");
        if(edb.addEvent("test",param)){
            sendText("Hai aggiunto una verifica", id);
        }
        else{
            sendText("Il comando non è andato a buon fine", id);
        }
    }

    protected  void addMissHour(Update upd){
        EventDB edb = EventDB.getInstance();
        Long id = upd.getMessage().getChatId();
        String[] param = upd.getMessage().getText().toLowerCase().split("\\s");
        if(edb.addEvent("misshour",param)){
            sendText("Hai aggiunto un'ora buca", id);
        }
        else{
            sendText("Il comando non è andato a buon fine", id);
        }

    }

    protected void homework(Update upd) {
        EventDB edb = EventDB.getInstance();
        Long id = upd.getMessage().getChatId();
        ArrayList<String> hw = edb.getHomework(upd.getMessage().getText().toLowerCase().split("\\s"));
        if(hw.isEmpty()){
            sendText("Non sono stati registrati compiti.", id);
        }
        else{
            sendText("Ecco i tuoi compiti.", id);
            for(int i =0 ; i<hw.size() ; i++){
                sendText(hw.get(i), id);
            }
        }
        logger.info("Sent homework to: " + id);
    }
    protected void activities(Update upd){
        EventDB edb = EventDB.getInstance();
        Long id = upd.getMessage().getChatId();
        ArrayList<String> hw = edb.getActivity(upd.getMessage().getText().toLowerCase().split("\\s"));
        if(hw.isEmpty()){
            sendText("Non ci sono attività in programma.", id);
        }
        else{
            sendText("Ecco le attività in programma.", id);
            for(int i =0 ; i<hw.size() ; i++){
                sendText(hw.get(i), id);
            }
        }
        logger.info("Sent activities to: " + id);

    }

    protected void misshours(Update upd){
        EventDB edb = EventDB.getInstance();
        Long id = upd.getMessage().getChatId();
        ArrayList<String> hw = edb.getMissH(upd.getMessage().getText().toLowerCase().split("\\s"));
        if(hw.isEmpty()){
            sendText("Non ci sono ore buche.", id);
        }
        else{
            sendText("Ecco le tue ore buche.", id);
            for(int i =0 ; i<hw.size() ; i++){
                sendText(hw.get(i), id);
            }
        }
        logger.info("Sent missed hours to: " + id);

    }

    protected void tests(Update upd){
        EventDB edb = EventDB.getInstance();
        Long id = upd.getMessage().getChatId();
        ArrayList<String> hw = edb.getTest(upd.getMessage().getText().toLowerCase().split("\\s"));
        if(hw.isEmpty()){
            sendText("Non sono stati registrate verifiche.", id);
        }
        else{
            sendText("Ecco le tue verifiche.", id);
            for(int i =0 ; i<hw.size() ; i++){
                sendText(hw.get(i), id);
            }
        }
        logger.info("Sent tests to: " + id);

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
        if(ctx.update().getMessage().getText().length()>8){
            executeTime(ctx);
        }else{
            silent.forceReply("Specifica la classe per piacere!",ctx.chatId());
        }
    }
    private void executeTime(MessageContext ctx){
        TimeTablesDB timeTablesDB = TimeTablesDB.getInstance();
        TimeTableGraphic graphic = new TimeTableGraphic();
        String[] userMessage = clearMes(ctx.update().getMessage().getText().substring(8));
        File file = null;
        try {
            file = graphic.printImage(timeTablesDB.getDate(Integer.parseInt(userMessage[0]), userMessage[1]));
        } catch (IOException e) {
            logger.log(SEVERE, "Failed to create time table picture.", e);
        }

        if(file!=null){
            sendPhoto(file, "Ecco il tuo orario!", ctx.chatId());
            if (!file.delete()) {
                logger.log(SEVERE,"Failed to delete time table picture.");
            }
        }
    }

    protected void getTodaysTime(MessageContext ctx) {
        int day = LocalDate.now().getDayOfWeek().getValue() - 1;
        TimeTablesDB timeTablesDB = TimeTablesDB.getInstance();
        String[] userMessage = clearMes(ctx.update().getMessage().getText().substring(11));
        Records[] classes = timeTablesDB.getDayClasses(Integer.parseInt(userMessage[0]), userMessage[1], day);
        StringBuilder builder = new StringBuilder();
        if(classes.length > 0) {
            boolean first = true;
            for (Records rec : classes) {
                if (rec != null) {
                    if (first) first = false;
                    else builder.append('\n');
                    builder.append(rec.buildMessage());
                }
            }
        }
        else builder.append("Non ci sono lezioni oggi!");
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

    private static String truncate(String input, int length) {
        if (input.length() > (length - 3)) input = input.substring(0, length) + "...";
        return input;
    }

}
