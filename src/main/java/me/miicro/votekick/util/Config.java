package me.miicro.votekick.util;

import java.io.File;
import java.io.IOException;
import me.miicro.votekick.Votekick;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {

  private Votekick vk;
  private File file;
  private FileConfiguration config;

  public Config(Votekick vk) {
    this.vk = vk;
    reload();
  }

  public void reload() {
    try {
      file = new File(vk.getDataFolder(), "config.yml");
      if (!file.exists()) {
        vk.saveResource("config.yml", false);
      }
      config = YamlConfiguration.loadConfiguration(file);
    } catch (Exception e) {
      vk.getLogger()
          .info("There has been an issue trying to read config file. \n" + e.getMessage());
    }
  }

  private void saveConfig() {
    try {
      config.save(file);
    } catch (IOException e) {
      vk.getLogger()
          .info("There has been an issue trying to save config file. \n" + e.getMessage());
    }
  }

  public int getMinPlayers() {
    if (config.get("min-players") == null) {
      config.set("min-players", 2);
      saveConfig();
    }
    return config.getInt("min-players");
  }

  public int getVoteTime() {
    if (config.get("vote-time") == null) {
      config.set("vote-time", 30);
      saveConfig();
    }
    return config.getInt("vote-time");
  }

  public double getVotePercentage() {
    if (config.get("needed-votes") == null) {
      config.set("needed-votes", 0.5);
      saveConfig();
    }
    double nVotes = config.getDouble("needed-votes");
    if (nVotes > 1) {
      nVotes = nVotes / 100;
    }
    return nVotes;
  }

  public String getCommand() {
    if (config.getString("command") == null) {
      config.set("command", "kick %s You have been vote kicked off the server!");
      saveConfig();
    }
    return config.getString("command");
  }

  public boolean getPlaySound() {
    if (config.get("play-sound") == null) {
      config.set("play-sound", true);
      saveConfig();
    }
    return config.getBoolean("play-sound");
  }
}
