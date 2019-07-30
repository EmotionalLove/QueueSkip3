package com.sasha.queueskip.command;

import com.sasha.queueskip.DiscordUtils;
import com.sasha.queueskip.Main;
import com.sasha.simplecmdsys.SimpleCommandInfo;
import me.someonelove.bettercommandsystem.Command;

import java.io.PrintWriter;
import java.io.StringWriter;

@SimpleCommandInfo(description = "Toggle \"player in range\" notifications", syntax = {""})
public class ToggleInRangeNotificationsCommand extends Command {

    public ToggleInRangeNotificationsCommand() {
        super("inrange");
    }

    @Override
    public void onCommand(boolean hasArgs, String[] args) {
        try {
            Action action = Main.CONFIG.var_inRange ? Action.DISABLING : Action.ENABLING;
            switch (action) {
                case ENABLING:
                    DiscordUtils.recievedMessage.getChannel().sendMessage(DiscordUtils.buildInfoEmbed("In-range notifications enabled", "You will now receive Discord alerts if a player comes into range.")).queue();
                    Main.CONFIG.var_inRange = true;
                    break;
                case DISABLING:
                    DiscordUtils.recievedMessage.getChannel().sendMessage(DiscordUtils.buildInfoEmbed("In-range notifications disabled", "You will no longer receive Discord alerts if a player comes into range.")).queue();
                    Main.CONFIG.var_inRange = false;
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