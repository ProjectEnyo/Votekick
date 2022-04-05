package me.miicro.votekick.commands;

import me.miicro.votekick.Votekick;
import me.miicro.votekick.util.MessageSender;
import me.miicro.votekick.vote.VoteExecutor;
import org.bukkit.ChatColor;
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

        String parameter = args[0].toLowerCase();
        Player p = (Player) sender;
        if (args.length > 1) {
            return false;
        }

        // Check permissions first
        if ((parameter.equals(CommandArgs.RELOAD.value) || parameter.equals(CommandArgs.STOP.value)) && !p.isOp()) {
            MessageSender.sendToPlayer(p, "Insufficient permissions.");
            return true;
        }

        // Vote happening
        if (voteExecutor.getIsVoting()) {
            // Voted yes/no, check if player is not voting for themselves
            if (parameter.equals(CommandArgs.YES.value) || parameter.equals(CommandArgs.NO.value) ) {
                if (p.getName().equals(voteExecutor.getPlayerInVoting())) {
                    MessageSender.sendToPlayer(p, "You may not vote for yourself!");
                    return true;
                }
                //TODO count vote + select player as voted
                MessageSender.sendToConsole(server, p.getDisplayName() + "has voted " + parameter.toLowerCase() + ".");
                MessageSender.sendToPlayer(p, "Voted " + parameter.toLowerCase());
            }
            // handle other args (STOP and RELOAD should work, RELOAD is handled earlier)
            else {
                if (parameter.equals(CommandArgs.STOP.value) && p.isOp()) {
                    //TODO stop vote
                    MessageSender.sendToPlayer(p, "Vote cancelled.");
                    MessageSender.sendToConsole(server, p.getName() + " has cancelled the vote.");
                } else {
                    MessageSender.sendToPlayer(p, "Wait until the vote is finished to execute the command.");
                }
            }
        }
        // No vote going on
        else{
            // Can't run YES/NO/STOP if vote's not going on
            if (parameter.matches(CommandArgs.YES.value+"|"+CommandArgs.NO.value+"|"+CommandArgs.STOP.value)) {
                MessageSender.sendToPlayer(p, "No ongoing vote!");
                return true;
            }
            Player votePlayer = server.getPlayerExact(parameter);

            if (votePlayer == null) {
                MessageSender.sendToPlayer(p, "No such player online!");
                return true;
            } else {
                voteExecutor.starVote(p, votePlayer);
                return true;
            }
        }

        return true;
    }
}
