package com.sasha.queueskip.command;

import com.sasha.queueskip.DiscordUtils;
import com.sasha.queueskip.Util;
import com.sasha.reminecraft.client.ReClient;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

import java.io.PrintWriter;
import java.io.StringWriter;

@SimpleCommandInfo(description = "View your position in the world, and other player stats.",
        syntax = {""})
public class InfoCommand extends SimpleCommand {

    public InfoCommand() {
        super("info");
    }

    @Override
    public void onCommand() {
        try {
            if (!Util.isConnected()) {
                DiscordUtils.recievedMessage.getChannel().sendMessage(
                        DiscordUtils.buildErrorEmbed("You cannot view your position because QueueSkip is disabled.")
                ).queue();
                return;
            }
            DiscordUtils.recievedMessage.getChannel().sendMessage(
                    DiscordUtils.buildInfoEmbed("Player Status",
                            "**X** " + (int) ReClient.ReClientCache.INSTANCE.posX + "\n" +
                                    "**Y** " + (int) ReClient.ReClientCache.INSTANCE.posY + "\n" +
                                    "**Z** " + (int) ReClient.ReClientCache.INSTANCE.posZ + "\n" +
                                    "**Health** " + ReClient.ReClientCache.INSTANCE.health / 2 + " hearts" + "\n" +
                                    "**Hunger** " + ReClient.ReClientCache.INSTANCE.food / (float) 2 + " chicken bois")
            ).queue();
        } catch (Exception e) {
            StringWriter writer = new StringWriter();
            PrintWriter writer1 = new PrintWriter(writer);
            e.printStackTrace(writer1);
            DiscordUtils.getAdministrator().openPrivateChannel().queue(dm -> {
                dm.sendMessage(DiscordUtils.buildErrorEmbed(writer.toString())).submit();
            });
        }

    }
}
