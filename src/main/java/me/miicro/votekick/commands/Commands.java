package me.miicro.votekick.commands;

import me.miicro.votekick.Votekick;
import me.miicro.votekick.util.MessageSender;
import me.miicro.votekick.vote.VoteExecutor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

  private Votekick votekick;
  private VoteExecutor voteExecutor;
  private Server server;

  public Commands(Votekick votekick, VoteExecutor voteExecutor) {
    this.votekick = votekick;
    this.voteExecutor = voteExecutor;
    this.server = votekick.getServer();
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    if (!(sender instanceof Player)) {
      return true;
    }

    if (args.length != 1) {
      return false;
    }

    String parameter = args[0];
    Player p = (Player) sender;

    // Check permissions first
    if ((parameter.equalsIgnoreCase(CommandArgs.RELOAD.value)
            || parameter.equalsIgnoreCase(CommandArgs.STOP.value))
        && (!p.isOp() || !p.hasPermission("votekick.staff"))) {
      MessageSender.sendToPlayer(p, "You don't have permission to do this!", true);
      return true;
    }

    if (parameter.equalsIgnoreCase(CommandArgs.RELOAD.value)) {
      voteExecutor.reloadConfig();
      MessageSender.sendToPlayer(p, "Config reloaded.", false);
      return true;
    }

    // Vote happening
    if (voteExecutor.getIsVoting()) {
      // Voted yes/no, check if player is not voting for themselves
      if (parameter.equalsIgnoreCase(CommandArgs.YES.value)
          || parameter.equalsIgnoreCase(CommandArgs.NO.value)) {
        if (p.getUniqueId().equals(voteExecutor.getPlayerInVoting().getUniqueId())) {
          MessageSender.sendToPlayer(p, "You may not participate in this vote!", true);
          return true;
        }
        voteExecutor.castVote(p, parameter.toLowerCase());
      }
      // handle other args (STOP and RELOAD should work, RELOAD is handled earlier)
      else {
        if ((parameter.equalsIgnoreCase(CommandArgs.STOP.value)
                || parameter.equalsIgnoreCase(CommandArgs.RELOAD.value))
            && (p.isOp() || p.hasPermission("votekick.staff"))) {
          if (parameter.equalsIgnoreCase(CommandArgs.STOP.value)) {
            voteExecutor.stopVote(p);
          }
        } else {
          MessageSender.sendToPlayer(p, "Only one vote at a time is allowed!", true);
        }
      }
    }
    // No vote going on
    else {
      // Can't run YES/NO/STOP if vote's not going on
      if (parameter.matches(
          CommandArgs.YES.value + "|" + CommandArgs.NO.value + "|" + CommandArgs.STOP.value)) {
        MessageSender.sendToPlayer(p, "No ongoing vote!", true);
        return true;
      }
      Player votePlayer = server.getPlayerExact(parameter);

      if (votePlayer == null) {
        MessageSender.sendToPlayer(p, "No such player online!", true);
      } else {
        if (p.getUniqueId().equals(votePlayer.getUniqueId())) {
          MessageSender.sendToPlayer(p, "You cannot start a vote against yourself!", true);
        } else if (votePlayer.isOp() || votePlayer.hasPermission("votekick.staff")) {
          MessageSender.sendToPlayer(p, "You cannot start a vote against a staff member!", true);
        } else {
          voteExecutor.starVote(p, votePlayer);
        }
      }
    }

    return true;
  }
}
