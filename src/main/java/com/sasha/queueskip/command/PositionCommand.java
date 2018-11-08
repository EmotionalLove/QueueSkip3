package com.sasha.queueskip.command;

import com.sasha.queueskip.DiscordUtils;
import com.sasha.queueskip.Main;
import com.sasha.reminecraft.client.ReClient;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

import java.util.concurrent.atomic.AtomicInteger;

@SimpleCommandInfo(description = "View your position in the world",
syntax = {""})
public class PositionCommand extends SimpleCommand {

    public PositionCommand() {
        super("pos");
    }

    @Override
    public void onCommand() {
        if (!Main.isConnected()) {
            DiscordUtils.recievedMessage.getChannel().sendMessage(
                    DiscordUtils.buildErrorEmbed("You cannot view your position because QueueSkip is disabled.")
            ).queue();
            return;
        }
        DiscordUtils.recievedMessage.getChannel().sendMessage(
                DiscordUtils.buildInfoEmbed("Position XYZ",
                        "**X** " + (int)ReClient.ReClientCache.INSTANCE.posX + "\n" +
                        "**Y** " + (int) ReClient.ReClientCache.INSTANCE.posY + "\n" +
                        "**Z** " + (int) ReClient.ReClientCache.INSTANCE.posZ)
        ).queue();
    }
}
