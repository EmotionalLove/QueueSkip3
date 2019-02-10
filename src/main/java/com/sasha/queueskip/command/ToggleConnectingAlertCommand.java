package com.sasha.queueskip.command;

import com.sasha.queueskip.DiscordUtils;
import com.sasha.queueskip.Main;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

@SimpleCommandInfo(description = "Toggle \"Connecting to 2b2t\" notifications", syntax = {""})
public class ToggleConnectingAlertCommand extends SimpleCommand {

    public ToggleConnectingAlertCommand() {
        super("connectalert");
    }

    @Override
    public void onCommand() {
        Action action = Main.CONFIG.var_connectingAlert ? Action.DISABLING : Action.ENABLING;
        switch (action) {
            case ENABLING:
                DiscordUtils.recievedMessage.getChannel().sendMessage(DiscordUtils.buildInfoEmbed("notifications enabled", "You will now receive Discord alerts when your account finishes queueing.")).queue();
                Main.CONFIG.var_connectingAlert = true;
                break;
            case DISABLING:
                DiscordUtils.recievedMessage.getChannel().sendMessage(DiscordUtils.buildInfoEmbed("notifications disabled", "You will no longer receive Discord alerts when your account finishes queueing.")).queue();
                Main.CONFIG.var_connectingAlert = false;
                break;
        }
    }
}