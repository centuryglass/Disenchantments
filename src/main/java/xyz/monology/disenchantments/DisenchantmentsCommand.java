package xyz.monology.disenchantments;

import org.bukkit.*;
import org.bukkit.command.*;

public class DisenchantmentsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[Disenchantments] &8Version 1.0."));

        return true;
    }
}
