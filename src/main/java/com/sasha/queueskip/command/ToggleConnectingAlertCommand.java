package com.sasha.queueskip.command;

import com.sasha.queueskip.DiscordUtils;
import com.sasha.queueskip.Main;
import com.sasha.simplecmdsys.SimpleCommandInfo;
import me.someonelove.bettercommandsystem.Command;

import java.io.PrintWriter;
import java.io.StringWriter;

@SimpleCommandInfo(description = "Toggle \"Connecting to 2b2t\" notifications", syntax = {""})
public class ToggleConnectingAlertCommand extends Command {

    public ToggleConnectingAlertCommand() {
        super("connectalert");
    }

    @Override
    public void onCommand(boolean hasArgs, String[] args) {
        try {
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