package com.sasha.queueskip.command;

import com.sasha.queueskip.DiscordUtils;
import com.sasha.queueskip.Main;
import com.sasha.simplecmdsys.SimpleCommand;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;

import static com.sasha.queueskip.Main.COMMAND_PROCESSOR;

public class HelpCommand extends SimpleCommand {

    public HelpCommand() {
        super("help");
    }

    @Override
    public void onCommand() {
        EmbedBuilder b = new EmbedBuilder();
        b.setColor(Color.LIGHT_GRAY);
        b.setTitle("Help");
        StringBuilder sb = new StringBuilder();
        COMMAND_PROCESSOR.getCommandRegistry().forEach((clazz, cmdObj) -> {
            SimpleCommand cmd = (SimpleCommand) cmdObj;
            sb.append("\n**-").append(cmd.getCommandName()).append("** - ").append(COMMAND_PROCESSOR.getDescription(clazz)).append("\n(syntax:");
            for (String argument : COMMAND_PROCESSOR.getSyntax(clazz)) {
                sb.append(" `-").append(cmd.getCommandName()).append(" ").append(argument).append("`");
            }
            sb.append(")");
        });
        b.setDescription(sb);
        DiscordUtils.recievedMessage.getChannel().sendMessage(b.build()).queue();
    }
}
