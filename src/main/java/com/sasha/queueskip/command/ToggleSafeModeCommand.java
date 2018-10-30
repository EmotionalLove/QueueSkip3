package com.sasha.queueskip.command;

import com.sasha.queueskip.DiscordUtils;
import com.sasha.queueskip.Main;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

@SimpleCommandInfo(description = "Toggle safe mode on and off", syntax = {""})
public class ToggleSafeModeCommand extends SimpleCommand {

    public ToggleSafeModeCommand() {
        super("safe");
    }

    @Override
    public void onCommand() {
        Action action = Main.CONFIG.var_safeMode ? Action.DISABLING : Action.ENABLING;
        switch (action) {
            case ENABLING:
                DiscordUtils.recievedMessage.getChannel().sendMessage(DiscordUtils.buildInfoEmbed("Safe mode enabled", "You will now be disconnected if you take damage while logged out.")).queue();
                Main.CONFIG.var_safeMode = true;
                break;
            case DISABLING:
                DiscordUtils.recievedMessage.getChannel().sendMessage(DiscordUtils.buildInfoEmbed("Safe mode disabled", "You will no longer be disconnected if you take damage.")).queue();
                Main.CONFIG.var_safeMode = false;
                break;
        }
    }
}