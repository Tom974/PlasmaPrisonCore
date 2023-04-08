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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.FallingBlock;
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
    private static final NavigableMap<Long, String> suffixes = new TreeMap<> ();
    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "B");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }
    private final PlasmaPrisonCore instance = PlasmaPrisonCore.getInstance();
    private final List<String> help = this.instance.messages.getStringList("help-menu.player");
    private final List<String> adminHelp = this.instance.messages.getStringList("help-menu.admin");
    @Override
    public void onCommand(CommandSender sender, Player player, String[] args) {
        if (args.length<1) {
            for (String s : help) {
                player.sendMessage(instance.color(s));
            }
            if (player.hasPermission("plasmaprison.admin")) {
                for (String s : adminHelp) {
                    player.sendMessage(instance.color(s));
                }
            }
            return;
        }
        switch (args[0].toLowerCase()) {
            case "addons": {
                if (!player.hasPermission("plasmaprison.admin")) {
                    player.sendMessage(instance.color(instance.messages.getString("prefix") + instance.messages.getString("commands.no-permission")));
                    return;
                }
                if (instance.addonManager.addons.size()>0) {
                    player.sendMessage(instance.color(instance.messages.getString("prefix") + instance.messages.getString("commands.loaded-addons")));
                    instance.addonManager.addons.forEach((k,v)->{
//                        TextComponent dash = new TextComponent(" - ");
//                        dash.setColor(ChatColor.WHITE);
//                        TextComponent tc = new TextComponent();
//                        tc.setText(k);
//                        tc.setColor(v.getMain().isEnabled?ChatColor.GREEN:ChatColor.RED);
//                        tc.setBold(true);
//                        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
//                                TextComponent.fromLegacyText(
//                                        ChatColor.WHITE + "Name: " + ChatColor.YELLOW + k + "\n" +
//                                                ChatColor.WHITE + "Main: " + ChatColor.YELLOW + v.getMain().getClass().getName() + "\n" +
//                                                ChatColor.WHITE + "Enabled? " + (v.getMain().isEnabled?ChatColor.GREEN+"Yes":ChatColor.RED+"No"))));
//                        player.sendMessage(dash,tc);
                        player.sendMessage("Enabled: " + k);
                    });
                    return;
                }
                player.sendMessage(instance.color(instance.messages.getString("prefix") + instance.messages.getString("commands.no-addons-found")));
                return;
            }
            case "unload": {
                if (!player.hasPermission("plasmaprison.admin")) {
                    player.sendMessage(instance.color(instance.messages.getString("prefix") + instance.messages.getString("commands.no-permission")));
                    return;
                }
                if (args.length<2) {
                    player.sendMessage(instance.color(instance.messages.getString("prefix") + instance.messages.getString("commands.no-addon-provided")));
                    return;
                }
                if (instance.addonManager.addons.keySet().stream().anyMatch(addon->args[1].equals(addon))) {
                    instance.addonManager.unload(args[1]);
                    player.sendMessage(instance.color(instance.messages.getString("prefix") + instance.messages.getString("commands.addon-unloaded").replace("%addon%", args[1])));
                    return;
                }
                player.sendMessage(instance.color(instance.messages.getString("prefix") + instance.messages.getString("commands.addon-not-found").replace("%addon%", args[1])));
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
                    playertoCheck.sendMessage(instance.color(this.instance.messages.getString("prefix") + this.instance.messages.getString("commands.disenchant-specify-more")));
                    return;
                }

                if (!playertoCheck.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_PICKAXE)) {
                    playertoCheck.sendMessage(instance.color(this.instance.messages.getString("prefix") + this.instance.messages.getString("commands.disenchant-not-pickaxe")));
                    return;
                }

                if (!instance.enchantManager.registeredEnchants.containsKey(args[1])) {
                    playertoCheck.sendMessage(instance.color(this.instance.messages.getString("prefix") + this.instance.messages.getString("commands.disenchant-invalid-enchant").replace("%enchant%", args[1])));
                    return;
                }
                int i;
                try {
                    i=Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    playertoCheck.sendMessage(this.instance.color(this.instance.messages.getString("prefix") + this.instance.messages.getString("commands.disenchant-invalid-level").replace("%level%", args[2])));
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
                    playertoCheck.sendMessage(instance.color(this.instance.messages.getString("prefix") + this.instance.messages.getString("commands.disenchant-no-refund")));
                    return;
                }

                playertoCheck.sendMessage("You received: " + refund_amount + " tokens to remove " + i + " levels");
                basics.addTokensWithoutFiringEvent(playertoCheck.getUniqueId(), refund_amount);
                playertoCheck.getInventory().setItemInMainHand(instance.enchantManager.enchant(hand,enchant,i,playertoCheck));
                playertoCheck.sendMessage(instance.color( this.instance.messages.getString("prefix") + this.instance.messages.getString("commands.disenchant-success").replace("%enchant%", args[1]).replace("%level%", args[2])));
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
                    playertoCheck.sendMessage(instance.color(this.instance.messages.getString("prefix") + this.instance.messages.getString("commands.enchant-specify-more")));
                    return;
                }

                
                if (!playertoCheck.getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_PICKAXE)) {
                    playertoCheck.sendMessage(instance.color( this.instance.messages.getString("prefix") + this.instance.messages.getString("commands.enchant-not-pickaxe")));
                    return;
                }

                if (!instance.enchantManager.registeredEnchants.containsKey(args[1])) {
                    playertoCheck.sendMessage(instance.color( this.instance.messages.getString("prefix") + this.instance.messages.getString("commands.enchant-invalid-enchant").replace("%enchant%", args[1])));
                    return;
                }
                int i;
                try {
                    i=Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    playertoCheck.sendMessage(instance.color( this.instance.messages.getString("prefix") + this.instance.messages.getString("commands.enchant-invalid-level").replace("%level%", args[2])));
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
                    playertoCheck.sendMessage(instance.color( this.instance.messages.getString("prefix") + this.instance.messages.getString("commands.enchant-max-level")));
                    return;
                }

                if (i > 0) i = i + currentlevel;
                if (i > enchant.max) i = enchant.max;

                long cost = enchant.calcCost(currentlevel, i);
                if (cost == 0) {
                    playertoCheck.sendMessage(instance.color( this.instance.messages.getString("prefix") + this.instance.messages.getString("commands.enchant-no-cost")));
                    return;
                }

                if (basics.getTokens(playertoCheck.getUniqueId(), false) < cost) {
                    playertoCheck.sendMessage(instance.color( this.instance.messages.getString("prefix") + this.instance.messages.getString("commands.enchant-not-enough-tokens").replace("%tokens%", String.valueOf(cost))));
                    return;
                }

//                playertoCheck.sendMessage("You paid: " + cost + " tokens to enchant this item to level " + (i) + " max you can enchant is " + enchant.max + " levels");
                basics.removeTokensWithoutFiringEvent(playertoCheck.getUniqueId(), cost);
                playertoCheck.getInventory().setItemInMainHand(instance.enchantManager.enchant(hand,enchant,i,playertoCheck));
                playertoCheck.sendMessage(instance.color( this.instance.messages.getString("prefix") + this.instance.messages.getString("commands.enchant-success").replace("%enchant%", args[1]).replace("%level%", args[2]).replace("%cost%", String.valueOf(format(cost)))));
                return;
            }
            case "givepick": {
                if (sender instanceof Player) {
                    if (!sender.hasPermission("plasmaprison.givepick")) return;
                }
                Player target;
                if (args.length < 2) {
                    target = player;
                } else {
                    if (args[1].equalsIgnoreCase("*")) {
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            // get item in slot 0
                            ItemStack slot0 = p.getInventory().getItem(0);
                            ItemStack pick = new ItemStack(Material.DIAMOND_PICKAXE);
                            ItemMeta meta = pick.getItemMeta();
                            meta.setDisplayName(instance.color("&d" + p.getName() + "'s Pickaxe"));
                            pick.setItemMeta(meta);
                            p.getInventory().setItem(0, instance.enchantManager.enchant(pick, instance.enchantManager.registeredEnchants.get("fortune"), 100, p));
                            pick = p.getInventory().getItem(0);
                            p.getInventory().setItem(0, instance.enchantManager.enchant(pick, instance.enchantManager.registeredEnchants.get("efficiency"), 1000, p));
                            pick = p.getInventory().getItem(0);
                            p.getInventory().setItem(0, instance.enchantManager.enchant(pick, instance.enchantManager.registeredEnchants.get("tokenator"), 10, p));
                            pick = p.getInventory().getItem(0);
                            p.getInventory().setItem(0, instance.enchantManager.enchant(pick, instance.enchantManager.registeredEnchants.get("haste"), 5, p));
                            // check if slot 0 had an item in it

                            if (slot0 == null) return;
                            if (!slot0.getType().equals(Material.AIR)) {
                                p.getInventory().addItem(slot0);
                            }
                        }
                        sender.sendMessage(instance.color( this.instance.messages.getString("prefix") + this.instance.messages.getString("commands.givepick-all")));
                        return;
                    }
                    target = Bukkit.getPlayer(args[1]);
                }

                if (target == null) {
                    player.sendMessage(instance.color( this.instance.messages.getString("prefix") + this.instance.messages.getString("commands.player-not-found")));
                    return;
                }

                // get item in slot 0
                ItemStack slot0 = target.getInventory().getItem(0);
                ItemStack pick = new ItemStack(Material.DIAMOND_PICKAXE);
                ItemMeta meta = pick.getItemMeta();
                meta.setDisplayName(instance.color("&d" + target.getName() + "'s Pickaxe"));
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
//                    TextComponent text = new TextComponent();
//                    text.setText(ChatColor.WHITE + " - " + ChatColor.GREEN + ChatColor.BOLD + enchant.getId());
//                    StringBuilder sb = new StringBuilder();
//                    // print enchant.options in chat
////                    player.sendMessage("Options for " + enchant.getId() + ":");
////                    for (SimpleEntry<String, Object> entry : enchant.options) {
////                        sb.append("&f").append(entry.getKey()).append(": &a").append(entry.getValue().toString()).append("\n");
////                    }
//
//                    String adminString = "";
//                    if (player.hasPermission("plasmaprison.admin")) {
//                        adminString = "\n&fDefault cost: &a" + enchant.cost + "\n" +"\n&eOptions:\n" + sb;
//                    }
//                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
//                            ChatColor.translateAlternateColorCodes('&', "DisplayName: &a" + enchant.displayName +
//                                    "\n&fMax level: &a" + enchant.max +
//                                    "\n&fMax chance: &a" + enchant.maxChance +
//                                    "\n&fRefund percent: &a" + enchant.refundPercent + adminString))));
//                    player.isOp().sendMessage(text);
                    player.sendMessage("Message: " + enchant);
                });
                return;
            }
            case "reload": {
                if (!player.hasPermission("plasmaprison.admin")) {
                    player.sendMessage(instance.color(this.instance.messages.getString("prefix") + this.instance.messages.getString("commands.no-permission")));
                    return;
                } else {
                    player.sendMessage(instance.color(this.instance.messages.getString("prefix") + this.instance.messages.getString("commands.reloading")));
                    instance.addonManager.reloadAddons();
                    player.sendMessage(instance.color(this.instance.messages.getString("prefix") + this.instance.messages.getString("commands.reload-complete")));
                    return;
                }
            }
//            case "nbtdata": {
//                if (!player.hasPermission("plasmaprison.admin")) {
//                    player.sendMessage(instance.color(this.instance.messages.getString("prefix") + this.instance.messages.getString("commands.no-permission")));
//                    return;
//                } else {
//                    player.sendMessage(instance.color(this.instance.messages.getString("prefix") + this.instance.messages.getString("commands.nbt-data")));
//                    ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
//                    if (itemInMainHand != null && !itemInMainHand.getType().equals(Material.AIR)) {
//                        ItemStack item = new ItemStack(itemInMainHand);
//                        net.minecraft.server.v1_12_R1.ItemStack itemStack = CraftItemStack.asNMSCopy(item);
//                        NBTTagCompound nbtTagCompound = itemStack.getTag();
//
//                        if (nbtTagCompound == null)
//                            nbtTagCompound = new NBTTagCompound();
//
//                        if (!item.getType().equals(Material.AIR)) {
//
//                            Set<String> keys = nbtTagCompound.c();
//                            for (String key : keys) {
//                                player.sendMessage(key + ": " + nbtTagCompound.get(key).toString());
//                            }
//                        }
//                    }
//                }
//                return;
//            }
            default:
                for (String s : help) {
                    player.sendMessage(instance.color(s));
                }
                if (player.hasPermission("plasmaprison.admin")) {
                    for (String s : adminHelp) {
                        player.sendMessage(instance.color(this.instance.messages.getString("prefix") + s));
                        
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
