package com.sasha.queueskip;

import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import com.sasha.queueskip.command.LoginCommand;
import com.sasha.queueskip.command.RequeueCommand;
import com.sasha.queueskip.command.ToggleActiveCommand;
import com.sasha.queueskip.command.WhisperCommand;
import com.sasha.reminecraft.Logger;
import com.sasha.reminecraft.api.RePlugin;
import com.sasha.reminecraft.api.event.ChatRecievedEvent;
import com.sasha.reminecraft.api.event.MojangAuthenticateEvent;
import com.sasha.simplecmdsys.SimpleCommandProcessor;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;

import javax.security.auth.login.LoginException;
import java.io.IOException;

public class Main extends RePlugin implements SimpleListener {

    public static Main INSTANCE;
    public static final String VERSION = "3.0 beta";

    public static JDA Jda;

    public static Logger logger = new Logger("QueueSkip3");
    public static QSkipConfig CONFIG;

    public static SimpleCommandProcessor COMMAND_PROCESSOR = new SimpleCommandProcessor(";");

    @Override
    public void onPluginInit() {
        INSTANCE = this;
        this.getReMinecraft().EVENT_BUS.registerListener(this);
    }

    @Override
    public void onPluginEnable() {
        logger.log("QueueSkip plugin is enabled!");
    }

    @Override
    public void onPluginDisable() {
        Jda.shutdown();
        logger.logWarning("QueueSkip plugin is disabled!");
    }

    @Override
    public void registerCommands() {
        try {
            COMMAND_PROCESSOR.register(LoginCommand.class);
            COMMAND_PROCESSOR.register(ToggleActiveCommand.class);
            COMMAND_PROCESSOR.register(RequeueCommand.class);
            COMMAND_PROCESSOR.register(WhisperCommand.class);
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
        if (!e.isSuccessful()) {
            DiscordUtils.getManager().openPrivateChannel()
                    .queue(dm -> dm.sendMessage(DiscordUtils.buildErrorEmbed("Your Mojang account credentials are invalid!"))
                            .queue());
        }
    }

    @SimpleEventHandler
    public void onPreAuth(MojangAuthenticateEvent.Pre e) {
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
        if (!CONFIG.var_queueSkipEnabled) {
            logger.logWarning("Queue Skip is disabled, RE:Minecraft will not continue.");
            e.setCancelled(true);
        }
    }

    // to Color: hi there
    @SimpleEventHandler
    public void onChat(ChatRecievedEvent e) {
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
            return;
        }
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
