package me.mynqme.plasmaprisoncore.addons.basics;

import me.mynqme.plasmaprisoncore.PlasmaPrisonCore;
import me.mynqme.plasmaprisoncore.addons.cmds.CommandInfo;
import me.mynqme.plasmaprisoncore.addons.cmds.XCommand;
import me.mynqme.plasmaprisoncore.enchant.Enchant;
import me.mynqme.plasmaprisoncore.enchant.EnchantManager;
import me.mynqme.plasmaprisoncore.internal.util.SimpleEntry;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

@CommandInfo(
    name = "plasmaprison",
    desc = "Plasmaprison",
    perm = "plasmaprison.commands"
)
public class CmdPlasmaPrison extends XCommand {
    private final PlasmaPrisonCore instance = PlasmaPrisonCore.getInstance();
    private final List<String> help = Arrays.asList(
        "&5&lPlasma&f&lMC &8» &fPossible commands:",
        "&5&lPlasma&f&lMC &8» &f/plasmaprison enchant <enchant_id> <level>",
        "&5&lPlasma&f&lMC &8» &f/plasmaprison enchants"
    );

    private final List<String> adminHelp = Arrays.asList(
        "/plasmaprison addons",
        "/plasmaprison unload <addon>",
        "/plasmaprison reload",
        "/plasmaprison givepick <player>"
    );

    @Override
    public void onCommand(CommandSender sender, Player player, String[] args) {
        if (args.length<1) {
            for (String s : help) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            }
            if (player.hasPermission("plasmaprison.admin")) {
                for (String s : adminHelp) {
                    player.sendMessage(s);
                }
            }
            return;
        }
        switch (args[0].toLowerCase()) {
            case "addons": {
                if (!player.hasPermission("plasmaprison.admin")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.RED + "You don't have permission to do this!");
                    return;
                }
                if (instance.addonManager.addons.size()>0) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + "Loaded addons:");
                    instance.addonManager.addons.forEach((k,v)->{
                        TextComponent dash = new TextComponent(" - ");
                        dash.setColor(ChatColor.WHITE);
                        TextComponent tc = new TextComponent();
                        tc.setText(k);
                        tc.setColor(v.getMain().isEnabled?ChatColor.GREEN:ChatColor.RED);
                        tc.setBold(true);
                        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                TextComponent.fromLegacyText(
                                        ChatColor.WHITE + "Name: " + ChatColor.YELLOW + k + "\n" +
                                                ChatColor.WHITE + "Main: " + ChatColor.YELLOW + v.getMain().getClass().getName() + "\n" +
                                                ChatColor.WHITE + "Enabled? " + (v.getMain().isEnabled?ChatColor.GREEN+"Yes":ChatColor.RED+"No"))));
                        player.getPlayer().spigot().sendMessage(dash,tc);
                    });
                    return;
                }
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.RED + "No addons found!");
                return;
            }
            case "unload": {
                if (!player.hasPermission("plasmaprison.admin")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.RED + "You don't have permission to do this!");
                    return;
                }
                if (args.length<2) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.RED + "You must need to specify at least one addon to unload!");
                    return;
                }
                if (instance.addonManager.addons.keySet().stream().anyMatch(addon->args[1].equals(addon))) {
                    instance.addonManager.unload(args[1]);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.GREEN + "Successfully unloaded " + ChatColor.WHITE + args[1]);
                    return;
                }
                player.sendMessage(ChatColor.RED + "No addon found for provided input "
                        + ChatColor.WHITE + args[1]);
                return;
            }
            case "disenchant": {
                if (!player.hasPermission("plasmaprison.disenchant")) return;
                Player playertoCheck;
                if (args.length == 4) {
                    playertoCheck = instance.getServer().getPlayer(args[3]);
                } else {
                    playertoCheck = player;
                }

                if (args.length < 3) {
                    playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.RED + "You must need to specify at least one enchantment and level!");
                    return;
                }

                if (!playertoCheck.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_PICKAXE)) {
                    playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.RED + "You must hold an item to enchant!");
                    return;
                }

                if (!instance.enchantManager.registeredEnchants.containsKey(args[1])) {
                    playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + args[1] + ChatColor.RED + " is not a valid enchantment!");
                    return;
                }
                int i;
                try {
                    i=Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + args[2] + ChatColor.RED + " is not a valid number!");
                    return;
                }
                Enchant enchant = instance.enchantManager.registeredEnchants.get(args[1]);
                // remove tokens
                AddonBasics basics = AddonBasics.getInstance();
                ItemStack hand = playertoCheck.getInventory().getItemInMainHand();
                // What level is player currently at?
                int currentlevel = instance.enchantManager.getEnchantLevel(hand, args[1]);
                i = currentlevel - i;

                long refund_amount = enchant.calcCost(i, currentlevel);
                playertoCheck.sendMessage("refund_amount: " + refund_amount + " i: " + i + " currentlevel: " + currentlevel);
                refund_amount = (refund_amount / 100) * 40; /* 40% refund percentage */
                if (refund_amount == 0) {
                    playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.RED + "Uhm, Please report this to MyNqme: (Cost was 0?)");
                    return;
                }

                playertoCheck.sendMessage("You received: " + refund_amount + " tokens to remove " + i + " levels");
                basics.addTokensWithoutFiringEvent(playertoCheck.getUniqueId(), refund_amount);
                playertoCheck.getInventory().setItemInMainHand(instance.enchantManager.enchant(hand,enchant,i,playertoCheck));
                playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.GREEN + "Successfully disenchanted the held item with " + ChatColor.WHITE + args[1]);
                return;
            }
            case "enchant": {

                Player playertoCheck;
                if (args.length == 4) {
                    playertoCheck = instance.getServer().getPlayer(args[3]);
                } else {
                    playertoCheck = player;
                }

                if (sender instanceof Player) {
                    if (!sender.hasPermission("plasmaprison.enchant")) return;
                }

                if (args.length < 3) {
                    playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.RED + "You must need to specify at least one enchantment and level!");
                    return;
                }

                
                if (!playertoCheck.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_PICKAXE)) {
                    playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.RED + "You must hold an item to enchant!");
                    return;
                }

                if (!instance.enchantManager.registeredEnchants.containsKey(args[1])) {
                    playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + args[1] + ChatColor.RED + " is not a valid enchantment!");
                    return;
                }
                int i;
                try {
                    i=Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + args[2] + ChatColor.RED + " is not a valid number!");
                    return;
                }
                Enchant enchant = instance.enchantManager.registeredEnchants.get(args[1]);
                if (i>enchant.max) i = enchant.max;
                // remove tokens
                AddonBasics basics = AddonBasics.getInstance();
                ItemStack hand = playertoCheck.getInventory().getItemInMainHand();
                // What level is player currently at?
                int currentlevel = instance.enchantManager.getEnchantLevel(hand, args[1]);
                if (currentlevel == enchant.max) {
                    playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.RED + "You have already maxed out this enchantment");
                    return;
                }

                if (i > 0) i = i + currentlevel;
                if (i > enchant.max) i = enchant.max;

                long cost = enchant.calcCost(currentlevel, i);
                if (cost == 0) {
                    playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.RED + "Uhm, Please report this to MyNqme: (Cost was 0?)");
                    return;
                }

                if (basics.getTokens(playertoCheck.getUniqueId(), false) < cost) {
                    playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.RED + "You don't have enough tokens to enchant this item!");
                    return;
                }

                playertoCheck.sendMessage("You paid: " + cost + " tokens to enchant this item to level " + (i) + " max you can enchant is " + enchant.max + " levels");
                basics.removeTokensWithoutFiringEvent(playertoCheck.getUniqueId(), cost);
                playertoCheck.getInventory().setItemInMainHand(instance.enchantManager.enchant(hand,enchant,i,playertoCheck));
                playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.GREEN + "Successfully enchant the held item with " + ChatColor.WHITE + args[1]);
                return;
            }
            case "givepick": {
                if (!player.hasPermission("plasmaprison.admin")) return;
                Player target;
                if (args.length < 2) {
                    target = player;
                } else {
                    target = Bukkit.getPlayer(args[1]);
                }

                if (target == null) {
                    player.sendMessage("Please specify an online player!");
                    return;
                }

                // get item in slot 0
                ItemStack slot0 = target.getInventory().getItem(0);
                ItemStack pick = new ItemStack(Material.DIAMOND_PICKAXE);
                ItemMeta meta = pick.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&d" + target.getName() + "'s Pickaxe"));
                pick.setItemMeta(meta);
                target.getInventory().setItem(0, instance.enchantManager.enchant(pick, instance.enchantManager.registeredEnchants.get("fortune"), 100, target));
                pick = target.getInventory().getItem(0);
                target.getInventory().setItem(0, instance.enchantManager.enchant(pick, instance.enchantManager.registeredEnchants.get("efficiency"), 1000, target));
                pick = target.getInventory().getItem(0);
                target.getInventory().setItem(0, instance.enchantManager.enchant(pick, instance.enchantManager.registeredEnchants.get("tokenator"), 10, target));
                pick = target.getInventory().getItem(0);
                target.getInventory().setItem(0, instance.enchantManager.enchant(pick, instance.enchantManager.registeredEnchants.get("haste"), 5, target));
                // check if slot 0 had an item in it

                if (slot0 == null) return;
                if (!slot0.getType().equals(Material.AIR)) {
                    target.getInventory().addItem(slot0);
                }
                
                return;
            }
            case "enchants": {
                if (!player.hasPermission("plasmaprison.admin")) return;
                player.sendMessage("Registered enchants:");
                EnchantManager.getInst().registeredEnchants.values().stream().sorted(Comparator.comparing(Enchant::getId)).forEach(enchant->{
                    TextComponent text = new TextComponent();
                    text.setText(ChatColor.WHITE + " - " + ChatColor.GREEN + ChatColor.BOLD + enchant.getId());
                    StringBuilder sb = new StringBuilder();
                    for (SimpleEntry<String, Object> entry : enchant.options) {
                        sb.append("&f").append(entry.getKey()).append(": &a").append(entry.getValue().toString()).append("\n");
                    }

                    String adminString = "";
                    if (player.hasPermission("plasmaprison.admin")) {
                        adminString = "\n&fDefault cost: &a" + enchant.cost + "\n" +"\n&eOptions:\n" + sb;
                    }
                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                            ChatColor.translateAlternateColorCodes('&', "DisplayName: &a" + enchant.displayName +
                                    "\n&fMax level: &a" + enchant.max +
                                    "\n&fMax chance: &a" + enchant.maxChance +
                                    "\n&fRefund percent: &a" + enchant.refundPercent + adminString))));
                    player.spigot().sendMessage(text);
                });
                return;
            }
            case "reload": {
                if (!player.hasPermission("plasmaprison.admin")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.RED + "You don't have permission to do that!");
                    return;
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.GREEN + "Reloading Addons...");
                    instance.addonManager.reloadAddons();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.GREEN + "Reloaded Addons!");
                    return;
                }
            }
            case "nbtdata": {
                if (!player.hasPermission("plasmaprison.admin")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.RED + "You don't have permission to do that!");
                    return;
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.GREEN + "NBT Data:");
                    ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
                    if (itemInMainHand != null && !itemInMainHand.getType().equals(Material.AIR)) {
                        ItemStack item = new ItemStack(itemInMainHand);
                        net.minecraft.server.v1_12_R1.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
                        NBTTagCompound nbtTagCompound = itemStack.getTag();

                        if (nbtTagCompound == null)
                            nbtTagCompound = new NBTTagCompound();

                        if (!item.getType().equals(Material.AIR)) {

                            Set<String> keys = nbtTagCompound.c();
                            for (String key : keys) {
                                player.sendMessage(key + ": " + nbtTagCompound.get(key).toString());
                            }
                        }
                    }
                }
                return;
            }
            default:
                for (String s : help) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
                }
                if (player.hasPermission("plasmaprison.admin")) {
                    for (String s : adminHelp) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.WHITE + s);
                        
                    }
                }
                break;
        }
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        if (args.length==1) {
            return Arrays.asList("addons", "enchant", "disenchant", "unload", "reload", "givepick", "enchants");
        } else if (args[0].equalsIgnoreCase("enchant") || args[0].equalsIgnoreCase("disenchant")) {
            if (args.length==2) {
                return instance.enchantManager.registeredEnchants.values().stream().map(Enchant::getId).collect(Collectors.toList());
            } else if (args.length==3) {
                return Arrays.asList("1", "10", "100", "1000");
            }
            return Collections.EMPTY_LIST;
        } else if (args[0].equalsIgnoreCase("givepick")) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        } else if (args[0].equalsIgnoreCase("unload")) {
            return new ArrayList<>(instance.addonManager.addons.keySet());
        }
        return Collections.EMPTY_LIST;
    }
}
