package com.sasha.queueskip.event;

import com.sasha.queueskip.DiscordUtils;
import com.sasha.queueskip.Main;
import net.dv8tion.jda.core.events.ReconnectedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class DiscordEvents extends ListenerAdapter {

    @SubscribeEvent
    public void onDMRx(GuildMessageReceivedEvent e) {
        if (!e.getChannel().getId().equals(Main.CONFIG.var_channelId)) return;
        if (e.getAuthor().getId().equals(Main.CONFIG.var_managerId) || e.getAuthor().getId().equalsIgnoreCase(Main.CONFIG.var_adminId)) {
            DiscordUtils.recievedMessage = e.getMessage();
            e.getMessage().getChannel().sendTyping().queue(type -> {
                if (e.getMessage().getContentDisplay().startsWith(";")) {
                    try {
                        if (!Main.COMMAND_PROCESSOR.processCommand(e.getMessage().getContentDisplay())) {
                            e.getMessage().getChannel().sendMessage(DiscordUtils.buildErrorEmbed("The specified command couldn't be found.")).submit();
                        }
                    } catch (Exception ex) {
                        e.getMessage().getChannel().sendMessage(DiscordUtils.buildErrorEmbed("An exception occurred whilst processing the requested command.")).submit();
                        ex.printStackTrace();
                    }
                }
            });

        }
    }

    @SubscribeEvent
    public void onDiscordDed(ReconnectedEvent e) {
        Main.INSTANCE.getReMinecraft().reLaunch();
    }
}
