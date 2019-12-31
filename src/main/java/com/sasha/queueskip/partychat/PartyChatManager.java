package com.sasha.queueskip.partychat;

import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.sasha.queueskip.Main;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import org.json.JSONException;
import org.json.JSONObject;

public class PartyChatManager {

    private JDA discord;
    private boolean partyChatModeEnabled;

    public PartyChatManager(JDA discord) {
        this.discord = discord;
        discord.addEventListener(this);
    }

    @SubscribeEvent
    public void onMsgRx(GuildMessageReceivedEvent e) {
        if (e.getChannel().getIdLong() != getPartyChatChannel().getIdLong()) {
            return;
        }
        String raw = e.getMessage().getContentRaw();
        if (raw.startsWith("```") && raw.endsWith("```")) {
            String content = raw.substring(4, raw.length() - 3).trim();
            try {
                JSONObject object = new JSONObject(content);
                // name - mc username of sender
                // discord - discord id of manager
                // content - the msg
                String name = object.getString("name");
                String discord = object.getString("discord");
                String msgContent = object.getString("content");
                User u = this.discord.getUserById(discord);
                String f = String.format("&8[&eParty&8] &7<%s | %s> &f%s", name, u != null ? u.getAsTag() : "???", msgContent).trim();
                Main.INSTANCE.getReMinecraft().sendToChildren(new ServerChatPacket(f.replace("&", "\247")));
            } catch (JSONException x) {
                // x.printStackTrace();
            }
        }
    }

    private TextChannel getPartyChatChannel() {
        return discord.getGuildById(Main.CONFIG.var_serverId).getTextChannelsByName("party-chat", false).get(0);
    }
}
