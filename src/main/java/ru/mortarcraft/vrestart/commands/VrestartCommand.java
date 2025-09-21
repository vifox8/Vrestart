package ru.mortarcraft.vrestart.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import ru.mortarcraft.vrestart.VrestartPlugin;
import ru.mortarcraft.vrestart.scheduler.RestartScheduler;
import ru.mortarcraft.vrestart.util.Messages;

import java.util.Map;

public class VrestartCommand implements CommandExecutor {
    private final VrestartPlugin plugin;
    private final RestartScheduler scheduler;

    public VrestartCommand(VrestartPlugin plugin) {
        this.plugin = plugin;
        this.scheduler = plugin.getScheduler();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("vrestart.use")) {
            sender.sendMessage(Messages.component(plugin.getLocale().getPrefix(), plugin.getLocale().get("no_permission")));
            return true;
        }

        if (args.length == 0) {
            showSchedule(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "force" -> handleForce(sender, args);
            case "soft" -> handleSoft(sender, args);
            case "cancel" -> handleCancel(sender, args);
            case "reload" -> handleReload(sender);
            default -> showSchedule(sender);
        }
        return true;
    }

    private void handleForce(CommandSender sender, String[] args) {
        if (!sender.hasPermission("vrestart.admin")) {
            sender.sendMessage(Messages.component(plugin.getLocale().getPrefix(),
                    plugin.getLocale().get("no_permission")));
            return;
        }

        int time = plugin.getConfig().getInt("defaults.force-time", 60);
        String reason = null;

        if (args.length > 1) {
            try {
                time = Integer.parseInt(args[1]);
                if (args.length > 2) reason = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length));
            } catch (NumberFormatException e) {
                reason = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
            }
        }

        scheduler.startForcedNow(reason, time);

        sender.sendMessage(Messages.component(
                plugin.getLocale().getPrefix(),
                "force_started",
                Map.of("time", String.valueOf(time)),
                plugin.getLocale()
        ));
    }

    private void handleSoft(CommandSender sender, String[] args) {
        if (!sender.hasPermission("vrestart.admin")) {
            sender.sendMessage(Messages.component(plugin.getLocale().getPrefix(),
                    plugin.getLocale().get("no_permission")));
            return;
        }

        int time = plugin.getConfig().getInt("defaults.soft-time", 60);
        String reason = null;

        if (args.length > 1) {
            try {
                time = Integer.parseInt(args[1]);
                if (args.length > 2) reason = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length));
            } catch (NumberFormatException e) {
                reason = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
            }
        }

        scheduler.startSoftNow(reason, time);

        sender.sendMessage(Messages.component(
                plugin.getLocale().getPrefix(),
                "soft_started",
                Map.of("time", String.valueOf(time)),
                plugin.getLocale()
        ));
    }

    private void handleCancel(CommandSender sender, String[] args) {
        if (!sender.hasPermission("vrestart.admin")) {
            sender.sendMessage(Messages.component(plugin.getLocale().getPrefix(), plugin.getLocale().get("no_permission")));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(Messages.component(plugin.getLocale().getPrefix(), "Usage: /vrestart cancel <force|soft>"));
            return;
        }

        switch (args[1].toLowerCase()) {
            case "force" -> scheduler.cancelForced();
            case "soft" -> scheduler.cancelSoft();
            default -> sender.sendMessage(Messages.component(plugin.getLocale().getPrefix(), "Usage: /vrestart cancel <force|soft>"));
        }
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("vrestart.admin")) {
            sender.sendMessage(Messages.component(plugin.getLocale().getPrefix(), plugin.getLocale().get("no_permission")));
            return;
        }

        plugin.reloadPluginConfig();
        sender.sendMessage(Messages.component(plugin.getLocale().getPrefix(), plugin.getLocale().get("config_reloaded")));
    }

    private void showSchedule(CommandSender sender) {
        sender.sendMessage(Messages.component(plugin.getLocale().getPrefix(), plugin.getLocale().get("schedule_header")));
        sender.sendMessage(Messages.component(plugin.getLocale().getPrefix(),
                plugin.getLocale().get("schedule_forced",
                        Map.of("times", String.join(", ", plugin.getConfig().getStringList("forced-restarts"))))));
        sender.sendMessage(Messages.component(plugin.getLocale().getPrefix(),
                plugin.getLocale().get("schedule_soft",
                        Map.of("times", String.join(", ", plugin.getConfig().getStringList("soft-restarts"))))));
    }
}
