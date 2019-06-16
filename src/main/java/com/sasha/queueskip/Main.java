package com.sasha.queueskip;

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
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
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.sasha.queueskip.Util.isConnected;

public class Main extends RePlugin {

    public static Main INSTANCE;
    public static final String VERSION = "3.2.4";

    public static JDA discord;

    public static ILogger logger = LoggerBuilder.buildProperLogger("QueueSkip3");
    public static QSkipConfig CONFIG;

    public static SimpleCommandProcessor COMMAND_PROCESSOR = new SimpleCommandProcessor(";");
    public static final LocalisedResponseManager LANG_MANAGER = new LocalisedResponseManager();
    public static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

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
                DiscordUtils.generateUserChannel(channel -> {
                    channel.sendMessage(new MessageBuilder()
                            .setEmbed(DiscordUtils.buildInfoEmbed("Welcome to QueueSkip!", "Please read the #guide channel to learn how to set up QueueSkip."))
                            .setContent(DiscordUtils.getManager().getAsMention()).build()).queue();
                    getReMinecraft().MAIN_CONFIG.var_sessionId = "newuser";
                    getReMinecraft().MAIN_CONFIG.var_mojangEmail = "newuser";
                    getReMinecraft().MAIN_CONFIG.var_mojangEmail = "newuser";
                    CONFIG.var_newUser = false;
                });
            }
            executorService.scheduleAtFixedRate(() -> {
                if (CONFIG.var_spamChat && isConnected() && !getReMinecraft().areChildrenConnected() && ((MinecraftProtocol) getReMinecraft().minecraftClient.getSession().getPacketProtocol()).getSubProtocol() == SubProtocol.GAME) {
                    Random random = new Random();
                    getReMinecraft().minecraftClient.getSession().send(new ClientChatPacket(CONFIG.var_spamMessages.get(random.nextInt(CONFIG.var_spamMessages.size()))));
                }
            }, 60L, 60L, TimeUnit.SECONDS);
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
