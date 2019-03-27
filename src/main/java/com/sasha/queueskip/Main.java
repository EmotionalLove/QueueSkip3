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
import java.util.concurrent.TimeUnit;

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
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "welcome.title", "Welcome to QueueSkip " + VERSION);
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.SPANISH, "welcome.title", "Welcome to QueueSkip " + VERSION);
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "welcome.title", "Welcome to QueueSkip " + VERSION);
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "welcome.body", "**Please read the #queueskip-help chanel in the QueueSkip Discord server**. You can also view the ;help command for a list of commands.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "args.err", "Invalid arguments!");
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
                    CONFIG.var_subscription = System.currentTimeMillis();
                    Jda.getUserById(CONFIG.var_managerId).openPrivateChannel().queue(dm -> {
                        dm.sendMessage(DiscordUtils.buildInfoEmbed(LANG_MANAGER.resolve("welcome.title"),
                                LANG_MANAGER.resolve("welcome.body"))).queue();
                    }, fail -> {
                        DiscordUtils.getAdministrator().openPrivateChannel().queue(a -> {
                            a.sendMessage(DiscordUtils.buildErrorEmbed(DiscordUtils.getManager().getName() + "'s DM's can't be reached!")).submit();
                        });
                    });
                    CONFIG.var_newUser = false;
                }
                ScheduledExecutorService sc = Executors.newScheduledThreadPool(2);
                sc.scheduleAtFixedRate(() -> shouldNotifyRenew(CONFIG.var_subscription), 5L, 5L, TimeUnit.SECONDS);
            } catch (LoginException | InterruptedException ex) {
                logger.logError("Couldn't log into Discord. Is the token invalid?");
                ex.printStackTrace();
            }

        }
    }

    public void shouldNotifyRenew(long lastRenewed) {
        if (lastRenewed == -1L) return;
        if (System.currentTimeMillis() - lastRenewed <= 2.592e+9) {
            Jda.getUserById(CONFIG.var_adminId).openPrivateChannel().queue(dm -> {
                dm.sendMessage(Jda.getUserById(CONFIG.var_managerId).getName() + " needs to renew!!").submit();
                CONFIG.var_subscription = System.currentTimeMillis();
            });
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
            COMMAND_PROCESSOR.register(PositionCommand.class);
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
