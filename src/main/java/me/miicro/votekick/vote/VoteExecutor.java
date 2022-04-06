package me.miicro.votekick.vote;

import me.miicro.votekick.Votekick;
import me.miicro.votekick.util.MessageSender;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class VoteExecutor {

    private boolean isVoting = false;
    private Map<UUID, String> votedPlayers = new HashMap<UUID, String>();
    private Player playerToBeKicked; // player getting votted off
    private int votesFor = 0;
    private int neededVotes = 5;
    private Votekick plugin;
    private double votePercentage = 0.5; // TODO read from config
    private int voteTime = 30; //TODO get from config
    private int cVoteTime;

    public VoteExecutor(Votekick plugin) {
        this.plugin = plugin;
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

    private void readConfig(){

    }

    public void starVote(Player pStarted, Player pVoted) {
        isVoting = true;
        neededVotes = (int)(plugin.getServer().getOnlinePlayers().size() / votePercentage);
        cVoteTime = voteTime;
        String startMessage = pStarted.getDisplayName() + " has started a vote to kick " + pVoted.getDisplayName()
                + ". " + neededVotes + " are required.\n" + "Use &2/votekick <yes/no>&f to vote";
        String timeRemaining = "Time reamining: ";
        addToVoted(pStarted);

        new BukkitRunnable() {
            int halftime = voteTime/2;
            @Override
            public void run() {
                cVoteTime--;
                if (cVoteTime == 0 || neededVotes >= votesFor) {
                     if (neededVotes >= votesFor) {
                         MessageSender.broadcastMessage(plugin.getServer(), pVoted.getDisplayName() + " has been kicked out!");
                         pVoted.kickPlayer("You have been kicked from the server!");
                     } else {
                        MessageSender.broadcastMessage(plugin.getServer(), "The kick vote did not pass!");
                     }
                     endVote();
                }

                if (cVoteTime == halftime) {
                    MessageSender.broadcastMessage(plugin.getServer(), halftime + " seconds remaining.");
                }

                if (halftime > 5 && cVoteTime == 5) {
                    MessageSender.broadcastMessage(plugin.getServer(), "5 seconds remaining.");
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20L, 20L);
        //TODO print how many votes needed
        // print who is being voted out by who
        // print time remaining 60s
        // print time remaining 30s
        // print time remaining 5s
        // end vote if required votes reached

    }

    public void castVote (Player pVoter, String vote) {
        if (!votedPlayers.containsKey(pVoter.getUniqueId())){
            addToVoted(pVoter);
            if (vote.toLowerCase().equals("yes")) {
                votesFor++;
            }
        } else if (playerToBeKicked.getUniqueId().equals(pVoter.getUniqueId())) {
            MessageSender.sendToPlayer(pVoter, "You cannot vote for yourself.");
        }else{
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
        MessageSender.sendToConsole(plugin.getServer(), p.getDisplayName() + " has cancelled the kick vote.");
        endVote();
    }
}
