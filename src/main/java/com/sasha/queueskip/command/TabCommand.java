package com.sasha.queueskip.command;

import com.sasha.queueskip.DiscordUtils;
import com.sasha.queueskip.Main;
import com.sasha.queueskip.Util;
import com.sasha.reminecraft.client.ReClient;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicInteger;

@SimpleCommandInfo(description = "View the server tablist",
        syntax = {""})
public class TabCommand extends SimpleCommand {

    public TabCommand() {
        super("tab");
    }

    @Override
    public void onCommand() {
        try {
            if (!Main.CONFIG.var_queueSkipEnabled) {
                DiscordUtils.recievedMessage.getChannel().sendMessage(
                        DiscordUtils.buildErrorEmbed("You cannot view the tablist because QueueSkip is disabled.")
                ).queue();
                return;
            }
            if (!Util.isConnected()) {
                DiscordUtils.recievedMessage.getChannel().sendMessage(
                        DiscordUtils.buildErrorEmbed("Your account isn't connected to 2b2t. This is likely a bug. Try using the ;requeue command.")
                ).queue();
                return;
            }
            String tab = getTabText(true);
            if (tab.length() >= 2000) tab = getTabText(false);
            DiscordUtils.recievedMessage.getChannel().sendMessage(tab).queue();
        } catch (Exception e) {
            StringWriter writer = new StringWriter();
            PrintWriter writer1 = new PrintWriter(writer);
            e.printStackTrace(writer1);
            DiscordUtils.getAdministrator().openPrivateChannel().queue(dm -> {
                dm.sendMessage(DiscordUtils.buildErrorEmbed(writer.toString())).submit();
            });
        }

    }

    public String getTabText(boolean includePlayers) {
        StringBuilder builder = new StringBuilder("```\n" + ReClient.ReClientCache.INSTANCE.tabHeader.getFullText().replaceAll("§.", "") + "\n");
        AtomicInteger i = new AtomicInteger();
        ReClient.ReClientCache.INSTANCE.playerListEntries.forEach(entry -> {
            if (i.get() == 0) {
                if (includePlayers) builder.append(entry.getProfile().getName());
                i.getAndIncrement();
                return;
            }
            if (includePlayers) builder.append(", ").append(entry.getProfile().getName());
            i.getAndIncrement();
        });
        if (!includePlayers) builder.append(i.get()).append(" players not shown");
        builder.append("\n").append(ReClient.ReClientCache.INSTANCE.tabFooter.getFullText().replaceAll("§.", "")).append("\n```");
        return builder.toString();
    }

}
