package com.sasha.queueskip.command;

import com.sasha.queueskip.DiscordUtils;
import com.sasha.reminecraft.ReMinecraft;
import com.sasha.simplecmdsys.SimpleCommandInfo;
import me.someonelove.bettercommandsystem.Command;

import java.io.PrintWriter;
import java.io.StringWriter;

import static com.sasha.queueskip.Main.LANG_MANAGER;

@SimpleCommandInfo(description = "Log into a Mojang account", syntax = {"<email> <password>"})
public class LoginCommand extends Command {

    public LoginCommand() {
        super("login");
    }

    @Override
    public void onCommand(boolean hasArgs, String[] args) {
        try {
            if (args == null || args.length != 2) {
                DiscordUtils.recievedMessage.getChannel().sendMessage(DiscordUtils.buildErrorEmbed(LANG_MANAGER.resolve("args.err"))).queue();
                return;
            }
            String email = args[0];
            String password = args[1];
            ReMinecraft.INSTANCE.MAIN_CONFIG.var_mojangEmail = email;
            ReMinecraft.INSTANCE.MAIN_CONFIG.var_mojangPassword = password;
            ReMinecraft.INSTANCE.MAIN_CONFIG.var_sessionId = "dsljf";
            DiscordUtils.recievedMessage.getChannel().sendMessage
                    (DiscordUtils.buildInfoEmbed("Credentials Updated", "Your credentials have been updated!")).queue();
            ReMinecraft.INSTANCE.reLaunch();
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
