package com.sasha.queueskip.command;

import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.sasha.queueskip.DiscordUtils;
import com.sasha.queueskip.Util;
import com.sasha.reminecraft.ReMinecraft;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

import java.io.PrintWriter;
import java.io.StringWriter;

import static com.sasha.queueskip.Main.LANG_MANAGER;

@SimpleCommandInfo(description = "Send a whisper to another player on 2b2t",
        syntax = "<player> <message>")
public class WhisperCommand extends SimpleCommand {
    public WhisperCommand() {
        super("msg");
    }

    @Override
    public void onCommand() {
        try {
            if (this.getArguments() == null || this.getArguments().length != 2) {
                DiscordUtils.recievedMessage.getChannel().sendMessage(DiscordUtils.buildErrorEmbed(LANG_MANAGER.resolve("args.err") + "\nexample: ';msg SashaTH \"Girl youre fucking gay\"'")).queue();
                return;
            }
            if (!Util.isConnected()) {
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
