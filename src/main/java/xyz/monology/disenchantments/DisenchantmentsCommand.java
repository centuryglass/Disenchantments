package xyz.monology.disenchantments;

import org.bukkit.command.*;

import java.util.*;

public final class DisenchantmentsCommand implements CommandExecutor, TabCompleter {
    private static final List<String> TAB_COMPLETIONS = new ArrayList<>();

    static {
        TAB_COMPLETIONS.add("reload");
    }

    private final Disenchantments plugin;

    public DisenchantmentsCommand(Disenchantments plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equals("reload")) {
            plugin.reloadConfig();
            sender.sendMessage(Disenchantments.info("The configuration has been reloaded."));
            return true;
        }

        sender.sendMessage(Disenchantments.info("Disenchantments version 1.0"));
        sender.sendMessage(Disenchantments.info("&f/disenchantments reload &7- Reloads the plugin configuration."));
        sender.sendMessage(Disenchantments.info("&f/disenchant [enchantment] &7- Strips an enchantment from an item."));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return TAB_COMPLETIONS;
    }
}
