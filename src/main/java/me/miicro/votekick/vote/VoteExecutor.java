package me.miicro.votekick.vote;

import me.miicro.votekick.Votekick;
import me.miicro.votekick.util.Config;
import me.miicro.votekick.util.MessageSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class VoteExecutor {

    private Config config;
    private boolean isVoting = false;
    private Map<UUID, String> votedPlayers = new HashMap<UUID, String>();
    private Player playerToBeKicked; // player getting votted off
    private int votesFor = 0;
    private int neededVotes;
    private int neededPlayers;
    private Votekick plugin;
    private double votePercentage;
    private int voteTime;
    private int cVoteTime;

    public VoteExecutor(Votekick plugin, Config config) {
        this.plugin = plugin;
        this.config = config;
        neededPlayers = config.getMinPlayers();
        votePercentage = (config.getVotePercentage() > 1 ? config.getVotePercentage()/100 : config.getVotePercentage());
        neededVotes = (int)(plugin.getServer().getOnlinePlayers().size() / votePercentage);
        voteTime = config.getVoteTime();
    }

    public void setVotePercentage (double percentage) {
        this.votePercentage = percentage;
    }

    public boolean getIsVoting() {
        return this.isVoting;
    }

    public Player getPlayerInVoting() { return this.playerToBeKicked; }

    private void addToVoted(Player p) {
        votedPlayers.put(p.getUniqueId(), p.getName());
    }

    public void reloadConfig() {
        config.reload();
    }

    public void starVote(Player pStarted, Player pVoted) {
        int playersOnline = plugin.getServer().getOnlinePlayers().size();
        if (neededPlayers > playersOnline) {
            MessageSender.sendToPlayer(pStarted, "In order for the vote to start, " + neededPlayers + " players have to be online.");
            return;
        }
        isVoting = true;
        cVoteTime = voteTime;
        String startMessage = pStarted.getName() + " has started a vote to kick " + pVoted.getName()
                + ". " + neededVotes + " are required.\n" + "Use &2/votekick <yes/no>&f to vote";
        String timeRemaining = "Time remaining: " + voteTime + " seconds.";
        addToVoted(pStarted);
        addToVoted(pStarted);
        votesFor++;

        MessageSender.broadcastMessage(plugin.getServer(), startMessage);
        MessageSender.broadcastMessage(plugin.getServer(), timeRemaining);
        new BukkitRunnable() {
            int halftime = voteTime/2;
            @Override
            public void run() {
                cVoteTime--;
                if (cVoteTime == 0 || votesFor >= neededVotes) {
                     if (votesFor >= neededVotes) {
                         MessageSender.broadcastMessage(plugin.getServer(), pVoted.getName() + " has been kicked out!");
                         Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                             public void run() {
                                 pVoted.kickPlayer(config.getKickMessage());
                             }
                         }, 20L);
                     } else {
                        MessageSender.broadcastMessage(plugin.getServer(), "The kick vote did not pass!");
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

    public void castVote (Player pVoter, String vote) {
        if (!votedPlayers.containsKey(pVoter.getUniqueId())){
            addToVoted(pVoter);
            if (vote.toLowerCase().equals("yes")) {
                votesFor++;
            }
        } else{
            MessageSender.sendToPlayer(pVoter, "You have already voted!");
        }
    }

    /**
     * Used to end the vote when timer runs out or needed
     * number of votes is reached
     */
    private void endVote() {
        isVoting = false;
        votesFor = 0;
        votedPlayers = new HashMap<UUID, String>();
        playerToBeKicked = null;
        cVoteTime = voteTime;
    }

    /**
     * Used to halt and ongoing vote
     *
     */
    public void stopVote(Player p) {
        MessageSender.broadcastMessage(plugin.getServer(), "The vote has been cancelled.");
        MessageSender.sendToConsole(plugin.getServer(), p.getName() + " has cancelled the kick vote.");
        endVote();
    }
}
