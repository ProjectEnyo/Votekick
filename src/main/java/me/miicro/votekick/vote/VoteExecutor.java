package me.miicro.votekick.vote;

import java.util.*;
import me.miicro.votekick.Votekick;
import me.miicro.votekick.util.Config;
import me.miicro.votekick.util.MessageSender;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class VoteExecutor {

  private Config config;
  private boolean isVoting = false;
  private Set<Player> votedPlayers = new HashSet<>();
  private Player playerToBeKicked;
  private int votesFor = 0;
  private int votesAgainst = 0;
  private int neededVotesFor = 0;
  private int neededVotesAgainst = 0;
  private int neededPlayers;
  private Votekick plugin;
  private double votePercentage;
  private int voteTime;

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

    if (neededPlayers > playersOnline) {
      MessageSender.sendToPlayer(
          pStarted,
          "In order for the vote to start, &a" + neededPlayers + "&f players have to be online.",
          false);
      return;
    }

    neededVotesFor = (int) Math.round(playersOnline * votePercentage);
    if (votePercentage == 1) {
      neededVotesFor -= 1;
    }
    neededVotesAgainst = playersOnline - neededVotesFor;

    isVoting = true;
    playerToBeKicked = pVoted;
    addToVoted(pStarted);
    votesFor++;
    votesAgainst++;

    if (config.getPlaySound()) {
      plugin
          .getServer()
          .getOnlinePlayers()
          .forEach(p -> p.playSound(p.getLocation(), Sound.CLICK, 0.3f, 0.6f));
    }
    printVoteStartMessages(pStarted.getName(), pVoted.getName());

    new BukkitRunnable() {
      int halftime = voteTime / 2;
      int cVoteTime = voteTime;

      @Override
      public void run() {
        if (!isVoting) {
          this.cancel();
        }
        cVoteTime--;
        if (cVoteTime == 0 || votesFor >= neededVotesFor || votesAgainst > neededVotesAgainst) {
          if (votesFor >= neededVotesFor) {
            MessageSender.broadcastMessage(
                plugin.getServer(),
                "&a"
                    + pVoted.getName()
                    + "&f has been kicked! &a\u2714&f"
                    + votesFor
                    + " | &c\u2715&f"
                    + votesAgainst);
            Bukkit.getScheduler()
                .runTaskLater(
                    plugin,
                    new Runnable() {
                      public void run() {
                        if (config.getLightning())
                          pVoted.getWorld().strikeLightning(pVoted.getLocation());
                        if (config.getCommand().length() == 0) {
                          pVoted.kickPlayer("You have been vote kicked off the server!");
                        } else {
                          plugin
                              .getServer()
                              .dispatchCommand(
                                  plugin.getServer().getConsoleSender(),
                                  String.format(config.getCommand(), pVoted.getName()));
                        }
                      }
                    },
                    20L);
          } else {
            MessageSender.broadcastMessage(
                plugin.getServer(),
                "The vote did not pass! &a\u2714&f" + votesFor + " | &c\u2715 &f" + votesAgainst);
          }
          endVote();
          this.cancel();
        } else {
          if (cVoteTime == halftime || (cVoteTime == 5 && halftime > 5)) {
            MessageSender.broadcastMessage(
                plugin.getServer(),
                cVoteTime
                    + " seconds remaining. &a\u2714&f"
                    + votesFor
                    + " | &c\u2715&f"
                    + votesAgainst);
          }
        }
      }
    }.runTaskTimerAsynchronously(plugin, 20, 20);
  }

  private void printVoteStartMessages(String started, String voted) {
    String startMessage =
        "&a"
            + started
            + "&f has started a vote to kick &a"
            + voted
            + "&f. "
            + neededVotesFor
            + " votes are required for the vote to pass.";
    String instructions = "Use &a/votekick <yes|no>&f to vote.";
    String timeRemaining =
        voteTime + " seconds remaining. &a\u2714&f" + votesFor + " | &c\u2715&f" + votesAgainst;

    MessageSender.broadcastMessage(plugin.getServer(), startMessage);
    MessageSender.broadcastMessage(plugin.getServer(), instructions);
    MessageSender.broadcastMessage(plugin.getServer(), timeRemaining);
  }

  public void castVote(Player pVoter, String vote) {
    if (!votedPlayers.contains(pVoter)) {
      addToVoted(pVoter);
      if (vote.equalsIgnoreCase("yes")) {
        votesFor++;
      } else {
        votesAgainst++;
      }
      MessageSender.sendToConsole(
          plugin.getServer(), pVoter.getName() + " has voted " + vote + ".");
      MessageSender.sendToPlayer(pVoter, "Voted " + vote + ".", false);
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
    votesAgainst = 0;
  }

  /** Used to halt and ongoing vote */
  public void stopVote(Player p) {
    MessageSender.broadcastMessage(plugin.getServer(), "The vote has been cancelled.");
    MessageSender.sendToConsole(plugin.getServer(), p.getName() + " has cancelled the vote.");
    endVote();
  }
}
