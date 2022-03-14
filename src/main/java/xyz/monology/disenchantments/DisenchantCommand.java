package xyz.monology.disenchantments;

import com.gmail.nossr50.api.AbilityAPI;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;

public final class DisenchantCommand implements CommandExecutor, TabCompleter {
    private static final Pattern ENCHANTMENT_PATTERN = Pattern.compile("[a-z0-9/._-]+");

    private final Disenchantments plugin;

    public DisenchantCommand(Disenchantments plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
                             String[] args) {
        if (args.length < 1) {
            sender.sendMessage(plugin.getLocaleEntry(0));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getLocaleEntry(1));
            return true;
        }

        Player player = (Player) sender;
        PlayerInventory inventory = player.getInventory();

        if (plugin.getServer().getPluginManager().isPluginEnabled("mcMMO") &&
                AbilityAPI.isAnyAbilityEnabled(player)) {
            player.sendMessage(plugin.getLocaleEntry(2));
            return true;
        }

        if (inventory.firstEmpty() == -1) {
            sender.sendMessage(plugin.getLocaleEntry(3));
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
                player.sendMessage(plugin.getLocaleEntry(4).replace("{0}", enchantment.getKey().getKey()));
                return true;
            }

            level += itemStackEnchantments.get(enchantment);
        }

        int experience = (int) (level * plugin.getExperienceFactor());

        if (player.getGameMode() != GameMode.CREATIVE && player.getLevel() < experience) {
            player.sendMessage(plugin.getLocaleEntry(5).replace("{0}",
                    Integer.toString(experience - player.getLevel())));
            return true;
        }

        Repairable repairableMeta;
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Repairable) {
            repairableMeta = (Repairable) meta;
        } else {
            repairableMeta = null;
        }

        int num = enchantments.size();
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            if (inventory.firstEmpty() == -1) {
                player.sendMessage(plugin.getLocaleEntry(6));
                if (meta != null) item.setItemMeta(meta);
                return true;
            }

            if (player.getGameMode() != GameMode.CREATIVE) {
                if (!inventory.contains(Material.BOOK)) {
                    sender.sendMessage(plugin.getLocaleEntry(7).replace("{0}", Integer.toString(num)));
                    if (meta != null) item.setItemMeta(meta);
                    return true;
                }

                ItemStack books = Objects.requireNonNull(inventory.getItem(inventory.first(Material.BOOK)));
                books.setAmount(books.getAmount() - 1);

                player.setLevel(player.getLevel() - (int) (entry.getValue() * plugin.getExperienceFactor()));
            }

            System.out.println(entry.getKey());
            if (meta != null) meta.removeEnchant(entry.getKey());
            inventory.addItem(makeEnchantedBook(entry.getKey(), entry.getValue()));
            if (repairableMeta != null) repairableMeta.setRepairCost((repairableMeta.getRepairCost() - 1) / 2);

            num--;
        }

        item.setItemMeta(meta);

        player.sendMessage(plugin.getLocaleEntry(8));

        return true;
    }

    private boolean enchantmentDoesNotExist(Player player, String arg) {
        player.sendMessage(plugin.getLocaleEntry(9).replace("{0}", arg));
        return true;
    }

    private ItemStack makeEnchantedBook(Enchantment enchantment, int level) {
        ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta enchantmentBookMeta = (EnchantmentStorageMeta)
                Objects.requireNonNull(itemStack.getItemMeta());

        enchantmentBookMeta.addStoredEnchant(enchantment, level, plugin.isIgnoreLevelRestriction());

        itemStack.setItemMeta(enchantmentBookMeta);

        return itemStack;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias,
                                      String[] args) {
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
