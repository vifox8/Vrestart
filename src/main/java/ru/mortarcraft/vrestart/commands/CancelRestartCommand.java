package ru.mortarcraft.vrestart.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.mortarcraft.vrestart.scheduler.VoteManager;

public class CancelRestartCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        VoteManager voteManager = VoteManager.getInstance();
        if (voteManager != null) {
            voteManager.voteCancel(player);
        }

        return true;
    }
}
