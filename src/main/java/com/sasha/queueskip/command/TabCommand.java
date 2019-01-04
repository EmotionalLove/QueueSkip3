package com.sasha.queueskip.command;

import com.sasha.queueskip.DiscordUtils;
import com.sasha.queueskip.Main;
import com.sasha.reminecraft.client.ReClient;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

import java.util.concurrent.atomic.AtomicInteger;

@SimpleCommandInfo(description = "View the server tablist",
syntax = {""})
public class TabCommand extends SimpleCommand {

    public TabCommand() {
        super("tab");
    }

    @Override
    public void onCommand() {
        if (!Main.CONFIG.var_queueSkipEnabled) {
            DiscordUtils.recievedMessage.getChannel().sendMessage(
                    DiscordUtils.buildErrorEmbed("You cannot view the tablist because QueueSkip is disabled.")
            ).queue();
            return;
        }
        if (!Main.isConnected()) {
            DiscordUtils.recievedMessage.getChannel().sendMessage(
                    DiscordUtils.buildErrorEmbed("Your account isn't connected to 2b2t. This is likely a bug. Try using the ;requeue command.")
            ).queue();
            return;
        }
        StringBuilder builder = new StringBuilder("```\n" + ReClient.ReClientCache.INSTANCE.tabHeader.getFullText().replaceAll("§.", "") + "\n");
        AtomicInteger i = new AtomicInteger();
        ReClient.ReClientCache.INSTANCE.playerListEntries.forEach(entry -> {
            if (i.get() == 0) {
                builder.append(entry.getProfile().getName());
                i.getAndIncrement();
                return;
            }
            builder.append(", ").append(entry.getProfile().getName());
        });
        builder.append("\n").append(ReClient.ReClientCache.INSTANCE.tabFooter.getFullText().replaceAll("§.", "")).append("\n```");
        DiscordUtils.recievedMessage.getChannel().sendMessage(builder.toString()).queue();
    }
}
