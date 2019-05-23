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
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;

import javax.security.auth.login.LoginException;

public class Main extends RePlugin {

    public static Main INSTANCE;
    public static final String VERSION = "3.2.1";

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
        if (!Util.doStartupChecks(CONFIG)) {
            logger.logError("Some parts of the configuration weren't filled out. Please revise it.");
            this.getReMinecraft().stop();
            return;
        }
        try {
            discord = new JDABuilder(CONFIG.var_discordToken)
                    .setEventManager(new AnnotatedEventManager())
                    .addEventListener(new DiscordEvents())
                    .buildBlocking();
            if (CONFIG.var_newUser) {
                DiscordUtils.generateUserChannel();
                DiscordUtils.getUserChannel()
                        .sendMessage(new MessageBuilder()
                                .setEmbed(DiscordUtils.buildInfoEmbed("Welcome to QueueSkip!", "Please read the #guide channel to learn how to set up QueueSkip."))
                                .setContent(DiscordUtils.getManager().getAsMention()).build()).queue();
                CONFIG.var_sessionId = "newuser";
                CONFIG.var_mojangEmail = "newuser";
                CONFIG.var_mojangEmail = "newuser";
                CONFIG.var_newUser = false;
            }
        } catch (LoginException | InterruptedException ex) {
            logger.logError("Couldn't log into Discord. Is the token invalid?");
            ex.printStackTrace();
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
