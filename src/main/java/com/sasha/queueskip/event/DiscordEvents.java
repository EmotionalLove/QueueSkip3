package com.sasha.queueskip.event;

import com.sasha.queueskip.DiscordUtils;
import com.sasha.queueskip.Main;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class DiscordEvents extends ListenerAdapter {

    @SubscribeEvent
    public void onDMRx(PrivateMessageReceivedEvent e) {
        if (e.getAuthor().getId().equals(Main.CONFIG.var_managerId)) {
            DiscordUtils.recievedMessage = e.getMessage();
            e.getMessage().getChannel().sendTyping().queue(type -> {
                if (e.getMessage().getContentDisplay().startsWith(";")) {
                    Main.COMMAND_PROCESSOR.processCommand(e.getMessage().getContentDisplay());
                    return;
                }
                e.getMessage().getChannel().sendMessage(DiscordUtils.buildErrorEmbed("Commands must start with \";\"")).queue();
            });

        }
    }
}
