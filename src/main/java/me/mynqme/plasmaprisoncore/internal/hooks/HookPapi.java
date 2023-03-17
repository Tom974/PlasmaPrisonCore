package me.mynqme.plasmaprisoncore.internal.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.mynqme.plasmaprisoncore.enchant.Enchant;
import me.mynqme.plasmaprisoncore.addons.basics.AddonBasics;
import me.mynqme.plasmaprisoncore.enchant.EnchantManager;
import me.mynqme.plasmaprisoncore.internal.util.NumberFormat;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

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
        return "MyNqme";
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
            if (amount > enchant.max) amount = enchant.max;
            // remove tokens
            ItemStack hand = player.getInventory().getItemInMainHand();
            // What level is player currently at?
            int currentlevel = this.manager.getEnchantLevel(hand, enchant_name);
            
            int to = (amount + currentlevel);
            if (to >= enchant.max) to = enchant.max;
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
        } else if (params.contains("_occurrence_max")) {
            String enchant_name = params.replace("_occurrence_max", "");
            Enchant ench = this.manager.registeredEnchants.get(enchant_name);
            if (ench == null) return "This is not a placeholder! Contact MyNqme";
            BigDecimal bd = new BigDecimal(String.valueOf(ench.maxChance));
            return new DecimalFormat("#0.####").format(bd).toString();
        } else if (params.contains("_occurrence_")) {
            String enchant_name = params.split("_occurrence_")[0];
            int currentlevel = Integer.parseInt(params.split("_occurrence_")[1]);
            if (player.getInventory().getItemInMainHand() == null) return "Please hold your pickaxe in your main hand!";
            Enchant ench = this.manager.registeredEnchants.get(enchant_name);
            if (ench == null) return "This is not a placeholder! Contact MyNqme";
            ItemStack hand = player.getInventory().getItemInMainHand();
            int enchlevel = this.manager.getEnchantLevel(hand, enchant_name);
            if (enchlevel != 0) {
                currentlevel = currentlevel + enchlevel;
            }
            // calculate the occurrence based on the current level, the max level and the maxChance
            double percent = ((double) currentlevel / (double) ench.max) * ench.maxChance;
            if (percent>ench.maxChance) percent = ench.maxChance;
            BigDecimal bd = new BigDecimal(String.valueOf(percent));

            return new DecimalFormat("#0.####").format(bd).toString();
        } else if (params.contains("_occurrence")) {
            String enchant_name = params.replace("_occurrence", "");
            if (player.getInventory().getItemInMainHand() == null) return "Please hold your pickaxe in your main hand!";
            Enchant ench = this.manager.registeredEnchants.get(enchant_name);
            if (ench == null) return "This is not a placeholder! Contact MyNqme";
            ItemStack hand = player.getInventory().getItemInMainHand();
            int currentlevel = this.manager.getEnchantLevel(hand, enchant_name);
            if (currentlevel == 0) return "0";
            // calculate the occurrence based on the current level, the max level and the maxChance
            double percent = ((double) currentlevel / (double) ench.max) * ench.maxChance;
            if (percent>ench.maxChance) percent = ench.maxChance;

            BigDecimal bd = new BigDecimal(String.valueOf(percent));

            return new DecimalFormat("#0.####").format(bd).toString();
        } else if (params.contains("max")) {
            String enchant_name = params.replace("_max", "");
            if (player.getInventory().getItemInMainHand() == null) return "Please hold your pickaxe in your main hand!";
            Enchant ench = this.manager.registeredEnchants.get(enchant_name);
            return ench.max + "";
        }
        return "This is not a placeholder! Contact MyNqme";
        // return placeholders.stream().filter(placeholder->params.startsWith(placeholder.getParam())).limit(1).map(placeholder->placeholder.onRequest(player, params.replace(placeholder.getParam()+"_", "").split("_"))).collect(Collectors.joining(", "));
    }

    public static HookPapi inst() {
        return instance;
    }
}
