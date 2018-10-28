package com.sasha.queueskip;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

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
        String uuid =obj.get("id").getAsString();
        System.out.println(uuid);
        return uuid;
    }

}
