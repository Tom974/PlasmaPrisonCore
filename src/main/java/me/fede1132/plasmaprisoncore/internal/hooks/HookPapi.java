package me.fede1132.plasmaprisoncore.internal.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.fede1132.plasmaprisoncore.enchant.Enchant;
import me.fede1132.plasmaprisoncore.addons.basics.AddonBasics;
import me.fede1132.plasmaprisoncore.enchant.EnchantManager;
import me.fede1132.plasmaprisoncore.internal.util.NumberFormat;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class HookPapi extends PlaceholderExpansion {
    private static HookPapi instance;
    // public List<PapiPlaceholder> placeholders = new ArrayList<>();
    private final AddonBasics basics = AddonBasics.getInstance();
    private final EnchantManager manager = EnchantManager.getInst();
    public HookPapi() {
        instance = this;
    }

    @Override
    public String getIdentifier() {
        return "plasmaprison";
    }

    @Override
    public String getAuthor() {
        return "Tom974, Fede1132";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (params.length() == 0) return "";
        params = params.toLowerCase();
        // player.sendMessage(params + " debug: " + params.toLowerCase().contains("cost_amount_"));
        // Bukkit.getConsoleSender().sendMessage("args: " + Arrays.stream(args).collect(Collectors.joining(",")));
        AddonBasics basics = this.basics == null ? AddonBasics.getInstance() : this.basics;
        if (params.equalsIgnoreCase("tokens") || params.equalsIgnoreCase("token")) {
            return basics.getTokens(player.getUniqueId(), false) + "";
        } else if (params.equalsIgnoreCase("tokens_formatted") || params.equalsIgnoreCase("formattedtokens")) {
            return NumberFormat.format(basics.getTokens(player.getUniqueId(), false));
        } else if (params.equalsIgnoreCase("tokenmultiplier")) {
            LuckPerms api = LuckPermsProvider.get();
            User user = api.getPlayerAdapter(Player.class).getUser(player);
            float highest = (float) 1.0;
            float highestPerm = (float) 1.0;
            for (Node node : user.getNodes()) {
                if (node.getKey().startsWith("plasmaprison.tokens.multiplier") && !node.getKey().contains("perm")) {
                    float val = Float.valueOf(node.getKey().split("plasmaprison.tokens.multiplier.")[1]);
                    if (val > highest) highest = val;
                }

                if (node.getKey().startsWith("plasmaprison.tokens.multiplier.perm")) {
                    float valPerm = Float.valueOf(node.getKey().split("plasmaprison.tokens.multiplier.perm.")[1]);
                    if (valPerm > highestPerm) highestPerm = valPerm;
                }
            }

            highest = highest + highestPerm;
            return String.valueOf(highest);
        } else if (params.contains("_level")) {
            // get current level of specified enchant
            String enchant = params.replace("_level", "");
            if (player.getInventory().getItemInMainHand() == null) return "Please hold your pickaxe in your main hand!";
            return this.manager.getEnchantLevel(player.getInventory().getItemInMainHand(), enchant) + "";
        } else if (params.toLowerCase().contains("cost_")) {
            // get current level of specified enchant
            String enchant_name = params.split("_cost_")[0];
            int amount = Integer.parseInt(params.split("_cost_")[1]);
            if (player.getInventory().getItemInMainHand() == null) return "Please hold your pickaxe in your main hand!";
            Enchant enchant = this.manager.registeredEnchants.get(enchant_name);
            // return (ench.cost * amount) + "";
            if (enchant == null) return "Enchant not found!";
            if (amount>enchant.max) amount = enchant.max;
            // remove tokens
            ItemStack hand = player.getInventory().getItemInMainHand();
            // What level is player currently at?
            int currentlevel = this.manager.getEnchantLevel(hand, enchant_name);
            
            int to = (amount + currentlevel);
            if (to >= enchant.max) to = enchant.max - 1;
            // player.sendMessage("currentlevel: " + currentlevel);
            // player.sendMessage("max: " + enchant.max);
            // player.sendMessage("amount: " + amount);
            // player.sendMessage("to: " + to);
            long cost = enchant.calcCost(currentlevel, to);
            return cost + "";
        } else if (params.contains("_cost")) {
            // get current level of specified enchant
            String enchant = params.replace("_cost", "");
            if (player.getInventory().getItemInMainHand() == null) return "Please hold your pickaxe in your main hand!";
            int level = this.manager.getEnchantLevel(player.getInventory().getItemInMainHand(), enchant);
            Enchant ench = this.manager.registeredEnchants.get(enchant);
            if (ench == null) return "Enchant not found!";
            level = level + 1;
            if (level > ench.max) level = ench.max;
            return (ench.cost * Long.valueOf(level)) + "";
        } else if (params.contains("max")) {
            String enchant_name = params.replace("_max", "");
            if (player.getInventory().getItemInMainHand() == null) return "Please hold your pickaxe in your main hand!";
            Enchant ench = this.manager.registeredEnchants.get(enchant_name);
            return ench.max + "";
        }  else if (params.contains("_occurrence")) {
            String enchant_name = params.replace("_occurrence", "");
            if (player.getInventory().getItemInMainHand() == null) return "Please hold your pickaxe in your main hand!";
            Enchant ench = this.manager.registeredEnchants.get(enchant_name);
            if (ench == null) return "This is not a placeholder! Contact MyNqme";
            ItemStack hand = player.getInventory().getItemInMainHand();
            int currentlevel = this.manager.getEnchantLevel(hand, enchant_name);
            if (currentlevel == 0) return "0";
            double percent = ((double) currentlevel / (double) ench.max) * 100D;
            // player.sendMessage("Currentlevel:  " + currentlevel + " Max: " + ench.max + " Percent: " + percent);
            if (percent>ench.maxChance) percent = ench.maxChance;
            return String.valueOf(percent);
        } 
        return "This is not a placeholder! Contact MyNqme";
        // return placeholders.stream().filter(placeholder->params.startsWith(placeholder.getParam())).limit(1).map(placeholder->placeholder.onRequest(player, params.replace(placeholder.getParam()+"_", "").split("_"))).collect(Collectors.joining(", "));
    }

    public static HookPapi inst() {
        return instance;
    }
}
