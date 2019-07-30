package com.sasha.queueskip.command;

import com.sasha.queueskip.DiscordUtils;
import com.sasha.queueskip.Main;
import com.sasha.reminecraft.ReMinecraft;
import com.sasha.simplecmdsys.SimpleCommandInfo;
import me.someonelove.bettercommandsystem.Command;

import java.io.PrintWriter;
import java.io.StringWriter;

@SimpleCommandInfo(description = "View information about the instance of queueskip",
        syntax = {""})
public class AboutCommand extends Command {

    public AboutCommand() {
        super("about");
    }

    @Override
    public void onCommand(boolean hasArgs, String[] args) {
        try {
            DiscordUtils.recievedMessage.getChannel().sendMessage("```\n" +
                    "QueueSkip " + Main.VERSION + " with RE:Minecraft " + ReMinecraft.VERSION + "\n\n" +
                    "https://github.com/EmotionalLove/ReMinecraft\nCopyright (c) 2017-2019 Sasha Stevens. All rights reserved." + "\n```").submit();
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
