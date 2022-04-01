package me.miicro.votekick.commands;

import me.miicro.votekick.Votekick;
import me.miicro.votekick.vote.VoteExecutor;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class Commands implements CommandExecutor {

    private Votekick votekick;
    private VoteExecutor voteExecutor;

    public Commands(Votekick votekick, VoteExecutor voteExecutor) {
        this.votekick = votekick;
        this.voteExecutor = voteExecutor;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;
        // Can't start a new vote during an ongoing vote
        if (voteExecutor.getIsVoting()) {

        } else{
            if (args.length > 1) {
            }
        }

        return true;
    }
}
