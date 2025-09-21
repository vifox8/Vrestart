package ru.mortarcraft.vrestart.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.mortarcraft.vrestart.VrestartPlugin;

import java.util.Map;

public final class Messages {
    private Messages() {}

    public static Component component(String prefix, String message) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + message);
    }

    public static Component component(String prefix, String key, Map<String, String> placeholders, Locale locale) {
        String msg = locale.get(key, placeholders);
        return component(prefix, msg);
    }

    public static Component component(String prefix, String key, Locale locale) {
        return component(prefix, key, Map.of(), locale);
    }

    public static void componentBroadcast(VrestartPlugin plugin, String key, Map<String, String> placeholders) {
        Component message = component(plugin.getLocale().getPrefix(), key, placeholders, plugin.getLocale());
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    public static void componentBroadcast(VrestartPlugin plugin, String key) {
        componentBroadcast(plugin, key, Map.of());
    }
}
