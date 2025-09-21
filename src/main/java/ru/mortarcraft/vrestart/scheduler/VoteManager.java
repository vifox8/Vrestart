package ru.mortarcraft.vrestart.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.mortarcraft.vrestart.VrestartPlugin;
import ru.mortarcraft.vrestart.util.Messages;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VoteManager {

    private static VoteManager instance;

    private final VrestartPlugin plugin;
    private SoftRestartTask currentTask;
    private final Set<Player> votes = new HashSet<>();

    private VoteManager(VrestartPlugin plugin) {
        this.plugin = plugin;
    }

    public static VoteManager getInstance(VrestartPlugin plugin) {
        if (instance == null) instance = new VoteManager(plugin);
        return instance;
    }

    public static VoteManager getInstance() {
        return instance;
    }

    public void startVote(SoftRestartTask task) {
        currentTask = task;
        votes.clear();
    }

    public void voteCancel(Player player) {
        if (currentTask == null || votes.contains(player)) return;

        votes.add(player);

        int online = Bukkit.getOnlinePlayers().size();
        double percent = plugin.getConfig().getDouble("soft-cancel-percent", 50);
        int required = (int) Math.ceil(online * (percent / 100.0));

        if (votes.size() >= required) {
            currentTask.cancelByVote();
        } else {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("current", String.valueOf(votes.size()));
            placeholders.put("required", String.valueOf(required));

            Messages.componentBroadcast(
                    plugin,
                    "vote_progress",
                    placeholders
            );
        }
    }

    public boolean isVoteCancelled(SoftRestartTask task) {
        return task != currentTask;
    }

    public void clearCurrentVote() {
        currentTask = null;
        votes.clear();
    }
}
