package com.sasha.queueskip;

import net.dv8tion.jda.core.entities.User;

public abstract class DiscordUtils {

    public static User getManager() {
        return Main.Jda.getUserById(Main.CONFIG.var_managerId);
    }
    public static User getAdministrator() {
        return Main.Jda.getUserById(Main.CONFIG.var_adminId);
    }

}
