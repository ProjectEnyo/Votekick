package me.miicro.votekick.util;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class MessageSender {
    private String pluginPrefix = "[Votekick] ";
    private String cPluginPrefix = "&C[&FVotekick&C]&F ";


    public void sendToPlayer (Player p, String message) {
        p.sendMessage(cPluginPrefix + message);
    }

    public void broadcastMessage (Server server, String message) {
        server.broadcastMessage(cPluginPrefix + message);
    }

    public void sendToConsole (Server server, String message) {
        server.getConsoleSender().sendMessage(pluginPrefix + message);
    }

}
