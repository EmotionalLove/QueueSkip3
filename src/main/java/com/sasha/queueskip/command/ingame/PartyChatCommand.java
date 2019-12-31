package com.sasha.queueskip.command.ingame;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.sasha.queueskip.Main;
import com.sasha.simplecmdsys.SimpleCommand;

public class PartyChatCommand extends SimpleCommand {

    public PartyChatCommand() {
        super("pc");
    }

    @Override
    public void onCommand() {
        boolean state = Main.partyChatManager.toggleState();
        Main.INSTANCE.getReMinecraft().sendToChildren(new ServerChatPacket("\247eParty Chat " + (state ? "enabled" : "disabled")));
    }
}
