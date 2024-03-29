package com.sasha.queueskip.event;

import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.data.message.TextMessage;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDisconnectPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListDataPacket;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import com.sasha.queueskip.DiscordUtils;
import com.sasha.queueskip.Main;
import com.sasha.queueskip.Util;
import com.sasha.reminecraft.ReMinecraft;
import com.sasha.reminecraft.api.event.*;
import com.sasha.reminecraft.client.ReClient;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.io.IOException;

import static com.sasha.queueskip.Main.CONFIG;
import static com.sasha.queueskip.Util.is2b2tDead;
import static com.sasha.queueskip.Util.isConnected;

public class MinecraftEvents implements SimpleListener {

    private Main qskip;

    public MinecraftEvents(Main qskip) {
        this.qskip = qskip;
    }

    @SimpleEventHandler
    public void onPostAuth(MojangAuthenticateEvent.Post e) {
        DiscordUtils.sendDebug("authentication with mojang completed.");
        if (!e.isSuccessful() && e.getMethod() == MojangAuthenticateEvent.Method.EMAILPASS) {
            e.setCancelled(true);
            if (CONFIG.var_newUser) {
                DiscordUtils.getUserChannel().sendMessage(DiscordUtils.buildErrorEmbed("Your Mojang account credentials are invalid!")).queue();
            } else {
                DiscordUtils.getUserChannel().sendMessage(DiscordUtils.buildErrorEmbed("Your Mojang account credentials are invalid!")).queue();
            }
        }
    }

    @SimpleEventHandler
    public void onPlayerVisible(EntityInRangeEvent.Player e) {
        if (CONFIG.var_inRange && isConnected() && !qskip.getReMinecraft().areChildrenConnected()) {
            DiscordUtils.getUserChannel().sendMessage(DiscordUtils.buildInfoEmbed("A player has entered visible range!", e.getName() + " has entered your account's visible range!")).submit();
        }
    }

    @SimpleEventHandler
    public void onPreAuth(MojangAuthenticateEvent.Pre e) {
        DiscordUtils.sendDebug("authentication with mojang requested.");
        if (!CONFIG.var_queueSkipEnabled) {
            Main.logger.logWarning("Queue Skip is disabled, RE:Minecraft will not continue.");
            e.setCancelled(true);
        }
    }

    @SimpleEventHandler
    public void onChildJoin(ChildJoinEvent e) {
        DiscordUtils.sendDebug("child user joined qskip server");
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            qskip.getReMinecraft().sendToChildren(new ServerChatPacket(Message.fromString("\2476You have been connected to 2b2t using queueskip " + Main.VERSION)));
        }).start();
    }

    // to Color: hi there
    @SimpleEventHandler
    public void onChat(ChatReceivedEvent e) {
        if (is2b2tDead(e.getMessageText())) {
            DiscordUtils.getUserChannel().sendMessage(DiscordUtils.buildErrorEmbed(e.getMessageText() + "\n\nDue to the above exception, QueueSkip3 is automatically requeuing")).queue(x -> {
                Main.INSTANCE.getReMinecraft().reLaunch();
            });
            return;
        }
        if (e.getMessageText().toLowerCase().contains("connecting to the server")) {
            DiscordUtils.getUserChannel().sendMessage(DiscordUtils.buildInfoEmbed("Connecting to 2b2t", "Your account has completed the queuing process")).queue();
            return;
        }
        if (!qskip.getReMinecraft().areChildrenConnected() && Util.isWhisperTo(e.getMessageText().toLowerCase())) {
            DiscordUtils.sendDebug("is a whisper to msg.");
            String[] begin = e.getMessageText().substring(0, e.getMessageText().indexOf(":")).split(" ");
            String who = begin[1].replace(":", "");
            String msg = e.getMessageText().substring(e.getMessageText().indexOf(":") + 2);
            try {
                if (msg.equalsIgnoreCase("owo 2b2t")) return; // xd this is for the queueskip spammer.
                DiscordUtils.getUserChannel().sendMessage
                        (DiscordUtils.buildWhisperToEmbed(who, msg)).queue();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return;
        }
        if (!qskip.getReMinecraft().areChildrenConnected() && Util.isWhisperFrom(e.getMessageText().toLowerCase())) {
            DiscordUtils.sendDebug("is a whisper from msg.");
            String who = e.getMessageText().substring(0, e.getMessageText().indexOf(" "));
            String msg = e.getMessageText().substring(e.getMessageText().indexOf(":") + 2);
            qskip.getReMinecraft().minecraftClient.getSession().send(new ClientChatPacket("/w " + who + " [QueueSkip] This player is currently AFK!"));
            try {
                DiscordUtils.getUserChannel().sendMessage(DiscordUtils.buildWhisperFromEmbed(who, msg)).queue();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return;
        }
        if (!qskip.getReMinecraft().areChildrenConnected() && Util.isServer(e.getMessageText())) {
            DiscordUtils.sendDebug("is a server announcement.");
            String message = e.getMessageText().replace("[SERVER] ", "");
            DiscordUtils.getUserChannel().sendMessage(DiscordUtils.buildServerAnnouncementEmbed(message)).queue();
        }
    }

    @SimpleEventHandler
    public void onKick(RemoteServerPacketRecieveEvent e) {
        if (e.getRecievedPacket() instanceof ServerDisconnectPacket) {
            MessageEmbed embed = DiscordUtils.buildErrorEmbed("[Disconnected] " + ((ServerDisconnectPacket) e.getRecievedPacket()).getReason().getText().replaceAll("§.", ""));
            DiscordUtils.getUserChannel().sendMessage(embed).queue();
        }
        if (e.getRecievedPacket() instanceof ServerPlayerListDataPacket) {
            ServerPlayerListDataPacket pck = (ServerPlayerListDataPacket) e.getRecievedPacket();
            ReClient.ReClientCache.INSTANCE.tabFooter = new TextMessage(pck.getFooter().getFullText() + "\n\2476Powered by QueueSkip " + Main.VERSION + " by Sasha\n");
            ReClient.ReClientCache.INSTANCE.tabHeader = pck.getHeader();
            ReMinecraft.INSTANCE.sendToChildren(new ServerPlayerListDataPacket(ReClient.ReClientCache.INSTANCE.tabHeader, ReClient.ReClientCache.INSTANCE.tabFooter));
            e.setCancelled(true);
        }
    }

    @SimpleEventHandler
    public void onHurt(PlayerDamagedEvent e) {
        DiscordUtils.sendDebug("player health updated.");
        if (isConnected() && !qskip.getReMinecraft().areChildrenConnected() && CONFIG.var_safeMode) {
            if (e.getOldHealth() - e.getNewHealth() < 0.1f) return;
            if (e.getNewHealth() > 7f) return;
            DiscordUtils.getUserChannel().sendMessage(DiscordUtils.buildInfoEmbed("Disconnected", "You were damaged " + Util.asHearts(e.getOldHealth() - e.getNewHealth()) + " hearts. You were requeued because Safe mode is on.")).queue(ee -> ReMinecraft.INSTANCE.reLaunch());
        }
    }

}
