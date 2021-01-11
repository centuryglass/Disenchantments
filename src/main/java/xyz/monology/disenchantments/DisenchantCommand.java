package xyz.monology.disenchantments;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.enchantments.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

import java.util.*;
import java.util.regex.*;

public final class DisenchantCommand implements CommandExecutor, TabCompleter {
    private static final Pattern ENCHANTMENT_PATTERN = Pattern.compile("[a-z0-9/._-]+");

    private final Disenchantments plugin;

    public DisenchantCommand(Disenchantments plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(Disenchantments.error("Invalid usage: /disenchant [enchantments... | 'all']"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Disenchantments.error("You must be a player to execute this command."));
            return true;
        }

        Player player = (Player) sender;
        PlayerInventory inventory = player.getInventory();

        if (inventory.firstEmpty() == -1) {
            sender.sendMessage(Disenchantments.error("Your inventory is full."));
            return true;
        }

        ItemStack item = inventory.getItemInMainHand();
        Map<Enchantment, Integer> originalEnchantments = item.getEnchantments();

        String argument = args[0];

        Map<Enchantment, Integer> enchantments;
        if (argument.equals("all")) {
            enchantments = originalEnchantments;
        } else {
            enchantments = new HashMap<>();
            for (String arg : args) {
                if (argument.length() > 256 || !ENCHANTMENT_PATTERN.matcher(arg).matches()) {
                    return enchantmentDoesNotExist(player, arg);
                }

                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(arg));

                if (enchantment == null) {
                    return enchantmentDoesNotExist(player, arg);
                }

                enchantments.put(enchantment, originalEnchantments.get(enchantment));
            }
        }

        Map<Enchantment, Integer> itemStackEnchantments = item.getEnchantments();
        int level = 0;
        for (Enchantment enchantment : enchantments.keySet()) {
            if (!itemStackEnchantments.containsKey(enchantment)) {
                player.sendMessage(Disenchantments.error("Your item does not have the \"" + enchantment.getKey().getKey() + "\" enchantment."));
                return true;
            }

            level += itemStackEnchantments.get(enchantment);
        }

        int experience = (int) (level * plugin.getExperienceFactor());

        if (player.getGameMode() != GameMode.CREATIVE && player.getLevel() < experience) {
            player.sendMessage(Disenchantments.error("You do not have enough experience levels to disenchant your item. You need " + (experience - player.getLevel()) + " more experience level(s)."));
            return true;
        }

        Repairable meta;
        if (item.getItemMeta() instanceof Repairable) {
            meta = (Repairable) item.getItemMeta();
        } else {
            meta = null;
        }

        int num = enchantments.size();
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            if (inventory.firstEmpty() == -1) {
                player.sendMessage(Disenchantments.error("There are no more empty slots in your inventory."));
                if (meta != null) item.setItemMeta((ItemMeta) meta);
                return true;
            }

            if (player.getGameMode() != GameMode.CREATIVE) {
                if (!inventory.contains(Material.BOOK)) {
                    sender.sendMessage(Disenchantments.error("You need " + num + " more book(s) in your inventory to disenchant this item."));
                    if (meta != null) item.setItemMeta((ItemMeta) meta);
                    return true;
                }

                ItemStack books = inventory.getItem(inventory.first(Material.BOOK));
                books.setAmount(books.getAmount() - 1);

                player.setLevel(player.getLevel() - (int) (entry.getValue() * plugin.getExperienceFactor()));
            }

            System.out.println(entry.getKey());
            item.removeEnchantment(entry.getKey());
            inventory.addItem(makeEnchantedBook(entry.getKey(), entry.getValue()));
            if (meta != null) meta.setRepairCost(meta.getRepairCost() - 1);

            num--;
        }

        if (meta != null) item.setItemMeta((ItemMeta) meta);

        player.sendMessage(Disenchantments.info("The item was disenchanted."));

        return true;
    }

    private boolean enchantmentDoesNotExist(Player player, String arg) {
        player.sendMessage(Disenchantments.error("The specified enchantment \"" + arg + "\" does not exist."));
        return true;
    }

    private ItemStack makeEnchantedBook(Enchantment enchantment, int level) {
        ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta enchantmentBookMeta = (EnchantmentStorageMeta) itemStack.getItemMeta();

        enchantmentBookMeta.addStoredEnchant(enchantment, level, false);

        itemStack.setItemMeta(enchantmentBookMeta);

        return itemStack;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && args[0].equals("all")) {
            return Collections.emptyList();
        }

        List<String> argumentList = Arrays.asList(args);

        if (!(sender instanceof Player)) return Collections.emptyList();

        Player player = (Player) sender;

        Set<Enchantment> enchantments = player.getInventory().getItemInMainHand().getEnchantments().keySet();

        List<String> enchantmentNames = new ArrayList<>();

        if (args.length == 1) {
            enchantmentNames.add("all");
        }

        for (Enchantment enchantment : enchantments) {
            if (!argumentList.contains(enchantment.getKey().getKey())) {
                enchantmentNames.add(enchantment.getKey().getKey());
            }
        }

        return enchantmentNames;
    }
}
