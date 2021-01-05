package xyz.monology.disenchantments;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import xyz.monology.disenchantments.mcmmo.MCMMOController;

import java.util.*;
import java.util.regex.Pattern;

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

        if (plugin.isMcMMOInstalled()) {
            if (MCMMOController.isUserUsingAbility(player)) {
                sender.sendMessage(Disenchantments.error("No  mcMMO abilities must be active to execute this command"));
                return true;
            }
        }
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
