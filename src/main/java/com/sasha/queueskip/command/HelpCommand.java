package com.sasha.queueskip.command;

import com.sasha.queueskip.DiscordUtils;
import com.sasha.simplecmdsys.SimpleCommand;
import com.sasha.simplecmdsys.SimpleCommandInfo;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;

import static com.sasha.queueskip.Main.COMMAND_PROCESSOR;

@SimpleCommandInfo(description = "Show this message", syntax = {""})
public class HelpCommand extends SimpleCommand {

    public HelpCommand() {
        super("help");
    }

    @Override
    public void onCommand() {
        try {
            EmbedBuilder b = new EmbedBuilder();
            b.setColor(Color.LIGHT_GRAY);
            b.setTitle("Help");
            StringBuilder sb = new StringBuilder();
            COMMAND_PROCESSOR.getCommandRegistry().forEach((clazz, cmdObj) -> {
                SimpleCommand cmd = (SimpleCommand) cmdObj;
                sb.append("\n**;").append(cmd.getCommandName()).append("** - ").append(COMMAND_PROCESSOR.getDescription(clazz)).append("\n(syntax:");
                for (String argument : COMMAND_PROCESSOR.getSyntax(clazz)) {
                    sb.append(" `;").append(cmd.getCommandName()).append(" ").append(argument).append("`");
                }
                sb.append(")");
            });
            b.setDescription(sb);
            DiscordUtils.recievedMessage.getChannel().sendMessage(b.build()).queue();
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
