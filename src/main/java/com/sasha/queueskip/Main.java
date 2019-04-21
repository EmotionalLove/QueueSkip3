package com.sasha.queueskip;

import com.sasha.queueskip.command.*;
import com.sasha.queueskip.event.DiscordEvents;
import com.sasha.queueskip.event.MinecraftEvents;
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
    public static final String VERSION = "3.2";

    public static JDA discord;

    public static ILogger logger = LoggerBuilder.buildProperLogger("QueueSkip3");
    public static QSkipConfig CONFIG;

    public static SimpleCommandProcessor COMMAND_PROCESSOR = new SimpleCommandProcessor(";");
    public static final LocalisedResponseManager LANG_MANAGER = new LocalisedResponseManager();

    public static long uptime;

    @Override
    public void onPluginInit() {
        INSTANCE = this;
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
        if (discord == null) {
            try {
                discord = new JDABuilder(CONFIG.var_discordToken)
                        .setEventManager(new AnnotatedEventManager())
                        .addEventListener(new DiscordEvents())
                        .buildBlocking();
                if (CONFIG.var_newUser) {
                    discord.getUserById(CONFIG.var_managerId).openPrivateChannel().queue(dm -> {
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
        discord.shutdown();
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






}
