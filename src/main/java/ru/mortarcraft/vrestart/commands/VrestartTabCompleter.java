package ru.mortarcraft.vrestart.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VrestartTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("force", "soft", "cancel", "reload");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("cancel")) {
            return Arrays.asList("force", "soft");
        }
        return Collections.emptyList();
    }
}
