package com.sasha.queueskip.command;

import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.sasha.queueskip.DiscordUtils;
import com.sasha.queueskip.Main;
import com.sasha.reminecraft.ReMinecraft;
import com.sasha.simplecmdsys.SimpleCommand;

import java.io.IOException;

public class WhisperCommand extends SimpleCommand {
    public WhisperCommand() {
        super("msg");
    }

    @Override
    public void onCommand() {
        if (this.getArguments() == null || this.getArguments().length != 2) {
            DiscordUtils.recievedMessage.getChannel().sendMessage(DiscordUtils.buildErrorEmbed("Invalid arguments!")).queue();
            return;
        }
        if (!Main.CONFIG.var_queueSkipEnabled || !Main.INSTANCE.getReMinecraft().minecraftClient.getSession().isConnected()) {
            DiscordUtils.recievedMessage.getChannel().sendMessage(DiscordUtils.buildErrorEmbed("You cannot send messages while queueskip is disabled.")).queue();
            return;
        }
        String playerToSendTo = this.getArguments()[0].replace("\247", "");
        String msg = this.getArguments()[1].replace("\247", "");
        String format = "/w " + playerToSendTo + " " + msg;
        if (format.length() > 256) {
            DiscordUtils.recievedMessage.getChannel().sendMessage(DiscordUtils.buildErrorEmbed("Your message must be under 256 characters.")).queue();
            return;
        }
        ReMinecraft.INSTANCE.minecraftClient.getSession().send(new ClientChatPacket(format));
    }
}
