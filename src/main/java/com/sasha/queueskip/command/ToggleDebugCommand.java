package com.sasha.queueskip.command;

import com.sasha.queueskip.DiscordUtils;
import com.sasha.queueskip.Main;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

@SimpleCommandInfo(description = "Toggle debug msgs", syntax = {""})
public class ToggleDebugCommand extends SimpleCommand {

    public ToggleDebugCommand() {
        super("debug");
    }

    @Override
    public void onCommand() {
        Action action = Main.CONFIG.var_debug ? Action.DISABLING : Action.ENABLING;
        switch (action) {
            case ENABLING:
                DiscordUtils.recievedMessage.getChannel().sendMessage(DiscordUtils.buildInfoEmbed("Debug notifications enabled", "Mm.")).queue();
                Main.CONFIG.var_debug = true;
                break;
            case DISABLING:
                DiscordUtils.recievedMessage.getChannel().sendMessage(DiscordUtils.buildInfoEmbed("Debug notifications disabled", "Mm.")).queue();
                Main.CONFIG.var_debug = false;
                break;
        }
    }
}