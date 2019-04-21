package com.sasha.queueskip;

import com.sasha.reminecraft.Configuration;

public class QSkipConfig extends Configuration {

    @ConfigSetting public String var_discordToken;
    @ConfigSetting public String var_managerId;
    @ConfigSetting
    public String var_adminId = "552213176582799372";
    @ConfigSetting
    public String var_channelId;
    @ConfigSetting
    public String var_serverId = "569109575383515137";
    @ConfigSetting public boolean var_queueSkipEnabled = true;
    @ConfigSetting public boolean var_safeMode = false;
    @ConfigSetting public boolean var_inRange = true;
    @ConfigSetting public boolean var_newUser = true;
    @ConfigSetting public boolean var_debug = false;
    @ConfigSetting public boolean var_connectingAlert = true;

    QSkipConfig() {
        super("QueueSkip3");
    }
}
