package me.miicro.votekick.util;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class MessageSender {
    private final static String PLUGIN_PREFIX = "[Votekick] ";
    private final static String CPLUGIN_PREFIX = "&C[&FVotekick&C]&F ";

    public static void sendToPlayer (Player p, String message) {
        p.sendMessage(CPLUGIN_PREFIX + message);
    }

    public static void broadcastMessage (Server server, String message) {
        server.broadcastMessage(CPLUGIN_PREFIX + message);
    }

    public static void sendToConsole (Server server, String message) {
        server.getConsoleSender().sendMessage(PLUGIN_PREFIX + message);
    }

}
