package zuccbot;

import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.atomic.AtomicReference;

import static org.telegram.abilitybots.api.objects.Flag.MESSAGE;
import static org.telegram.abilitybots.api.objects.Flag.REPLY;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.ADMIN;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

/**
 * contains the abilitys of the bot
 */
public class ZuccBot extends AbilityBot {
    private static ZuccBot singleton = null;

    public static ZuccBot getInstance() {
        if (singleton == null) singleton = new ZuccBot();
        return singleton;
    }

    private final ZuccBotActions actions;

    public ZuccBot() {
        super(Configuration.getInstance().getBotToken(), Configuration.getInstance().getBotName());
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

    public Ability updateorario() {
        return Ability
                .builder()
                .name("updateorario")
                .info("Forza l'aggiornamento dell'orario.")
                .locality(ALL)
                .privacy(ADMIN)
                .action((ctx) -> actions.updateOrario(ctx))
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

    public Ability addEvent() {
        String text = "Inserisci nel seguente formato i parametri: tipo, classe, data, materia e testo";
        return Ability
                .builder()
                .name("addevent")
                .info("Aggiunge un evento.")
                .locality(ALL)
                .privacy(PUBLIC)
                .action((ctx) -> silent.forceReply(text, ctx.chatId()))
                .reply((upd) -> actions.addEvent(upd), MESSAGE, REPLY,
                        upd -> upd.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(getBotUsername()),
                        upd -> {
                            Message reply = upd.getMessage().getReplyToMessage();
                            return reply.hasText() && reply.getText().equalsIgnoreCase(text);
                        })
                .build();
    }

    public Ability addHomework() {
        String text = "Inserisci nel seguente formato i parametri: classe data materia consegna";
        return Ability
                .builder()
                .name("addhomework")
                .info("Aggiunge un compito da svolgere per la data indicata.")
                .locality(ALL)
                .privacy(ADMIN)
                .action((ctx) -> silent.forceReply(text, ctx.chatId()))
                .reply((upd) -> actions.addHomework(upd), MESSAGE, REPLY,
                        upd -> upd.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(getBotUsername()),
                        upd -> {
                            Message reply = upd.getMessage().getReplyToMessage();
                            return reply.hasText() && reply.getText().equalsIgnoreCase(text);
                        })
                .build();
    }


    public Ability addActivitiy() {
        String text = "Inserisci nel seguente formato i parametri: classe data argomento.";
        return Ability
                .builder()
                .name("addactivity")
                .info("Aggiunge un'attività prevista per la data indicata.")
                .locality(ALL)
                .privacy(ADMIN)
                .action((ctx) -> silent.forceReply(text, ctx.chatId()))
                .reply((upd) -> actions.addActivity(upd), MESSAGE, REPLY,
                        upd -> upd.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(getBotUsername()),
                        upd -> {
                            Message reply = upd.getMessage().getReplyToMessage();
                            return reply.hasText() && reply.getText().equalsIgnoreCase(text);
                        })
                .build();
    }

    public Ability addTest() {
        String text = "Inserisci nel seguente formato i parametri: classe data materia [argomento].";
        return Ability
                .builder()
                .name("addtest")
                .info("Aggiunge un'attività prevista per la data indicata.")
                .locality(ALL)
                .privacy(ADMIN)
                .action((ctx) -> silent.forceReply(text, ctx.chatId()))
                .reply((upd) -> actions.addTest(upd), MESSAGE, REPLY,
                        upd -> upd.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(getBotUsername()),
                        upd -> {
                            Message reply = upd.getMessage().getReplyToMessage();
                            return reply.hasText() && reply.getText().equalsIgnoreCase(text);
                        })
                .build();
    }

    public Ability addMissHour() {
        String text = "Inserisci nel seguente formato i parametri: classe data ora [materia].";
        return Ability
                .builder()
                .name("addorabuca")
                .info("Aggiunge un'ora buca per la data indicata.")
                .locality(ALL)
                .privacy(ADMIN)
                .action((ctx) -> silent.forceReply(text, ctx.chatId()))
                .reply((upd) -> actions.addMissHour(upd), MESSAGE, REPLY,
                        upd -> upd.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(getBotUsername()),
                        upd -> {
                            Message reply = upd.getMessage().getReplyToMessage();
                            return reply.hasText() && reply.getText().equalsIgnoreCase(text);
                        })
                .build();
    }

    public Ability homework() {
        String text = "Inserisci la classe di cui vuoi sapere i compiti.";
        return Ability
                .builder()
                .name("homework")
                .info("Scrive i compiti da fare per i prossimi tre giorni")
                .locality(ALL)
                .privacy(PUBLIC)
                .action((ctx) -> silent.forceReply(text, ctx.chatId()))
                .reply((upd) -> actions.homework(upd), MESSAGE, REPLY,
                        upd -> upd.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(getBotUsername()),
                        upd -> {
                            Message reply = upd.getMessage().getReplyToMessage();
                            return reply.hasText() && reply.getText().equalsIgnoreCase(text);
                        })
                .build();
    }

    public Ability activities() {
        String text = "Inserisci la classe di cui vuoi sapere le attività.";
        return Ability
                .builder()
                .name("activities")
                .info("Scrive le attività future in programma della classe data.")
                .locality(ALL)
                .privacy(PUBLIC)
                .action((ctx) -> silent.forceReply(text, ctx.chatId()))
                .reply((upd) -> actions.activities(upd), MESSAGE, REPLY,
                        upd -> upd.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(getBotUsername()),
                        upd -> {
                            Message reply = upd.getMessage().getReplyToMessage();
                            return reply.hasText() && reply.getText().equalsIgnoreCase(text);
                        })
                .build();
    }

    public Ability tests() {
        String text = "Inserisci la classe di cui vuoi sapere le verifiche.";
        return Ability
                .builder()
                .name("tests")
                .info("Scrive le verifiche future in programma della classe data.")
                .locality(ALL)
                .privacy(PUBLIC)
                .action((ctx) -> silent.forceReply(text, ctx.chatId()))
                .reply((upd) -> actions.tests(upd), MESSAGE, REPLY,
                        upd -> upd.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(getBotUsername()),
                        upd -> {
                            Message reply = upd.getMessage().getReplyToMessage();
                            return reply.hasText() && reply.getText().equalsIgnoreCase(text);
                        })
                .build();
    }

    public Ability missHours() {
        String text = "Inserisci la classe di cui vuoi sapere le ore buche.";
        return Ability
                .builder()
                .name("orebuche")
                .info("Scrive le ore buche future della classe data.")
                .locality(ALL)
                .privacy(PUBLIC)
                .action((ctx) -> silent.forceReply(text, ctx.chatId()))
                .reply((upd) -> actions.misshours(upd), MESSAGE, REPLY,
                        upd -> upd.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(getBotUsername()),
                        upd -> {
                            Message reply = upd.getMessage().getReplyToMessage();
                            return reply.hasText() && reply.getText().equalsIgnoreCase(text);
                        })
                .build();
    }

    public Ability feedback() {
        String text = "Invia un feedback agli amministratori!";
        return Ability
                .builder()
                .name("feedback")
                .info("Invia un feedback agli amministratori")
                .locality(ALL)
                .privacy(PUBLIC)
                .action((ctx) -> silent.forceReply(text, ctx.chatId()))
                .reply((upd) -> actions.feedback(upd), MESSAGE, REPLY,
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
                .info("Invia un messaggio a tutti gli utenti del bot.")
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

    public Ability getTime() {
        return Ability
                .builder()
                .name("orario")
                .info("Ricevi il tuo orario.")
                .locality(ALL)
                .privacy(PUBLIC)
                .action((ctx) -> actions.getTime(ctx))
                .build();
    }

    public Ability getTodaysTime() {
        return Ability
                .builder()
                .name("orariooggi")
                .info("Ricevi il tuo orario di oggi.")
                .locality(ALL)
                .privacy(PUBLIC)
                .action((ctx) -> actions.getTodaysTime(ctx))
                .build();
    }

    public Ability setClass() {
        String text = "Classe?";
        return Ability
                .builder()
                .name("impostaclasse")
                .info("Imposta la tua classe.")
                .locality(ALL)
                .privacy(PUBLIC)
                .action((ctx) -> {
                    if (ctx.arguments().length > 0) actions.setClass(ctx);
                    else silent.forceReply(text, ctx.chatId());
                })
                .reply(upd -> actions.setClass(upd), MESSAGE, REPLY,
                        upd -> upd.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(getBotUsername()),
                        upd -> {
                            Message reply = upd.getMessage().getReplyToMessage();
                            return reply.hasText() && reply.getText().equalsIgnoreCase(text);
                        })
                .build();
    }

    @Override
    public int creatorId() {
        return Configuration.getInstance().getCreatorId();
    }

    public ZuccBotActions getBotActions() {
        return actions;
    }
}
