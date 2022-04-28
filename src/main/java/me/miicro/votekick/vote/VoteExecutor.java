package me.miicro.votekick.vote;

import java.util.*;
import me.miicro.votekick.Votekick;
import me.miicro.votekick.util.Config;
import me.miicro.votekick.util.MessageSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class VoteExecutor {

  private Config config;
  private boolean isVoting = false;
  private Set<Player> votedPlayers = new HashSet<>();
  private Player playerToBeKicked; // player getting voted off
  private int votesFor = 0;
  private int votedAgainst = 0;
  private int neededVotesFor = 0;
  private int neededVotesAgainst = 0;
  private int neededPlayers;
  private Votekick plugin;
  private double votePercentage;
  private int voteTime;
  private int cVoteTime;

  public VoteExecutor(Votekick plugin, Config config) {
    this.plugin = plugin;
    this.config = config;
    neededPlayers = config.getMinPlayers();
    votePercentage = config.getVotePercentage();
    voteTime = config.getVoteTime();
  }

  public boolean getIsVoting() {
    return this.isVoting;
  }

  public Player getPlayerInVoting() {
    return this.playerToBeKicked;
  }

  private void addToVoted(Player p) {
    votedPlayers.add(p);
  }

  public void reloadConfig() {
    config.reload();
    neededPlayers = config.getMinPlayers();
    votePercentage = config.getVotePercentage();
    voteTime = config.getVoteTime();
  }

  public void starVote(Player pStarted, Player pVoted) {
    int playersOnline = plugin.getServer().getOnlinePlayers().size();
    playerToBeKicked = pVoted;
    if (neededPlayers > playersOnline) {
      MessageSender.sendToPlayer(
          pStarted,
          "In order for the vote to start, &a" + neededPlayers + "&f players have to be online.",
          false);
      return;
    }

    neededVotesFor =
        (int) Math.round(plugin.getServer().getOnlinePlayers().size() * votePercentage);
    if (votePercentage == 1) {
      neededVotesFor -= 1;
    }

    neededVotesAgainst = plugin.getServer().getOnlinePlayers().size() - neededVotesFor;

    isVoting = true;
    cVoteTime = voteTime;
    String startMessage =
        "&a"
            + pStarted.getName()
            + "&f has started a vote to kick &a"
            + pVoted.getName()
            + "&f. "
            + neededVotesFor
            + " votes are required for the vote to pass.";
    String instructions = "Use &a/votekick <yes|no>&f to vote.";
    String timeRemaining = voteTime + " seconds remaining.";
    addToVoted(pStarted);
    votesFor++;

    MessageSender.broadcastMessage(plugin.getServer(), startMessage);
    MessageSender.broadcastMessage(plugin.getServer(), instructions);
    MessageSender.broadcastMessage(plugin.getServer(), timeRemaining);

    new BukkitRunnable() {
      int halftime = voteTime / 2;

      @Override
      public void run() {
        if (!isVoting) {
          this.cancel();
        }
        cVoteTime--;
        if (cVoteTime == 0 || votesFor >= neededVotesFor || votedAgainst >= neededVotesAgainst) {
          if (votesFor >= neededVotesFor) {
            MessageSender.broadcastMessage(
                plugin.getServer(), "&a" + pVoted.getName() + "&f has been kicked!");
            Bukkit.getScheduler()
                .runTaskLater(
                    plugin,
                    new Runnable() {
                      public void run() {
                        plugin
                            .getServer()
                            .dispatchCommand(
                                plugin.getServer().getConsoleSender(),
                                String.format(config.getCommand(), pVoted.getName()));
                        System.out.println(String.format(config.getCommand(), pVoted.getName()));
                      }
                    },
                    20L);
          } else {
            MessageSender.broadcastMessage(plugin.getServer(), "The vote did not pass!");
          }
          endVote();
          this.cancel();
        }

        if (cVoteTime == halftime) {
          MessageSender.broadcastMessage(plugin.getServer(), halftime + " seconds remaining.");
        }

        if (halftime > 5 && cVoteTime == 5) {
          MessageSender.broadcastMessage(plugin.getServer(), "5 seconds remaining.");
        }
      }
    }.runTaskTimerAsynchronously(plugin, 20, 20);
  }

  public void castVote(Player pVoter, String vote) {
    if (!votedPlayers.contains(pVoter)) {
      addToVoted(pVoter);
      if (vote.equalsIgnoreCase("yes")) {
        votesFor++;
        MessageSender.sendToConsole(
            plugin.getServer(), pVoter.getName() + " has voted " + vote + ".");
        MessageSender.sendToPlayer(pVoter, "Voted " + vote + ".", false);
      } else {
        votedAgainst++;
      }
    } else {
      MessageSender.sendToPlayer(pVoter, "You have already voted!", false);
    }
  }

  /** Used to end the vote when timer runs out or needed number of votes is reached */
  private void endVote() {
    isVoting = false;
    votesFor = 0;
    votedPlayers = new HashSet<>();
    playerToBeKicked = null;
    cVoteTime = voteTime;
    votedAgainst = 0;
  }

  /** Used to halt and ongoing vote */
  public void stopVote(Player p) {
    MessageSender.broadcastMessage(plugin.getServer(), "The vote has been cancelled.");
    MessageSender.sendToConsole(plugin.getServer(), p.getName() + " has cancelled the vote.");
    endVote();
  }
}
