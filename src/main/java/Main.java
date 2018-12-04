import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import zuccante.Parser;

public class Main {

    public static void main(String[] args) throws Exception {
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

        Parser.updatePosts(Parser.readFeed("https://www.itiszuccante.gov.it/rss.xml"));

        ZuccBot zuccBot = new ZuccBot();
        botsApi.registerBot(zuccBot);
        System.out.println("UP!");
    }
}
