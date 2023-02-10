package me.fede1132.plasmaprisoncore.addons.basics;

import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import me.fede1132.plasmaprisoncore.addons.cmds.CommandInfo;
import me.fede1132.plasmaprisoncore.addons.cmds.XCommand;
import me.fede1132.plasmaprisoncore.enchant.Enchant;
import me.fede1132.plasmaprisoncore.enchant.EnchantManager;
import me.fede1132.plasmaprisoncore.internal.util.SimpleEntry;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@CommandInfo(
        name="plasmaprison",
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
        "/plasmaprison reload"
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
                if (args.length<2) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.RED + "You must need to specify at least one addon to unload!");
                    return;
                }
                if (instance.addonManager.addons.keySet().stream().anyMatch(addon->args[1].equals(addon))) {
                    instance.addonManager.unload(args[2]);
                    player.sendMessage(
                        ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.GREEN + "Successfully unloaded " + ChatColor.WHITE + args[2]);
                    return;
                }
                player.sendMessage(ChatColor.RED + "No addon found for provided input "
                        + ChatColor.WHITE + args[1]);
                return;
            }
            // TODO: Fix disenchanter
            // case "disenchant": {
            //     Player playertoCheck;
            //     if (args.length == 4) {
            //         playertoCheck = instance.getServer().getPlayer(args[3]);
            //     } else {
            //         playertoCheck = player;
            //     }

            //     if (args.length < 3) {
            //         playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.RED + "You must need to specify at least one enchantment and level!");
            //         return;
            //     }

                
            //     if (!playertoCheck.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_PICKAXE)) {
            //         playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.RED + "You must hold an item to enchant!");
            //         return;
            //     }

            //     if (!instance.enchantManager.registeredEnchants.containsKey(args[1])) {
            //         playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + args[1] + ChatColor.RED + " is not a valid enchantment!");
            //         return;
            //     }
            //     int i;
            //     try {
            //         i=Integer.parseInt(args[2]);
            //     } catch (NumberFormatException e) {
            //         playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + args[2] + ChatColor.RED + " is not a valid number!");
            //         return;
            //     }
            //     Enchant enchant = instance.enchantManager.registeredEnchants.get(args[1]);
            //     // if (i>enchant.max) i = enchant.max;
            //     // remove tokens
            //     AddonBasics basics = AddonBasics.getInstance();
            //     ItemStack hand = playertoCheck.getInventory().getItemInMainHand();
            //     // What level is player currently at?
            //     int currentlevel = instance.enchantManager.getEnchantLevel(hand, args[1]);
            //     if (currentlevel == 0) {
            //         playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.RED + "You cant disenchant this any more since your level is already at 0!");
            //         return;
            //     }

            //     // if (i > 0) {
            //     //     i = i + currentlevel;
            //     // }

            //     // if (i > enchant.max) {
            //     //     i = enchant.max;
            //     // }

            //     int levelToGoTo = currentlevel - i;
 
            //     if (levelToGoTo < 0) {
            //         levelToGoTo = 0;
            //     }

            //     playertoCheck.sendMessage("YOUR CURRENT PICKAXE LEVEL IS: " + currentlevel + " YOU WANT TO ENCHANT TO: " + i + " LEVELS");
            //     int maxUserCanRefund = 0;
            //     long refundAmount = 0;
            //     if (currentlevel == 1 && i == 1) {
            //         refundAmount = enchant.cost;
            //         maxUserCanRefund = 1;
            //     } else {
            //         // count i amount down
            //         for (int level = currentlevel; level >= levelToGoTo; level--) {
            //             if (level == currentlevel) level = level - 1;
            //             playertoCheck.sendMessage("loop: " + level + " refund amount this loop:" + (enchant.cost * level));
            //             maxUserCanRefund++;
            //             refundAmount = refundAmount + (enchant.cost * level);
            //         }
            //     }

            //     if (refundAmount == 0) {
            //         playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.RED + "You don't have enough tokens to enchant this item!");
            //         return;
            //     }
            //     playertoCheck.sendMessage("total cost: " + refundAmount + " tokens to enchant this item to level " + (i) + " max you can refund is " + maxUserCanRefund + " levels");
            //     if (basics.getTokens(playertoCheck.getUniqueId()).longValue() < refundAmount) {
            //         playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.RED + "You don't have enough tokens to enchant this item!");
            //         return;
            //     }
            //     basics.addTokensWithoutFiringEvent(playertoCheck.getUniqueId(), new BigDecimal(refundAmount));
            //     if (levelToGoTo == 0) {
            //         playertoCheck.getInventory().setItemInMainHand(instance.enchantManager.removeEnchant(hand, enchant));
            //     } else {
            //         playertoCheck.getInventory().setItemInMainHand(instance.enchantManager.enchant(hand, enchant, levelToGoTo));
            //     }
                
            //     playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.GREEN + "Successfully enchant the held item with " + ChatColor.WHITE + args[1]);
            //     return;
            // }
            case "updatelore": {
                Enchant enchant = instance.enchantManager.registeredEnchants.get(args[1]);
                // if (i>enchant.max) i = enchant.max;
                // remove tokens
                // AddonBasics basics = AddonBasics.getInstance();
                ItemStack hand = player.getInventory().getItemInMainHand();
                // What level is player currently at?
                // int currentlevel = instance.enchantManager.getEnchantLevel(hand, args[1]);
                
                
                player.getInventory().setItemInMainHand(instance.enchantManager.enchant(hand,enchant,1,player));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.GREEN + "Updated the lore!");
                return;  
            }
            case "enchant": {
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

                if (i > 0) {
                    i = i + currentlevel;
                }

                if (i > enchant.max) {
                    i = enchant.max;
                }
                
                int maxUserCanBuy = 0;
                long cost = 0;
                if (currentlevel == 0 && i == 1) {
                    cost = enchant.cost;
                    maxUserCanBuy = 1;
                } else {
                    for (int level = currentlevel; level <= i; level++) {
                        if (level == currentlevel) level = level + 1;
                        maxUserCanBuy++;
                        long newCost = cost + (enchant.cost * level);
                        if (basics.getTokens(playertoCheck.getUniqueId()).longValue() < newCost) {
                            maxUserCanBuy = maxUserCanBuy - 1;
                            break;
                        } else {
                            cost = newCost;
                        }
                    }
                }

                if (cost == 0) {
                    playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.RED + "You don't have enough tokens to enchant this item!");
                    return;
                }
                if (basics.getTokens(playertoCheck.getUniqueId()).longValue() < cost) {
                    playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.RED + "You don't have enough tokens to enchant this item!");
                    return;
                }
                basics.removeTokensWithoutFiringEvent(playertoCheck.getUniqueId(), new BigDecimal(cost));
                
                
                playertoCheck.getInventory().setItemInMainHand(instance.enchantManager.enchant(hand,enchant,i,playertoCheck));
                playertoCheck.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.GREEN + "Successfully enchant the held item with " + ChatColor.WHITE + args[1]);
                return;
            }
            case "enchants":
                player.sendMessage("Registered enchants:");
                EnchantManager.getInst().registeredEnchants.values().stream().sorted(Comparator.comparing(Enchant::getId)).forEach(enchant->{
                    TextComponent text = new TextComponent();
                    text.setText(ChatColor.WHITE + " - " + ChatColor.GREEN.toString() + ChatColor.BOLD + enchant.getId());
                    StringBuilder sb = new StringBuilder();
                    for (SimpleEntry<String, Object> entry : enchant.options) {
                        sb.append("&f").append(entry.getKey()).append(": &a").append(entry.getValue().toString()).append("\n");
                    }

                    String adminString = "";
                    if (player.hasPermission("plasmaprison.admin")) {
                        adminString = "\n&fDefault cost: &a" + enchant.cost + "\n" +"\n&eOptions:\n" + sb.toString();
                    }
                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                            ChatColor.translateAlternateColorCodes('&', "DisplayName: &a" + enchant.displayName +
                                    "\n&fMax level: &a" + enchant.max +
                                    "\n&fMax chance: &a" + enchant.maxChance +
                                    "\n&fRefund percent: &a" + enchant.refundPercent + adminString))));
                    player.spigot().sendMessage(text);
                });
                return;
            case "reload": {
                if (!player.hasPermission("plasmaprison.admin")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.RED + "You don't have permission to do that!");
                    return;
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.GREEN + "Reloading...");
                    instance.addonManager.reloadAddons();
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5&lPlasma&f&lMC &8» &f") + ChatColor.GREEN + "Reloaded!");
                }
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
            return Arrays.asList("addons", "enchant", "disenchant", "unload", "reload", "enchants");
        } else if (args[0].equalsIgnoreCase("enchant") || args[0].equalsIgnoreCase("disenchant")) {
            if (args.length==2) {
                return instance.enchantManager.registeredEnchants.values().stream().map(Enchant::getId).collect(Collectors.toList());
            } else if (args.length==3) {
                return Arrays.asList("1", "10", "100", "1000");
            }
            return Collections.EMPTY_LIST;
        } else if (args[0].equalsIgnoreCase("unload")) {
            return new ArrayList<>(instance.addonManager.addons.keySet());
        }
        return Collections.EMPTY_LIST;
    }
}
