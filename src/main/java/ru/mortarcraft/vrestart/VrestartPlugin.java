package ru.mortarcraft.vrestart;

import org.bukkit.plugin.java.JavaPlugin;
import ru.mortarcraft.vrestart.commands.CancelRestartCommand;
import ru.mortarcraft.vrestart.commands.VrestartCommand;
import ru.mortarcraft.vrestart.commands.VrestartTabCompleter;
import ru.mortarcraft.vrestart.scheduler.RestartScheduler;
import ru.mortarcraft.vrestart.scheduler.VoteManager;
import ru.mortarcraft.vrestart.util.Locale;

public class VrestartPlugin extends JavaPlugin {
    private RestartScheduler scheduler;
    private Locale locale;

    private void saveResourceIfNotExists(String resourcePath) {
        java.io.File file = new java.io.File(getDataFolder(), resourcePath);
        if (!file.exists()) {
            saveResource(resourcePath, false);
        }
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("lang/en.yml", false);
        saveResource("lang/ru.yml", false);

        String lang = getConfig().getString("language", "en");
        locale = new Locale(this, lang);

        VoteManager.getInstance(this);

        scheduler = new RestartScheduler(this);
        scheduler.start();

        getCommand("vrestart").setExecutor(new VrestartCommand(this));
        getCommand("vrestart").setTabCompleter(new VrestartTabCompleter());
        getCommand("cancelrestart").setExecutor(new CancelRestartCommand());
    }

    @Override
    public void onDisable() {
        if (scheduler != null) scheduler.stop();
    }

    public RestartScheduler getScheduler() {
        return scheduler;
    }

    public Locale getLocale() {
        return locale;
    }

    public void reloadPluginConfig() {
        reloadConfig();

        String lang = getConfig().getString("language", "en");
        locale = new Locale(this, lang);

        if (scheduler != null) scheduler.reload();

        getLogger().info("Vrestart config and language reloaded. Current language: " + lang);
    }
}
