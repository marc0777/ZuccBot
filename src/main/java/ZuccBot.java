import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ZuccBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

            switch (update.getMessage().getText()) {
                case "/circolari":
                    try {
                        sendCircolari(update.getMessage().getChatId());
                    } catch (TelegramApiException ignored) {}
                    break;
            }

        }
    }

    private void sendCircolari(long to) throws TelegramApiException {
        for(Post post : Main.posts) {
            execute(new SendMessage()
                    .enableMarkdown(true)
                    .disableWebPagePreview()
                    .setText(buildMessage(post))
                    .setChatId(to));

            for (String file : post.getAttachments()) {
                execute(new SendDocument()
                        .setDocument(file)
                        .setChatId(to));
            }
        }

        System.out.println("Sent to: "+to);
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

    private static String truncate (String input, int length) {
        if (input.length() > (length-3)) input =  input.substring(0, length) + "...";
        return input;
    }

    @Override
    public String getBotUsername() {
        return "ZuccanteBot";
    }

    @Override
    public String getBotToken() {
        return "637205359:AAFqt88Md-HBfh90trrrrNa-uwmqBWtbBec";
    }
}
