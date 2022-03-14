package peterhenryd.disenchantments;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Disenchantments extends JavaPlugin {
    private double experienceFactor;
    private boolean ignoreLevelRestriction;
    private String[] locale;

    private static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    @Override
    public void onEnable() {
        DisenchantmentsCommand disenchantmentsCommand = new DisenchantmentsCommand(this);
        addCommand("disenchantments", "disenchantments.admin", disenchantmentsCommand,
                disenchantmentsCommand);

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

        locale = new String[]{
                color(getConfig().getString("locale.invalidUsage")),
                color(getConfig().getString("locale.mustBePlayer")),
                color(getConfig().getString("locale.mayNotDisenchantMcmmo")),
                color(getConfig().getString("locale.inventoryFull")),
                color(getConfig().getString("locale.missingEnchantment")),
                color(getConfig().getString("locale.notEnoughExerience")),
                color(getConfig().getString("locale.noEmptySlotsInventory")),
                color(getConfig().getString("locale.notEnoughBooks")),
                color(getConfig().getString("locale.itemDisenchanted")),
                color(getConfig().getString("locale.nonexistantEnchantment")),
                color(getConfig().getString("locale.configReloaded")),
                color(String.join("\n", getConfig().getStringList("locale.helpMessage")))
        };
    }

    public double getExperienceFactor() {
        return experienceFactor;
    }

    public boolean isIgnoreLevelRestriction() {
        return ignoreLevelRestriction;
    }

    public String getLocaleEntry(int i) {
        return locale[i];
    }

    private void addCommand(String name, String permission, CommandExecutor commandExecutor,
                            TabCompleter tabCompleter) {
        PluginCommand command = Objects.requireNonNull(getCommand(name), "The '" + name + "' command should " +
                "exist in the plugin.yml.");
        command.setPermission(permission);
        command.setExecutor(commandExecutor);
        command.setTabCompleter(tabCompleter);
    }
}
