package xyz.monology.disenchantments;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class Disenchantments extends JavaPlugin {
    private double experienceFactor;
    private boolean ignoreLevelRestriction;

    @Override
    public void onEnable() {
        DisenchantmentsCommand disenchantmentsCommand = new DisenchantmentsCommand(this);
        addCommand("disenchantments", "disenchantments.admin", disenchantmentsCommand, disenchantmentsCommand);

        DisenchantCommand disenchantCommand = new DisenchantCommand(this);
        addCommand("disenchant", "disenchantments.use", disenchantCommand, disenchantCommand);

        saveDefaultConfig();
        reloadConfig();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        experienceFactor = getConfig().getDouble("experienceFactor");
        ignoreLevelRestriction = getConfig().getBoolean("ignoreLevelRestriction");
    }

    public double getExperienceFactor() {
        return experienceFactor;
    }

    public boolean isIgnoreLevelRestriction() {
        return ignoreLevelRestriction;
    }

    private void addCommand(String name, String permission, CommandExecutor commandExecutor, TabCompleter tabCompleter) {
        PluginCommand command = getCommand(name);
        command.setPermission(permission);
        command.setExecutor(commandExecutor);
        command.setTabCompleter(tabCompleter);
    }

    static String info(String string) {
        return color("&9[Disenchantments] &7" + string);
    }

    static String error(String string) {
        return color("&c[Disenchantments] &7" + string);
    }

    static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
