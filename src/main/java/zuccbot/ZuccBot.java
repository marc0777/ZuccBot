package zuccbot;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.atomic.AtomicReference;

import static org.telegram.abilitybots.api.objects.Flag.MESSAGE;
import static org.telegram.abilitybots.api.objects.Flag.REPLY;
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
                .action((ctx) -> actions.subscribe(ctx))
                .build();
    }

    public Ability unsubscribe() {
        return Ability
                .builder()
                .name("unsubscribe")
                .info("Smetti di ricevere le circolari automaticamente.")
                .locality(ALL)
                .privacy(PUBLIC)
                .action((ctx) -> actions.unsubscribe(ctx))
                .build();
    }

    public Ability circolari() {
        return Ability
                .builder()
                .name("circolari")
                .info("Ricevi le nuove circolari!")
                .locality(ALL)
                .privacy(PUBLIC)
                .action((ctx) -> actions.sendCircolari(ctx))
                .build();
    }

    public Ability updatecircolari() {
        return Ability
                .builder()
                .name("updatecircolari")
                .info("Forza aggiornamento circolari.")
                .locality(ALL)
                .privacy(ADMIN)
                .action((ctx) -> actions.updateCircolari(ctx))
                .build();
    }

    public Ability getDb() {
        return Ability
                .builder()
                .name("getdb")
                .info("Scarica il database attuale.")
                .locality(ALL)
                .privacy(ADMIN)
                .action((ctx) -> actions.sendDb(ctx))
                .build();
    }

    public Ability start() {
        return Ability
                .builder()
                .name("start")
                .locality(ALL)
                .privacy(PUBLIC)
                .action((ctx) -> actions.startUser(ctx))
                .build();
    }
    public Ability addHomework(){
        return Ability
                .builder()
                .name("addhomework")
                .info("Aggiunge un compito da svolgere per la data indicata.")
                .locality(ALL)
                .privacy(PUBLIC)
                .action((ctx)-> actions.addHomework(ctx))
                .build();
    }
/*
    public Ability homework(){
        return Ability
                .builder()
                .name("homework")
                .info("Scrive i compiti da fare per i prossimi tre giorni")
                .locality(ALL)
                .privacy(PUBLIC)
                .action((ctx)-> actions.homework(ctx))
                .build();
    }
*/
    public Ability feedback(){
        String text= "Invia un feedback agli amministratori!";
        return Ability
                .builder()
                .name("feedback")
                .info("Invia un feedback agli amministratori")
                .locality(ALL)
                .privacy(PUBLIC)
                .action((ctx)-> silent.forceReply(text, ctx.chatId()))
                .reply((udp) -> actions.feedback(udp), MESSAGE, REPLY,
                        upd -> upd.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(getBotUsername()),
                        upd -> {
                            Message reply = upd.getMessage().getReplyToMessage();
                            return reply.hasText() && reply.getText().equalsIgnoreCase(text);
                        })
                .build();
    }

    public Ability tellEverybody() {
        String msg1 = "Rispondi con il messagio da inviare a tutti.";
        String msg2 = "Sei veramente sicuro? (sì)";
        AtomicReference<Update> toSend = new AtomicReference<>();
        return Ability
                .builder()
                .name("telleverybody")
                .info("Invia un messaggio a tutti gli utendi del bot.")
                .locality(ALL)
                .privacy(ADMIN)
                .action((ctx) -> silent.forceReply(msg1, ctx.chatId()))
                .reply((udp) -> {
                            toSend.set(udp);
                            silent.forceReply(msg2, udp.getMessage().getChatId());
                        }, MESSAGE, REPLY,
                        upd -> upd.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(getBotUsername()),
                        upd -> {
                            Message reply = upd.getMessage().getReplyToMessage();
                            return reply.hasText() && reply.getText().equalsIgnoreCase(msg1);
                        })
                .reply((udp) -> actions.tellEverybody(toSend.get()), MESSAGE, REPLY,
                        upd -> upd.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(getBotUsername()),
                        upd -> {
                            Message reply = upd.getMessage().getReplyToMessage();
                            return reply.hasText() && reply.getText().equalsIgnoreCase(msg2);
                        },
                        upd -> upd.getMessage().getText().equalsIgnoreCase("sì"))
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
