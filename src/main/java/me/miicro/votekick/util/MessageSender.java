package me.miicro.votekick.util;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class MessageSender {
  private static final String PLUGIN_PREFIX = "[Votekick] ";
  private static final String CPLUGIN_PREFIX = "&3[Votekick]&f ";

  public static void sendToPlayer(Player p, String message, boolean isWarn) {
    if (isWarn) {
      p.sendMessage(ChatColor.translateAlternateColorCodes('&', CPLUGIN_PREFIX + "&c" + message));
    } else {
      p.sendMessage(ChatColor.translateAlternateColorCodes('&', CPLUGIN_PREFIX + message));
    }
  }

  public static void broadcastMessage(Server server, String message) {
    server.broadcastMessage(ChatColor.translateAlternateColorCodes('&', CPLUGIN_PREFIX + message));
  }

  public static void sendToConsole(Server server, String message) {
    server
        .getConsoleSender()
        .sendMessage(ChatColor.translateAlternateColorCodes('&', PLUGIN_PREFIX + message));
  }
}
