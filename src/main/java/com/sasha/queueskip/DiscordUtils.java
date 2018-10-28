package com.sasha.queueskip;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

public abstract class DiscordUtils {

    public static Message recievedMessage;

    public static User getManager() {
        return Main.Jda.getUserById(Main.CONFIG.var_managerId);
    }
    public static User getAdministrator() {
        return Main.Jda.getUserById(Main.CONFIG.var_adminId);
    }

    public static MessageEmbed buildErrorEmbed(String message) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.RED);
        builder.setTitle("Error");
        builder.setDescription(message);
        return builder.build();
    }
    public static MessageEmbed buildInfoEmbed(String title, String message) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.RED);
        builder.setTitle(title);
        builder.setDescription(message);
        return builder.build();
    }

}
