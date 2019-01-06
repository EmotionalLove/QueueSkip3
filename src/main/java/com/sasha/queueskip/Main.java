package com.sasha.queueskip;

import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import com.sasha.queueskip.command.*;
import com.sasha.reminecraft.ReMinecraft;
import com.sasha.reminecraft.api.RePlugin;
import com.sasha.reminecraft.api.event.*;
import com.sasha.reminecraft.logging.ILogger;
import com.sasha.reminecraft.logging.LoggerBuilder;
import com.sasha.simplecmdsys.SimpleCommandProcessor;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends RePlugin implements SimpleListener {

    public static Main INSTANCE;
    public static final String VERSION = "3.1.4";

    public static JDA Jda;

    public static ILogger logger = LoggerBuilder.buildProperLogger("QueueSkip3");
    public static QSkipConfig CONFIG;

    public static SimpleCommandProcessor COMMAND_PROCESSOR = new SimpleCommandProcessor(";");

    public static long lastMsg = System.currentTimeMillis();

    private boolean isRunning = false;
    private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

    @Override
    public void onPluginInit() {
        INSTANCE = this;
        logger.log("RE:Minecraft implementing QueueSkip " + VERSION + "...");
        this.getReMinecraft().EVENT_BUS.registerListener(this);
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
                        dm.sendMessage(DiscordUtils.buildInfoEmbed("Welcome to QueueSkip",
                                "View the ;help command for a list of commands.")).queue();
                    }, fail -> {
                        DiscordUtils.getAdministrator().openPrivateChannel().queue(a -> {
                            a.sendMessage(DiscordUtils.buildErrorEmbed(DiscordUtils.getManager().getName() + "'s DM's can't be reached!")).submit();
                        });
                    });
                    CONFIG.var_newUser = false;
                }
            } catch (LoginException | InterruptedException ex) {
                logger.logError("Couldn't log into Discord. Is the token invalid?");
                ex.printStackTrace();
            }

        }
    }

    @Override
    public void onPluginEnable() {
        if (!isRunning) {
            scheduledExecutorService.scheduleAtFixedRate(() -> {
                DiscordUtils.sendDebug(System.currentTimeMillis() - lastMsg + "ms since last chat msg");
                if (System.currentTimeMillis() - lastMsg >= 300000L && isConnected()) {
                    DiscordUtils.sendDebug("over 5 mins since last chat... relaunching");
                    lastMsg = System.currentTimeMillis();
                    this.getReMinecraft().reLaunch();
                }
            }, 5, 5, TimeUnit.SECONDS);
        }
        isRunning = true;
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

    @SimpleEventHandler
    public void onPostAuth(MojangAuthenticateEvent.Post e) {
        DiscordUtils.sendDebug("authentication with mojang completed.");
        if (!e.isSuccessful() && e.getMethod() == MojangAuthenticateEvent.Method.EMAILPASS) {
            e.setCancelled(true);
            DiscordUtils.getManager().openPrivateChannel()
                    .queue(dm -> dm.sendMessage(DiscordUtils.buildErrorEmbed("Your Mojang account credentials are invalid!"))
                            .queue(), fail -> {
                        DiscordUtils.getAdministrator().openPrivateChannel().queue(a -> {
                            a.sendMessage(DiscordUtils.buildErrorEmbed(DiscordUtils.getManager().getName() + "'s DM's can't be reached, and their account credentials are invalid!")).submit();
                        });
                    });
        }
    }

    @SimpleEventHandler
    public void onPlayerVisible(EntityInRangeEvent.Player e) {
        if (CONFIG.var_inRange && isConnected() && !this.getReMinecraft().areChildrenConnected()) {
             DiscordUtils.getManager().openPrivateChannel().queue(dm -> {
                 dm.sendMessage(DiscordUtils.buildInfoEmbed("A player has entered visible range!", e.getName() + " has entered your account's visible range!")).submit();
             });
        }
    }

    @SimpleEventHandler
    public void onPreAuth(MojangAuthenticateEvent.Pre e) {
        DiscordUtils.sendDebug("authentication with mojang requested.");
        if (!CONFIG.var_queueSkipEnabled) {
            logger.logWarning("Queue Skip is disabled, RE:Minecraft will not continue.");
            e.setCancelled(true);
        }
    }

    @SimpleEventHandler
    public void onChildJoin(ChildJoinEvent e) {
        DiscordUtils.sendDebug("child user joined qskip server");
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
        DiscordUtils.sendDebug("chat message received for processing.");
        lastMsg = System.currentTimeMillis();
        if (isWhisperTo(e.getMessageText().toLowerCase())) {
            DiscordUtils.sendDebug("is a whisper to msg.");
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
            DiscordUtils.sendDebug("is a whisper from msg.");
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
        DiscordUtils.sendDebug("player health updated.");
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
