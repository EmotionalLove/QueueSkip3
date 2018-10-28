package com.sasha.queueskip;

import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import com.sasha.queueskip.command.LoginCommand;
import com.sasha.reminecraft.Logger;
import com.sasha.reminecraft.api.RePlugin;
import com.sasha.reminecraft.api.event.MojangAuthenticateEvent;
import com.sasha.simplecmdsys.SimpleCommandProcessor;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;

import javax.security.auth.login.LoginException;

public class Main extends RePlugin implements SimpleListener {

    public static JDA Jda;

    public static Logger logger = new Logger("QueueSkip3");
    public static QSkipConfig CONFIG;

    public static SimpleCommandProcessor COMMAND_PROCESSOR = new SimpleCommandProcessor(";");

    @Override
    public void onPluginInit() {
        this.getReMinecraft().EVENT_BUS.registerListener(this);
    }

    @Override
    public void onPluginEnable() {
    }

    @Override
    public void onPluginDisable() {
        if (Jda != null) Jda.shutdown();
    }

    @Override
    public void registerCommands() {
        try {
            COMMAND_PROCESSOR.register(LoginCommand.class);
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
        try {
            Jda = new JDABuilder(CONFIG.var_discordToken)
                    .setEventManager(new AnnotatedEventManager())
                    .addEventListener(new DiscordEvents())
                    .buildBlocking();
        } catch (LoginException | InterruptedException ex) {
            logger.logError("Couldn't log into Discord. Is the token invalid?");
            ex.printStackTrace();
        }
        if (!CONFIG.var_queueSkipEnabled) {
            logger.logWarning("Queue Skip is disabled, RE:Minecraft will not continue.");
            e.setCancelled(true);
        }
    }
}
