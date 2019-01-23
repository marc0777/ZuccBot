package zuccbot;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;

import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.*;

public class ZuccBot extends AbilityBot {
    private static ZuccBot singleton = null;

    public static ZuccBot getInstance() {
        if (singleton == null) singleton = new ZuccBot();
        return singleton;
    }

    private final ZuccBotActions actions;

    public ZuccBot() {
        super(Constants.BOT_TOKEN, Constants.BOT_NAME);
        actions = new ZuccBotActions(sender, silent);
    }

    public Ability subscribe() {
        return Ability
                .builder()
                .name("subscribe")
                .info("Ricevi le circolari automaticamente!")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(actions::subscribe)
                .build();
    }

    public Ability unsubscribe() {
        return Ability
                .builder()
                .name("unsubscribe")
                .info("Smetti di ricefere le circolari automaticamente.")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(actions::unsubscribe)
                .build();
    }

    public Ability circolari() {
        return Ability
                .builder()
                .name("circolari")
                .info("Ricevi le nuove circolari!")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(actions::sendCircolari)
                .build();
    }

    public Ability updatecircolari() {
        return Ability
                .builder()
                .name("updatecircolari")
                .info("Forza aggiornamento circolari.")
                .locality(ALL)
                .privacy(ADMIN)
                .action(actions::updateCircolari)
                .build();
    }

    public Ability getDb() {
        return Ability
                .builder()
                .name("getdb")
                .info("Scarica il database attuale.")
                .locality(ALL)
                .privacy(ADMIN)
                .action(actions::sendDb)
                .build();
    }

    public Ability start() {
        return Ability
                .builder()
                .name("start")
                .locality(ALL)
                .privacy(PUBLIC)
                .action(actions::startUser)
                .build();
    }

    @Override
    public int creatorId() {
        return Constants.CREATOR_ID;
    }

    public ZuccBotActions getBotActions() {
        return actions;
    }
}
