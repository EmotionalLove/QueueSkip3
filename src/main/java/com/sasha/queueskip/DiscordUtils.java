package com.sasha.queueskip;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.function.Consumer;

public abstract class DiscordUtils {

    public static Message recievedMessage;

    public static User getManager() {
        return Main.discord.getUserById(Main.CONFIG.var_managerId);
    }

    public static User getAdministrator() {
        return Main.discord.getUserById(Main.CONFIG.var_adminId);
    }

    public static TextChannel getUserChannel() {
        Guild guild = Main.discord.getGuildById(Main.CONFIG.var_serverId);
        Category cat = guild.getCategoriesByName("queueskip", true).get(0);
        if (cat == null) return null;
        for (TextChannel textChannel : cat.getTextChannels()) {
            if (textChannel.getId().equals(Main.CONFIG.var_channelId)) return textChannel;
        }
        return null;
    }

    public static void generateUserChannel(@Nullable Consumer<TextChannel> consumer) {
        Guild guild = Main.discord.getGuildById(Main.CONFIG.var_serverId);
        Category cat = guild.getCategoriesByName("queueskip", true).get(0);
        Member member = guild.getMemberById(Main.CONFIG.var_managerId);
        if (cat == null) throw new IllegalStateException("QueueSkip category does not exist.");
        cat.createTextChannel("control-panel").addPermissionOverride(member, Permission.MESSAGE_READ.getRawValue(), 0L).queue(channel -> {
            Main.CONFIG.var_channelId = channel.getId();
            if (consumer != null) consumer.accept((TextChannel) channel);
        });
    }

    public static void generateUserChannel() {
        generateUserChannel(null);
    }

    public static MessageEmbed buildErrorEmbed(String message) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.RED);
        builder.setTitle("Error");
        builder.setDescription(message);
        return builder.build();
    }

    public static MessageEmbed buildServerAnnouncementEmbed(String message) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.YELLOW);
        builder.setTitle("Server Announcement");
        builder.setDescription(message);
        return builder.build();
    }

    public static MessageEmbed buildInfoEmbed(String title, String message) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.CYAN);
        builder.setTitle(title);
        builder.setDescription(message);
        return builder.build();
    }

    public static MessageEmbed buildWhisperToEmbed(String recipiant, String message) throws IOException {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(0xff54e2); // purple
        builder.setTitle("You whisper to " + recipiant);
        builder.setDescription(message);
        String s = "https://visage.surgeplay.com/face/" + getUuidFromName(recipiant.trim());
        builder.setThumbnail(s);
        return builder.build();
    }

    public static MessageEmbed buildWhisperFromEmbed(String recipiant, String message) throws IOException {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(0xff54e2); // purple
        builder.setTitle(recipiant + " whispers");
        builder.setDescription(message);
        String s = "https://visage.surgeplay.com/face/" + getUuidFromName(recipiant.trim());
        builder.setThumbnail(s);
        return builder.build();
    }

    public static String getUuidFromName(String name) throws IOException {
        String json;
        String boi = "";
        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        while ((json = reader.readLine()) != null) {
            if (json.contains("{")) {
                boi = json;
                break;
            }
        }
        JsonElement element = new JsonParser().parse(boi);
        JsonObject obj = element.getAsJsonObject();
        String uuid = obj.get("id").getAsString();
        System.out.println(uuid);
        return uuid;
    }

    public static void sendDebug(String msg) {
        if (!Main.CONFIG.var_debug) return;
        DiscordUtils.getUserChannel().sendMessage("(debug) " + msg).submit();
    }

}
