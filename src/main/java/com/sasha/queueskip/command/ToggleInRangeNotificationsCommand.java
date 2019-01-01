package com.sasha.queueskip.command;

import com.sasha.queueskip.DiscordUtils;
import com.sasha.queueskip.Main;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

@SimpleCommandInfo(description = "Toggle \"player in range\" notifications", syntax = {""})
public class ToggleInRangeNotificationsCommand extends SimpleCommand {

    public ToggleInRangeNotificationsCommand() {
        super("inrange");
    }

    @Override
    public void onCommand() {
        Action action = Main.CONFIG.var_inRange ? Action.DISABLING : Action.ENABLING;
        switch (action) {
            case ENABLING:
                DiscordUtils.recievedMessage.getChannel().sendMessage(DiscordUtils.buildInfoEmbed("In-range notifications enabled", "You will now receive Discord alerts if a player comes into range.")).queue();
                Main.CONFIG.var_safeMode = true;
                break;
            case DISABLING:
                DiscordUtils.recievedMessage.getChannel().sendMessage(DiscordUtils.buildInfoEmbed("In-range notifications disabled", "You will no longer receive Discord alerts if a player comes into range.")).queue();
                Main.CONFIG.var_safeMode = false;
                break;
        }
    }
}