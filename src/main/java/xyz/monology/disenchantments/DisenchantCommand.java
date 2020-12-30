package xyz.monology.disenchantments;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.enchantments.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.Damageable;

import java.util.*;

public class DisenchantCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid usage: /disenchant [enchantment]"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou must be a player to execute this command."));
            return true;
        }

        Player player = (Player) sender;

        if (player.getInventory().firstEmpty() == -1) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour inventory is full."));
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(args[0]));

        if (enchantment == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe specified enchantment does not exist."));
            return true;
        }

        Map<Enchantment, Integer> enchantments = item.getEnchantments();

        if (!enchantments.containsKey(enchantment)) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour tool does not have the specified enchantment."));
            return true;
        }

        int level = enchantments.get(enchantment);

        item.removeEnchantment(enchantment);

        ItemMeta meta = item.getItemMeta();

        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            damageable.setDamage(damageable.getDamage() - level * 50);
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYour tool is not damageable, and therefore may not have its enchantments stripped."));
            return true;
        }

        item.setItemMeta(meta);

        ItemStack enchantmentBookItemStack = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta enchantmentBookMeta = enchantmentBookItemStack.getItemMeta();

        enchantmentBookMeta.addEnchant(enchantment, level, true);

        enchantmentBookItemStack.setItemMeta(enchantmentBookMeta);

        player.getInventory().addItem(enchantmentBookItemStack);

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a"));

        return true;
    }
}
