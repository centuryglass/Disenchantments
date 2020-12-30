package xyz.monology.disenchantments;

import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class Disenchantments extends JavaPlugin {
    @Override
    public void onEnable() {
        {
            PluginCommand command = getCommand("disenchantments");
            command.setPermission("disenchantments.admin");
            command.setExecutor(new DisenchantmentsCommand());
        }

        {
            PluginCommand command = getCommand("disenchant");
            command.setPermission("disenchantments.use");
            command.setExecutor(new DisenchantCommand());
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
