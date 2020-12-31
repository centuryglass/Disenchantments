package xyz.monology.disenchantments;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.enchantments.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.permissions.*;

import java.util.*;
import java.util.regex.*;

public class DisenchantCommand implements CommandExecutor, TabCompleter {
    private static Pattern ENCHANTMENT_PATTERN = Pattern.compile("[a-z0-9/._-]+");

    private final Disenchantments plugin;

    public DisenchantCommand(Disenchantments plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Disenchantments.error("Invalid usage: /disenchant [enchantment]"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Disenchantments.error("You must be a player to execute this command."));
            return true;
        }

        Player player = (Player) sender;
        Inventory inventory = player.getInventory();

        if (inventory.firstEmpty() == -1) {
            sender.sendMessage(Disenchantments.error("Your inventory is full."));
            return true;
        }

        if (!player.hasPermission("disenchantments.use.nobook") && !inventory.contains(Material.BOOK)) {
            sender.sendMessage(Disenchantments.error("You need to a book in your inventory to disenchant an item."));
            return true;
        }

        String argument = args[0];

        if (argument.length() > 256 || !ENCHANTMENT_PATTERN.matcher(argument).matches()) {
            sender.sendMessage(Disenchantments.error("The specified enchantment does not exist."));
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(args[0]));

        if (enchantment == null) {
            sender.sendMessage(Disenchantments.error("The specified enchantment does not exist."));
            return true;
        }

        Map<Enchantment, Integer> enchantments = item.getEnchantments();

        if (!enchantments.containsKey(enchantment)) {
            sender.sendMessage(Disenchantments.error("Your item does not have the specified enchantment."));
            return true;
        }

        int level = enchantments.get(enchantment);
        int experience = level * plugin.getExperienceFactor();

        if (player.getLevel() < experience) {
            sender.sendMessage(Disenchantments.error("You do not have enough experience levels to disenchant your item. You need " + (experience - player.getLevel()) + " more experience level(s)."));
            return true;
        }

        item.removeEnchantment(enchantment);

        player.getInventory().remove(new ItemStack(Material.BOOK));

        ItemStack enchantmentBookItemStack = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta enchantmentBookMeta = (EnchantmentStorageMeta) enchantmentBookItemStack.getItemMeta();

        enchantmentBookMeta.addStoredEnchant(enchantment, level, true);

        enchantmentBookItemStack.setItemMeta(enchantmentBookMeta);

        player.getInventory().addItem(enchantmentBookItemStack);

        player.setLevel(player.getLevel() - experience);

        player.sendMessage(Disenchantments.info("The item was disenchanted, and an enchantment book has been placed in your inventory."));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return Collections.emptyList();

        Player player = (Player) sender;

        Set<Enchantment> enchantments = player.getInventory().getItemInMainHand().getEnchantments().keySet();

        List<String> enchantmentNames = new ArrayList<>();
        for (Enchantment enchantment : enchantments) {
            enchantmentNames.add(enchantment.getKey().getKey());
        }

        return enchantmentNames;
    }
}
