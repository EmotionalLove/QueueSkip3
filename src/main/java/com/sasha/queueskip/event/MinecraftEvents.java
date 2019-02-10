package com.sasha.queueskip.event;

import com.github.steveice10.mc.protocol.data.message.Message;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.sasha.eventsys.SimpleEventHandler;
import com.sasha.eventsys.SimpleListener;
import com.sasha.queueskip.DiscordUtils;
import com.sasha.queueskip.Main;
import com.sasha.reminecraft.ReMinecraft;
import com.sasha.reminecraft.api.event.*;

import java.io.IOException;

import static com.sasha.queueskip.Main.CONFIG;
import static com.sasha.queueskip.Main.isConnected;

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
            DiscordUtils.getManager().openPrivateChannel()
                    .queue(dm -> dm.sendMessage(DiscordUtils.buildErrorEmbed("Your Mojang account credentials are invalid!"))
                            .queue(), fail -> {
                        DiscordUtils.getAdministrator().openPrivateChannel().queue(a -> {
                            a.sendMessage(DiscordUtils.buildErrorEmbed(DiscordUtils.getManager().getName() + "'s DM's can't be reached, and their account credentials are invalid!")).submit();
                        });
                    });
        }
    }

    @SimpleEventHandler
    public void onPlayerVisible(EntityInRangeEvent.Player e) {
        if (CONFIG.var_inRange && isConnected() && !qskip.getReMinecraft().areChildrenConnected()) {
            DiscordUtils.getManager().openPrivateChannel().queue(dm -> {
                dm.sendMessage(DiscordUtils.buildInfoEmbed("A player has entered visible range!", e.getName() + " has entered your account's visible range!")).submit();
            });
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
            qskip.getReMinecraft().sendToChildren(new ServerChatPacket(Message.fromString("\2476You have been connected to 2b2t via queueskip " + Main.VERSION)));
        }).start();
    }

    // to Color: hi there
    @SimpleEventHandler
    public void onChat(ChatReceivedEvent e) {
        DiscordUtils.sendDebug("chat message received for processing.");
        Main.lastMsg = System.currentTimeMillis();
        if (e.getMessageText().toLowerCase().contains("connecting to the server")) {
            DiscordUtils.getManager().openPrivateChannel().queue(pm -> {
                pm.sendMessage
                        (DiscordUtils.buildInfoEmbed("Connecting to 2b2t", "Your account has completed the queuing process")).queue();
            });
            return;
        }
        if (qskip.isWhisperTo(e.getMessageText().toLowerCase())) {
            DiscordUtils.sendDebug("is a whisper to msg.");
            String[] begin = e.getMessageText().substring(0, e.getMessageText().indexOf(":")).split(" ");
            String who = begin[1].replace(":", "");
            String msg = e.getMessageText().substring(e.getMessageText().indexOf(":") + 2);
            DiscordUtils.getManager().openPrivateChannel().queue(pm -> {
                try {
                    pm.sendMessage
                            (DiscordUtils.buildWhisperToEmbed(who, msg)).queue();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            return;
        }
        if (qskip.isWhisperFrom(e.getMessageText().toLowerCase())) {
            DiscordUtils.sendDebug("is a whisper from msg.");
            String who = e.getMessageText().substring(0, e.getMessageText().indexOf(" "));
            String msg = e.getMessageText().substring(e.getMessageText().indexOf(":") + 2);
            DiscordUtils.getManager().openPrivateChannel().queue(pm -> {
                try {
                    pm.sendMessage
                            (DiscordUtils.buildWhisperFromEmbed(who, msg)).queue();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    @SimpleEventHandler
    public void onHurt(PlayerDamagedEvent e) {
        DiscordUtils.sendDebug("player health updated.");
        if (isConnected() && !qskip.getReMinecraft().areChildrenConnected() && CONFIG.var_safeMode) {
            DiscordUtils.getManager().openPrivateChannel().queue(dm -> {
                if (e.getOldHealth() - e.getNewHealth() < 0.1f) return;
                dm.sendMessage(DiscordUtils.buildInfoEmbed("Disconnected", "You were damaged " + qskip.asHearts(e.getOldHealth() - e.getNewHealth()) + " hearts. You were requeued because Safe mode is on.")).queue(ee -> ReMinecraft.INSTANCE.reLaunch());
            });
        }
    }

}
