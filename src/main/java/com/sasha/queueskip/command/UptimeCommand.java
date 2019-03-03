package com.sasha.queueskip.command;

import com.sasha.queueskip.DiscordUtils;
import com.sasha.queueskip.Main;
import com.sasha.simplecmdsys.SimpleCommand;

import java.util.concurrent.TimeUnit;

public class UptimeCommand extends SimpleCommand {

    public UptimeCommand() {
        super("uptime");
    }

    @Override
    public void onCommand() {
        DiscordUtils.recievedMessage.getChannel()
                .sendMessage("```\nThe software has been running for " + formatUptime(System.currentTimeMillis(), Main.uptime) + " without a reboot.\n```").submit();
    }

    private static String formatUptime(long then, long now) {
        long uptime = then - now;
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(uptime),
                TimeUnit.MILLISECONDS.toMinutes(uptime) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(uptime)),
                TimeUnit.MILLISECONDS.toSeconds(uptime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(uptime)));
    }

}
