package ru.mortarcraft.vrestart.util;

import org.bukkit.configuration.file.YamlConfiguration;
import ru.mortarcraft.vrestart.VrestartPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Locale {
    private final VrestartPlugin plugin;
    private final String language;
    private final File file;
    private YamlConfiguration config;
    private String prefix;

    public Locale(VrestartPlugin plugin, String language) {
        this.plugin = plugin;
        this.language = language;

        // Файл локализации: plugins/Vrestart/lang/en.yml
        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists()) langDir.mkdirs();

        this.file = new File(langDir, language + ".yml");

        // Если файла нет — берём из ресурсов плагина
        if (!file.exists()) {
            plugin.saveResource("lang/" + language + ".yml", false);
        }

        reload();
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);
        this.prefix = config.getString("prefix", "[VRESTART] ");
    }

    public String get(String key) {
        return config.getString(key, key);
    }

    public String get(String key, Map<String, String> placeholders) {
        String value = get(key);
        if (placeholders != null) {
            for (Map.Entry<String, String> e : placeholders.entrySet()) {
                value = value.replace("%" + e.getKey() + "%", e.getValue());
            }
        }
        return value;
    }

    public String getPrefix() {
        return prefix;
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLanguage() {
        return language;
    }
}
