package com.sasha.queueskip;

import com.sasha.queueskip.localisation.EnumLocale;

import static com.sasha.queueskip.Main.LANG_MANAGER;

public class Util {

    public static boolean doStartupChecks(QSkipConfig config) {
        if (config.var_serverId.equalsIgnoreCase("[no default]")) {
            return false;
        }
        if (config.var_adminId.equalsIgnoreCase("[no default]")) {
            return false;
        }
        if (config.var_managerId.equalsIgnoreCase("[no default]")) {
            return false;
        }
        if (config.var_discordToken.equalsIgnoreCase("[no default]")) {
            return false;
        }
        return true;
    }

    public static boolean isConnected() {
        return Main.CONFIG.var_queueSkipEnabled && Main.INSTANCE.getReMinecraft().minecraftClient.getSession().isConnected();
    }

    public static boolean is2b2tDead(String msg) {
        return msg.toLowerCase().startsWith("exception connecting:");
    }

    public static boolean isWhisperTo(String s) {
        return s.matches("to .*: .*$");
    }

    public static boolean isWhisperFrom(String s) {
        return s.matches("^.* whispers: .*$");
    }

    public static boolean isServer(String s) {
        return s.startsWith("[SERVER]");
    }

    public static float asHearts(float health) {
        return health / (float) 2;
    }

    public static void registerLocalisation() {
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.welcome", "Welcome to QueueSkip " + Main.VERSION);
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.SPANISH, "queueskip.welcome", "Bienvenido a QueueSkip " + Main.VERSION);
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.welcome", "Bienvenue au QueueSkip " + Main.VERSION);

        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.body.welcome", "**Please read the #queueskip-help chanel in the QueueSkip Discord server**. You can also view the ;help command for a list of commands.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.body.welcome", "**Merci de lire le salon textuel #queueskip-help dans le server Discord**.\n" +
                "Vous pouvez aussi voir les commandes disponibles avec ;help.\n");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.SPANISH, "queueskip.body.welcome", "**Por favor leer la canal #queueskip-help en la servido Discord de QueueSkip***\n" +
                "Tú puedes además ;ayuda por las listos de comandos\n");

        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.err.args", "Invalid arguments!");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.SPANISH, "queueskip.err.args", "¡Argumento es inválido!");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.err.args", "Arguments Invalide!");

        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.uptime", "The software has been running for <time> without a reboot.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.uptime", "Le programme est en marche sans restart depuis <time>");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.SPANISH, "queueskip.uptime", "La programa ha sido trabajando por <time> sin reiniciar.");

        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.safeenable", "Safe mode enabled.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.safeenable", "Mode Sauf Activé.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.SPANISH, "queueskip.safeenable", "Modo seguro habilitado.");

        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.body.safeenable", "You will now be disconnected if you take damage while logged out.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.body.safeenable", "Vous serez déconnecté si vous prenez du dégâts.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.SPANISH, "queueskip.body.safeenable", "Serás desconectado si lo tomas dañar mientras está desconectado.");

        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.safedisable", "Safe mode disabled.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.safedisable", "Mode Sauf Désactivé.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.JAPANESE, "queueskip.safedisable", "セーフモードを無効にしていました");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.DUTCH, "queueskip.safedisable", "Veilige modus is niet meer actief");

        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.body.safedisable", "You will no longer be disconnected if you take damage.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.body.safedisable", "Vous serez plus déconnecté si vous prenez des dégâts.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.JAPANESE, "queueskip.body.safedisable", "あなたを害するば、あなたはディスコネクトいません");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.DUTCH, "queueskip.body.safedisable", "Je connectie wordt vanaf nu niet meer verbroken als je aangevallen wordt terwijl je offline bent.");

        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.inrangeenable", "In-range notifications enabled");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.inrangeenable", "Notifications de joueur en vue Activée");

        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.inrangeenable", "In-range notifications disabled.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.inrangeenable", "Notifications de joueur en vue Désactivé.");

        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.body.inrangeenable", "You will now receive Discord alerts if a player comes into range.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.body.inrangeenable", "Vous serez alerté si un joueur rentre dans votre champ de vue.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.JAPANESE, "queueskip.body.inrangeenable", "あなたのアカウントは人を見ますば、あなたを警告しています");

        LANG_MANAGER.registerLocalisedResponse(EnumLocale.ENG, "queueskip.body.inrangedisable", "You will no longer receive Discord alerts if a player comes into range.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.FRENCH, "queueskip.body.inrangedisable", "Vous ne serez plus alerté si un joueur rentre dans votre champ de vue.");
        LANG_MANAGER.registerLocalisedResponse(EnumLocale.JAPANESE, "queueskip.body.inrangedisable", "あなたのアカウントは人を見ますば、あなたを警告していません");
    }

}
