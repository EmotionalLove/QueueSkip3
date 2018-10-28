package com.sasha.queueskip.command;

import com.sasha.queueskip.DiscordUtils;
import com.sasha.reminecraft.ReMinecraft;
import com.sasha.simplecmdsys.SimpleCommand;

public class LoginCommand extends SimpleCommand {

    public LoginCommand() {
        super("login");
    }

    @Override
    public void onCommand() {
        if (this.getArguments() == null || this.getArguments().length != 2) {
            DiscordUtils.recievedMessage.getChannel().sendMessage(DiscordUtils.buildErrorEmbed("Invalid arguments!")).queue();
            return;
        }
        String email = this.getArguments()[0];
        String password = this.getArguments()[1];
        ReMinecraft.INSTANCE.MAIN_CONFIG.var_mojangEmail = email;
        ReMinecraft.INSTANCE.MAIN_CONFIG.var_mojangPassword = password;
        ReMinecraft.INSTANCE.MAIN_CONFIG.var_sessionId = "dsljf";
        DiscordUtils.recievedMessage.getChannel().sendMessage
                (DiscordUtils.buildInfoEmbed("Credentials Updated", "Your credentials have been updated!")).queue();
    }
}
