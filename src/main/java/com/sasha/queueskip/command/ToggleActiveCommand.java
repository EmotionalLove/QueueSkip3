package com.sasha.queueskip.command;

import com.sasha.queueskip.DiscordUtils;
import com.sasha.queueskip.Main;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

import java.io.PrintWriter;
import java.io.StringWriter;

@SimpleCommandInfo(description = "Enable or disable the queueskip service",
syntax = {""})
public class ToggleActiveCommand extends SimpleCommand {

    public ToggleActiveCommand() {
        super("active");
    }

    @Override
    public void onCommand() {
        try {
            Action action = Main.CONFIG.var_queueSkipEnabled ? Action.DISABLING : Action.ENABLING;
            switch (action) {
                case ENABLING:
                    DiscordUtils.recievedMessage.getChannel().sendMessage(DiscordUtils.buildInfoEmbed("QueueSkip enabled", "QueueSkip has been enabled. It may take about " + Main.INSTANCE.getReMinecraft().MAIN_CONFIG.var_reconnectDelaySeconds + " seconds for your account to log in")).queue();
                    Main.CONFIG.var_queueSkipEnabled = true;
                    Main.INSTANCE.getReMinecraft().reLaunch();
                    break;
                case DISABLING:
                    DiscordUtils.recievedMessage.getChannel().sendMessage(DiscordUtils.buildInfoEmbed("QueueSkip disabled", "QueueSkip has been disabled")).queue();
                    Main.CONFIG.var_queueSkipEnabled = false;
                    Main.INSTANCE.getReMinecraft().reLaunch();
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
enum Action {
    DISABLING, ENABLING
}
