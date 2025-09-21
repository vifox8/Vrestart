package ru.mortarcraft.vrestart.scheduler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import ru.mortarcraft.vrestart.VrestartPlugin;
import ru.mortarcraft.vrestart.util.Messages;

import java.util.*;

public class SoftRestartTask {
    private final VrestartPlugin plugin;
    private final VoteManager voteManager;

    private BukkitTask scheduledRestart;
    private BukkitTask finalRestartTask;

    public SoftRestartTask(VrestartPlugin plugin) {
        this.plugin = plugin;
        this.voteManager = VoteManager.getInstance(plugin);
    }

    public void startScheduled() {
        int seconds = plugin.getConfig().getInt("defaults.soft-time", 60);
        startNow(null, seconds);
    }

    public void startNow(String reason, int seconds) {
        startVote(seconds > 0 ? seconds : plugin.getConfig().getInt("defaults.soft-time", 60), reason);
    }

    private void startVote(int seconds, String reason) {
        String randomPlayer = getRandomPlayerName();

        Map<String,String> placeholders = new HashMap<>();
        placeholders.put("player", randomPlayer);
        if (reason != null) placeholders.put("reason", reason);

        Component message = Messages.component(plugin.getLocale().getPrefix(),
                "soft_restart", placeholders, plugin.getLocale());

        Component cancelButton = Messages.component(plugin.getLocale().getPrefix(),
                        plugin.getLocale().get("cancel_button"))
                .clickEvent(ClickEvent.runCommand("/cancelrestart"));

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission("vrestart.admin")) {
                p.sendMessage(message.append(Component.newline()).append(cancelButton));
            } else {
                p.sendMessage(message);
            }
        }

        voteManager.startVote(this);

        scheduledRestart = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!voteManager.isVoteCancelled(this)) {
                Component forcedMsg = Messages.component(plugin.getLocale().getPrefix(),
                        plugin.getLocale().get("forced_restart"));
                Bukkit.broadcast(forcedMsg);

                finalRestartTask = Bukkit.getScheduler().runTaskLater(plugin, this::executeRestart, 10 * 20L);
            }
        }, seconds * 20L);
    }

    private String getRandomPlayerName() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (players.isEmpty()) return "Vifoxy";
        return players.get(new Random().nextInt(players.size())).getName();
    }

    public void executeRestart() {
        Bukkit.broadcast(Messages.component(plugin.getLocale().getPrefix(), plugin.getLocale().get("forced_restart")));
        Bukkit.shutdown();
    }

    public void cancelRestart() {
        if (scheduledRestart != null) scheduledRestart.cancel();
        if (finalRestartTask != null) finalRestartTask.cancel();

        Bukkit.broadcast(Messages.component(plugin.getLocale().getPrefix(), plugin.getLocale().get("soft_cancel")));
        voteManager.clearCurrentVote();
    }

    public void cancelByVote() {
        if (scheduledRestart != null) scheduledRestart.cancel();
        if (finalRestartTask != null) finalRestartTask.cancel();

        Bukkit.broadcast(Messages.component(plugin.getLocale().getPrefix(), plugin.getLocale().get("soft_cancel_vote")));
        voteManager.clearCurrentVote();
    }

    public void cancelByAdmin() {
        if (scheduledRestart != null) scheduledRestart.cancel();
        if (finalRestartTask != null) finalRestartTask.cancel();

        Bukkit.broadcast(Messages.component(plugin.getLocale().getPrefix(), plugin.getLocale().get("soft_cancel_admin")));
        voteManager.clearCurrentVote();
    }
}
