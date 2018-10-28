package com.sasha.queueskip;

import com.sasha.reminecraft.Configuration;

public class QSkipConfig extends Configuration {

    @ConfigSetting public String var_discordToken;
    @ConfigSetting public String var_managerId;
    @ConfigSetting public String var_adminId = "483665844866514944";
    @ConfigSetting public boolean var_queueSkipEnabled = true;


    QSkipConfig() {
        super("QueueSkip3");
    }
}
