package me.miicro.votekick;

import me.miicro.votekick.commands.Commands;
import me.miicro.votekick.util.Config;
import me.miicro.votekick.vote.VoteExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class Votekick extends JavaPlugin {

  @Override
  public void onEnable() {
    this.getCommand("votekick")
        .setExecutor(new Commands(this, new VoteExecutor(this, new Config(this))));
  }
}
