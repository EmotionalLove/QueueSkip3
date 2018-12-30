package com.sasha.queueskip;

import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import com.sasha.queueskip.command.*;
import com.sasha.reminecraft.ReMinecraft;
import com.sasha.reminecraft.api.RePlugin;
import com.sasha.reminecraft.api.event.ChatReceivedEvent;
import com.sasha.reminecraft.api.event.ChildJoinEvent;
import com.sasha.reminecraft.api.event.MojangAuthenticateEvent;
import com.sasha.reminecraft.api.event.PlayerDamagedEvent;
import com.sasha.reminecraft.logging.ILogger;
import com.sasha.reminecraft.logging.LoggerBuilder;
import com.sasha.simplecmdsys.SimpleCommandProcessor;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Main extends RePlugin implements SimpleListener {

    public static Main INSTANCE;
    public static final String VERSION = "3.1";

    public static JDA Jda;

    public static ILogger logger = LoggerBuilder.buildProperLogger("QueueSkip3");
    public static QSkipConfig CONFIG;

    public static SimpleCommandProcessor COMMAND_PROCESSOR = new SimpleCommandProcessor(";");

    @Override
    public void onPluginInit() {
        INSTANCE = this;
        logger.log("RE:Minecraft implementing QueueSkip " + VERSION + "...");
    }

    @Override
    public void onPluginEnable() {
        this.getReMinecraft().EVENT_BUS.registerListener(this);
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
            ReMinecraft.INGAME_CMD_PROCESSOR.register(com.sasha.queueskip.command.ingame.AboutCommand.class);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerConfig() {
        CONFIG = new QSkipConfig();
        this.getReMinecraft().configurations.add(CONFIG);
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
            } catch (LoginException | InterruptedException ex) {
                logger.logError("Couldn't log into Discord. Is the token invalid?");
                ex.printStackTrace();
            }
        }
    }

    @SimpleEventHandler
    public void onPostAuth(MojangAuthenticateEvent.Post e) {
        if (!e.isSuccessful() && e.getMethod() == MojangAuthenticateEvent.Method.EMAILPASS) {
            DiscordUtils.getManager().openPrivateChannel()
                    .queue(dm -> dm.sendMessage(DiscordUtils.buildErrorEmbed("Your Mojang account credentials are invalid!"))
                            .queue());
        }
    }

    @SimpleEventHandler
    public void onPreAuth(MojangAuthenticateEvent.Pre e) {
        if (!CONFIG.var_queueSkipEnabled) {
            logger.logWarning("Queue Skip is disabled, RE:Minecraft will not continue.");
            e.setCancelled(true);
        }
        if (CONFIG.var_newUser) {
            Jda.getUserById(CONFIG.var_managerId).openPrivateChannel().queue(dm -> {
                dm.sendMessage(DiscordUtils.buildInfoEmbed("Welcome to queueskip",
                        "View the ;help command for a list of commands.")).queue();
            });
            CONFIG.var_newUser = false;
        }
    }

    @SimpleEventHandler
    public void onChildJoin(ChildJoinEvent e) {
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            this.getReMinecraft().sendToChildren(new ServerChatPacket(Message.fromString("\2476You have been connected to 2b2t via queueskip " + VERSION)));
        }).start();
    }

    // to Color: hi there
    @SimpleEventHandler
    public void onChat(ChatReceivedEvent e) {
        if (isWhisperTo(e.getMessageText().toLowerCase())) {
            String[] begin = e.getMessageText().substring(0, e.getMessageText().indexOf(":")).split(" ");
            String who = begin[1].replace(":", "");
            String msg = e.getMessageText().substring(e.getMessageText().indexOf(":") + 2);
            DiscordUtils.getManager().openPrivateChannel().queue(pm -> {
                try {
                    pm.sendMessage
                            (DiscordUtils.buildWhisperToEmbed(who, msg)).queue();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            return;
        }
        if (isWhisperFrom(e.getMessageText().toLowerCase())) {
            String who = e.getMessageText().substring(0, e.getMessageText().indexOf(" "));
            String msg = e.getMessageText().substring(e.getMessageText().indexOf(":") + 2);
            DiscordUtils.getManager().openPrivateChannel().queue(pm -> {
                try {
                    pm.sendMessage
                            (DiscordUtils.buildWhisperFromEmbed(who, msg)).queue();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    @SimpleEventHandler
    public void onHurt(PlayerDamagedEvent e) {
        if (isConnected() && !this.getReMinecraft().areChildrenConnected() && CONFIG.var_safeMode) {
            DiscordUtils.getManager().openPrivateChannel().queue(dm -> {
                if (e.getOldHealth() - e.getNewHealth() < 0.1f) return;
                dm.sendMessage(DiscordUtils.buildInfoEmbed("Disconnected", "You were damaged " + asHearts(e.getOldHealth() - e.getNewHealth()) + " hearts. You were requeued because Safe mode is on.")).queue(ee -> ReMinecraft.INSTANCE.reLaunch());
            });
        }
    }


    private float asHearts(float health) {
        return health / (float) 2;
    }

    public static boolean isConnected() {
        return Main.CONFIG.var_queueSkipEnabled && Main.INSTANCE.getReMinecraft().minecraftClient.getSession().isConnected();
    }

    private boolean isWhisperTo(String s) {
        return s.matches("to .*: .*$");
    }

    private boolean isWhisperFrom(String s) {
        return s.matches("^.* whispers: .*$");
    }
}
