package ru.mortarcraft.vrestart.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import ru.mortarcraft.vrestart.VrestartPlugin;
import ru.mortarcraft.vrestart.util.Messages;

import java.util.HashMap;
import java.util.Map;

public class ForcedRestartTask {
    private final VrestartPlugin plugin;
    private BukkitTask countdownTask;

    public ForcedRestartTask(VrestartPlugin plugin) { this.plugin = plugin; }

    public void startScheduled() {
        startCountdown(plugin.getConfig().getInt("defaults.force-time", 60), null, true);
    }

    public void startNow(String reason, int seconds) {
        startCountdown(seconds > 0 ? seconds : plugin.getConfig().getInt("defaults.force-time", 60), reason, false);
    }

    private void startCountdown(int seconds, String reason, boolean scheduled) {
        if (scheduled) {
            Bukkit.broadcast(Messages.component(plugin.getLocale().getPrefix(), "forced_warning", plugin.getLocale()));
        }

        if (reason != null) {
            Map<String,String> placeholders = new HashMap<>();
            placeholders.put("reason", reason);
            Bukkit.broadcast(Messages.component(plugin.getLocale().getPrefix(), "reason_format", placeholders, plugin.getLocale()));
        }

        final int[] left = {seconds};
        countdownTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (left[0] <= 0) {
                Bukkit.broadcast(Messages.component(plugin.getLocale().getPrefix(), plugin.getLocale().get("forced_restart")));
                countdownTask.cancel();
                Bukkit.shutdown();
                return;
            }

            if (left[0] % 10 == 0 || left[0] <= 10) {
                Map<String,String> placeholders = new HashMap<>();
                placeholders.put("seconds", String.valueOf(left[0]));
                Bukkit.broadcast(Messages.component(plugin.getLocale().getPrefix(), "forced_countdown", placeholders, plugin.getLocale()));
            }
            left[0]--;
        }, 0L, 20L);
    }

    public void cancel() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
            Bukkit.broadcast(Messages.component(plugin.getLocale().getPrefix(), plugin.getLocale().get("force_cancel")));
        } else {
            Bukkit.getLogger().info("[VRESTART] No active forced restart to cancel.");
        }
    }
}
