package com.sasha.queueskip.command.ingame;

import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.sasha.queueskip.Main;
import com.sasha.simplecmdsys.SimpleCommand;

public class AboutCommand extends SimpleCommand {

    public AboutCommand() {
        super("queueskip");
    }

    @Override public void onCommand() {
        Main.INSTANCE.getReMinecraft().sendToChildren(new ServerChatPacket(Message.fromString("\2476QueueSkip " + Main.VERSION)));
    }
}
