package xyz.monology.disenchantments.mcmmo;

import com.gmail.nossr50.api.AbilityAPI;
import org.bukkit.entity.Player;

public class MCMMOController {
    public static  boolean isUserUsingAbility(Player player) {
        return AbilityAPI.isAnyAbilityEnabled(player);
    }

}
