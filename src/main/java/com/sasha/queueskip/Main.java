package com.sasha.queueskip;

import com.sasha.queueskip.command.*;
import com.sasha.queueskip.event.DiscordEvents;
import com.sasha.queueskip.event.MinecraftEvents;
import com.sasha.queueskip.localisation.EnumLocale;
import com.sasha.queueskip.localisation.LocalisedResponseManager;
import com.sasha.reminecraft.ReMinecraft;
import com.sasha.reminecraft.api.RePlugin;
import com.sasha.reminecraft.logging.ILogger;
import com.sasha.reminecraft.logging.LoggerBuilder;
import com.sasha.simplecmdsys.SimpleCommandProcessor;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;

import javax.security.auth.login.LoginException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Main extends RePlugin {

    public static Main INSTANCE;
    public static final String VERSION = "3.1.7";

    public static JDA Jda;

    public static ILogger logger = LoggerBuilder.buildProperLogger("QueueSkip3");
    public static QSkipConfig CONFIG;

    public static SimpleCommandProcessor COMMAND_PROCESSOR = new SimpleCommandProcessor(";");
    public static final LocalisedResponseManager LANG_MANAGER = new LocalisedResponseManager();

    public static long uptime;

    @Override
    public void onPluginInit() {
        INSTANCE = this;
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.welcome", "Welcome to QueueSkip " + VERSION);
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.SPANISH, "queueskip.welcome", "Bienvenido a QueueSkip " + VERSION);
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.welcome", "Bienvenue au QueueSkip " + VERSION);

        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.body.welcome", "**Please read the #queueskip-help chanel in the QueueSkip Discord server**. You can also view the ;help command for a list of commands.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.body.welcome", "**Merci de lire le salon textuel #queueskip-help dans le server Discord**.\n" +
                "Vous pouvez aussi voir les commandes disponibles avec ;help.\n");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.SPANISH, "queueskip.body.welcome", "**Por favor leer la canal #queueskip-help en la servido Discord de QueueSkip***\n" +
                "Tú puedes además ;ayuda por las listos de comandos\n");

        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.err.args", "Invalid arguments!");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.SPANISH, "queueskip.err.args", "¡Argumento es inválido!");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.err.args", "Arguments Invalide!");

        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.uptime", "The software has been running for <time> without a reboot.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.uptime", "Le programme est en marche sans restart depuis <time>");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.SPANISH, "queueskip.uptime", "La programa ha sido trabajando por <time> sin reiniciar.");

        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.safeenable", "Safe mode enabled.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.safeenable", "Mode Sauf Activé.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.SPANISH, "queueskip.safeenable", "Modo seguro habilitado.");

        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.body.safeenable", "You will now be disconnected if you take damage while logged out.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.body.safeenable", "Vous serez déconnecté si vous prenez du dégâts.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.SPANISH, "queueskip.body.safeenable", "Serás desconectado si lo tomas dañar mientras está desconectado.");

        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.safedisable", "Safe mode disabled.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.safedisable", "Mode Sauf Désactivé.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.JAPANESE, "queueskip.safedisable", "セーフモードを無効にしていました");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.DUTCH, "queueskip.safedisable", "Veilige modus is niet meer actief");

        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.body.safedisable", "You will no longer be disconnected if you take damage.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.body.safedisable", "Vous serez plus déconnecté si vous prenez des dégâts.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.JAPANESE, "queueskip.body.safedisable", "あなたを害するば、あなたはディスコネクトいません");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.DUTCH, "queueskip.body.safedisable", "Je connectie wordt vanaf nu niet meer verbroken als je aangevallen wordt terwijl je offline bent.");

        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.inrangeenable", "In-range notifications enabled");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.inrangeenable", "Notifications de joueur en vue Activée");

        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.inrangeenable", "In-range notifications disabled.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.inrangeenable", "Notifications de joueur en vue Désactivé.");

        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.body.inrangeenable", "You will now receive Discord alerts if a player comes into range.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.body.inrangeenable", "Vous serez alerté si un joueur rentre dans votre champ de vue.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.JAPANESE, "queueskip.body.inrangeenable", "あなたのアカウントは人を見ますば、あなたを警告しています");

        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.body.inrangedisable", "You will no longer receive Discord alerts if a player comes into range.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.body.inrangedisable", "Vous ne serez plus alerté si un joueur rentre dans votre champ de vue.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.JAPANESE, "queueskip.body.inrangedisable", "あなたのアカウントは人を見ますば、あなたを警告していません");


        uptime = System.currentTimeMillis();
        logger.log("RE:Minecraft implementing QueueSkip " + VERSION + "...");
        this.getReMinecraft().EVENT_BUS.registerListener(new MinecraftEvents(this));
        if (CONFIG.var_discordToken.equalsIgnoreCase("[no default]")) {
            logger.logError("Discord token isn't set!");
            this.getReMinecraft().stop();
        }
        if (CONFIG.var_managerId.equalsIgnoreCase("[no default]")) {
            logger.logError("Manager ID isn't set!");
            this.getReMinecraft().stop();
        }
        if (Jda == null) {
            try {
                Jda = new JDABuilder(CONFIG.var_discordToken)
                        .setEventManager(new AnnotatedEventManager())
                        .addEventListener(new DiscordEvents())
                        .buildBlocking();
                if (CONFIG.var_newUser) {
                    Jda.getUserById(CONFIG.var_managerId).openPrivateChannel().queue(dm -> {
                        dm.sendMessage(DiscordUtils.buildInfoEmbed(LANG_MANAGER.resolve("queueskip.welcome"),
                                LANG_MANAGER.resolve("welcome.body"))).queue();
                    }, fail -> {
                        DiscordUtils.getAdministrator().openPrivateChannel().queue(a -> {
                            a.sendMessage(DiscordUtils.buildErrorEmbed(DiscordUtils.getManager().getName() + "'s DM's can't be reached!")).submit();
                        });
                    });
                    CONFIG.var_newUser = false;
                }
                ScheduledExecutorService sc = Executors.newScheduledThreadPool(2);
            } catch (LoginException | InterruptedException ex) {
                logger.logError("Couldn't log into Discord. Is the token invalid?");
                ex.printStackTrace();
            }

        }
    }

    @Override
    public void onPluginEnable() {
        logger.log("QueueSkip plugin is enabled!");
    }

    @Override
    public void onPluginDisable() {
        logger.logWarning("QueueSkip plugin is disabled!");
    }

    @Override
    public void onPluginShutdown() {
        Jda.shutdown();
    }

    @Override
    public void registerCommands() {
        try {
            COMMAND_PROCESSOR.register(LoginCommand.class);
            COMMAND_PROCESSOR.register(ToggleActiveCommand.class);
            COMMAND_PROCESSOR.register(RequeueCommand.class);
            COMMAND_PROCESSOR.register(WhisperCommand.class);
            COMMAND_PROCESSOR.register(TabCommand.class);
            COMMAND_PROCESSOR.register(InfoCommand.class);
            COMMAND_PROCESSOR.register(HelpCommand.class);
            COMMAND_PROCESSOR.register(ToggleSafeModeCommand.class);
            COMMAND_PROCESSOR.register(AboutCommand.class);
            COMMAND_PROCESSOR.register(ToggleInRangeNotificationsCommand.class);
            COMMAND_PROCESSOR.register(ToggleDebugCommand.class);
            COMMAND_PROCESSOR.register(UptimeCommand.class);
            COMMAND_PROCESSOR.register(ToggleConnectingAlertCommand.class);
            ReMinecraft.INGAME_CMD_PROCESSOR.register(com.sasha.queueskip.command.ingame.AboutCommand.class);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerConfig() {
        CONFIG = new QSkipConfig();
        this.getReMinecraft().configurations.add(CONFIG);
    }


    public float asHearts(float health) {
        return health / (float) 2;
    }

    public static boolean isConnected() {
        return Main.CONFIG.var_queueSkipEnabled && Main.INSTANCE.getReMinecraft().minecraftClient.getSession().isConnected();
    }

    public static boolean is2b2tDead(String msg) {
        return msg.toLowerCase().startsWith("exception connecting:");
    }

    public boolean isWhisperTo(String s) {
        return s.matches("to .*: .*$");
    }

    public boolean isWhisperFrom(String s) {
        return s.matches("^.* whispers: .*$");
    }
}
