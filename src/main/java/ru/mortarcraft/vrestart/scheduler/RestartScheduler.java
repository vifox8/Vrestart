package ru.mortarcraft.vrestart.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import ru.mortarcraft.vrestart.VrestartPlugin;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RestartScheduler {
    private final VrestartPlugin plugin;
    private ForcedRestartTask forcedTask;
    private SoftRestartTask softTask;

    private String lastForced = "";
    private String lastSoft = "";

    private BukkitTask scheduleTask;

    public RestartScheduler(VrestartPlugin plugin) {
        this.plugin = plugin;
        this.forcedTask = new ForcedRestartTask(plugin);
        this.softTask = new SoftRestartTask(plugin);
    }

    public void start() {
        scheduleTask = Bukkit.getScheduler().runTaskTimer(plugin, this::checkSchedule, 20L, 20L);
    }

    public void stop() {
        if (scheduleTask != null) scheduleTask.cancel();
        forcedTask.cancel();
        softTask.cancelByAdmin();
    }

    public void reload() {
        if (scheduleTask != null) scheduleTask.cancel();

        forcedTask = new ForcedRestartTask(plugin);
        softTask = new SoftRestartTask(plugin);

        start();
    }

    private void checkSchedule() {
        LocalTime now = LocalTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        String current = now.format(fmt);

        List<String> forced = plugin.getConfig().getStringList("forced-restarts");
        if (forced.contains(current) && !current.equals(lastForced)) {
            forcedTask.startScheduled();
            lastForced = current;
        }

        List<String> soft = plugin.getConfig().getStringList("soft-restarts");
        if (soft.contains(current) && !current.equals(lastSoft)) {
            softTask.startScheduled();
            lastSoft = current;
        }
    }

    public void startForcedNow(String reason, int seconds) {
        forcedTask.startNow(reason, seconds);
    }

    public void startSoftNow(String reason, int seconds) {
        softTask.startNow(reason, seconds);
    }

    public void cancelForced() { forcedTask.cancel(); }

    public void cancelSoft() { softTask.cancelByAdmin(); }
}
