package com.sasha.queueskip.command;

import com.sasha.queueskip.DiscordUtils;
import com.sasha.queueskip.Main;
import com.sasha.reminecraft.ReMinecraft;
import com.sasha.simplecmdsys.SimpleCommand;

public class AboutCommand extends SimpleCommand {

    public AboutCommand() {
        super("about");
    }

    @Override public void onCommand() {
        DiscordUtils.recievedMessage.getChannel().sendMessage("```\n" +
                "QueueSkip " + Main.VERSION + " with RE:Minecraft " + ReMinecraft.VERSION + "\n\n" +
                "https://github.com/EmotionalLove/ReMinecraft\nCopyright (c) 2017-2018 Sasha Stevens. All rights reserved." + "\n```").submit();
    }
}
