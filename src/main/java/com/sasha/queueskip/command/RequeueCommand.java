package com.sasha.queueskip.command;

import com.sasha.queueskip.DiscordUtils;
import com.sasha.queueskip.Main;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;

@SimpleCommandInfo(description = "Relog into 2b2t",
syntax = {""})
public class RequeueCommand extends SimpleCommand {
    public RequeueCommand() {
        super("requeue");
    }

    @Override
    public void onCommand() {
        if (!Main.CONFIG.var_queueSkipEnabled) {
            DiscordUtils.recievedMessage.getChannel().sendMessage(DiscordUtils.buildErrorEmbed("You can't requeue your account because you have QueueSkip disabled.")).queue();
            return;
        }
        DiscordUtils.recievedMessage.getChannel().sendMessage(DiscordUtils.buildInfoEmbed("Requeuing"
                , "Your account is requeuing, this may take about " + Main.INSTANCE.getReMinecraft().MAIN_CONFIG.var_reconnectDelaySeconds + " seconds.")).queue();
        Main.INSTANCE.getReMinecraft().reLaunch();
    }
}
