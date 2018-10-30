package com.sasha.queueskip.command;

import com.sasha.queueskip.DiscordUtils;
import com.sasha.queueskip.Main;
import com.sasha.reminecraft.client.ReClient;
import com.sasha.simplecmdsys.SimpleCommand;

import java.util.concurrent.atomic.AtomicInteger;

public class TabCommand extends SimpleCommand {

    public TabCommand() {
        super("tab");
    }

    @Override
    public void onCommand() {
        if (!Main.isConnected()) {
            DiscordUtils.recievedMessage.getChannel().sendMessage(
                    DiscordUtils.buildErrorEmbed("You cannot view the tablist because QueueSkip is disabled.")
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
