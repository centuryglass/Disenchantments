package xyz.monology.disenchantments;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Disenchantments extends JavaPlugin implements Listener {
    private int experienceFactor;
    public boolean mcMMOInstalled;

    @Override
    public void onEnable() {
        DisenchantmentsCommand disenchantmentsCommand = new DisenchantmentsCommand(this);
        addCommand("disenchantments", "disenchantments.admin", disenchantmentsCommand, disenchantmentsCommand);

        DisenchantCommand disenchantCommand = new DisenchantCommand(this);
        addCommand("disenchant", "disenchantments.use", disenchantCommand, disenchantCommand);

        saveDefaultConfig();
        reloadConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
        if (Bukkit.getPluginManager().isPluginEnabled("mcMMO")) {
            mcMMOInstalled = true;
        }
    }


    @Override
    public void reloadConfig() {
        super.reloadConfig();
        experienceFactor = getConfig().getInt("experienceFactor");
    }

    public int getExperienceFactor() {
        return experienceFactor;
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent e) {
        if (e.getPlugin().getName().equals("mcMMO")) {
            mcMMOInstalled = true;
        }
    }

    public boolean isMcMMOInstalled() {
        return mcMMOInstalled;
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
