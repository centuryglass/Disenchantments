package xyz.monology.disenchantments;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             String[] args) {
        if (args.length == 1 && args[0].equals("reload")) {
            plugin.reloadConfig();
            sender.sendMessage(plugin.getLocaleEntry(10));
            return true;
        }

        sender.sendMessage(plugin.getLocaleEntry(11));

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias,
                                      String[] args) {
        return TAB_COMPLETIONS;
    }
}
