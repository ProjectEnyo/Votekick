package me.miicro.votekick.vote;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VoteExecutor {

    private boolean isVoting = false;
    private Map<UUID, String> votedPlayers = new HashMap<UUID, String>();
    private String playerInVoting; // player getting votted off

    public VoteExecutor() {}

    public boolean getIsVoting() {
        return this.isVoting;
    }

    public String getPlayerInVoting() { return this.playerInVoting; }

    private void addToVoted(UUID id, Player p) {
        votedPlayers.put(id, p.getName());
    }
}
