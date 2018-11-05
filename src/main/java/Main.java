import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.util.List;

public class Main {
    public static List<Post> posts;

    public static void main(String args[]) throws Exception {
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

        posts = Parser.parsePosts(Parser.readFeed("https://www.itiszuccante.gov.it/rss.xml"));

        ZuccBot zuccBot = new ZuccBot();
        botsApi.registerBot(zuccBot);
        System.out.println("UP!");
    }
}
