package com.sasha.queueskip;

import com.sasha.reminecraft.Configuration;

import java.util.ArrayList;

public class QSkipConfig extends Configuration {

    @ConfigSetting
    public String var_discordToken;
    @ConfigSetting
    public String var_managerId;
    @ConfigSetting
    public String var_adminId = "552213176582799372";
    @ConfigSetting
    public String var_channelId;
    @ConfigSetting
    public String var_serverId = "569109575383515137";
    @ConfigSetting
    public boolean var_queueSkipEnabled = true;
    @ConfigSetting
    public boolean var_safeMode = false;
    @ConfigSetting
    public boolean var_inRange = true;
    @ConfigSetting
    public boolean var_newUser = true;
    @ConfigSetting
    public boolean var_debug = false;
    @ConfigSetting
    public boolean var_connectingAlert = true;
    @ConfigSetting
    public boolean var_spamChat = true;
    @ConfigSetting
    public ArrayList<String> var_spamMessages = new ArrayList<>();

    {
        var_spamMessages.add("> QueueSkip Discord - https://discord.gg/YKRY6P3 <");
        var_spamMessages.add("> 2b2t style anarchy, without the queue - nemuii.org <");
        var_spamMessages.add("> This account is connected with QueueSkip - https://discord.gg/YKRY6P3 <");
        var_spamMessages.add("> Don't pay $20 for the priority, pay $5 for the skip - https://discord.gg/YKRY6P3 <");
        var_spamMessages.add("> No queue, fresh world, elytra enabled - nemuii.org <");
        var_spamMessages.add("> Meet a rare female 2b2t player - https://discord.gg/YKRY6P3 <");
        var_spamMessages.add("/w RusherB0T owo 2b2t");
    }

    QSkipConfig() {
        super("QueueSkip3");
    }
}
