package me.fede1132.plasmaprisoncore.addons.basics;

import me.fede1132.f32lib.other.StringUtil;
import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import me.fede1132.plasmaprisoncore.addons.cmds.CommandInfo;
import me.fede1132.plasmaprisoncore.addons.cmds.XCommand;
import me.fede1132.plasmaprisoncore.enchant.Enchant;
import me.fede1132.plasmaprisoncore.enchant.EnchantManager;
import me.fede1132.plasmaprisoncore.internal.util.SimpleEntry;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

@CommandInfo(
        name="plasmaprison",
        desc = "Plasmaprison",
        perm = "plasmaprison.admin"
)
public class CmdPlasmaPrison extends XCommand {
    private final PlasmaPrisonCore instance = PlasmaPrisonCore.getInstance();
    @Override
    public void onCommand(Player player, String[] args) {
        if (args.length<1) {
            player.sendMessage("Missing arguments..");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "addons": {
                if (instance.addonManager.addons.size()>0) {
                    player.sendMessage("Loaded addons:");
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
                player.sendMessage(ChatColor.RED + "No addons found!");
                return;
            }
            case "unload": {
                if (args.length<2) {
                    player.sendMessage(ChatColor.RED + "You must need to specify at least one addon to unload!");
                    return;
                }
                if (instance.addonManager.addons.keySet().stream().anyMatch(addon->args[1].equals(addon))) {
                    instance.addonManager.unload(args[2]);
                    player.sendMessage(
                            ChatColor.GREEN + "Successfully unloaded " + ChatColor.WHITE + args[2]);
                    return;
                }
                player.sendMessage(ChatColor.RED + "No addon found for provided input "
                        + ChatColor.WHITE + args[1]);
                return;
            }
            case "enchant": {
                if (args.length<3||player.getInventory().getItemInMainHand()==null) return;
                if (!instance.enchantManager.registeredEnchants.containsKey(args[1])) {
                    player.sendMessage(args[1] + ChatColor.RED + " is not a valid enchantment!");
                    return;
                }
                int i;
                try {
                    i=Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage(args[2] + ChatColor.RED + " is not a valid number!");
                    return;
                }
                Enchant enchant = instance.enchantManager.registeredEnchants.get(args[1]);
                if (i>enchant.max) {
                    player.sendMessage(ChatColor.RED + "The maximum enchant level for this enchantment is " + ChatColor.WHITE + enchant.max);
                    return;
                }
                ItemStack hand = player.getInventory().getItemInMainHand();
                player.getInventory().setItemInMainHand(instance.enchantManager.enchant(hand,enchant,i));
                player.sendMessage(ChatColor.GREEN + "Successfully enchant the held item with "
                        + ChatColor.WHITE + args[1]);
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
                    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                            StringUtil.color("DisplayName: &a" + enchant.displayName +
                                    "\n&fMax level: &a" + enchant.max +
                                    "\n&fMax chance: &a" + enchant.maxChance +
                                    "\n&fRefund percent: &a" + enchant.refundPercent +
                                    "\n&fDefault cost: &a" + enchant.cost +
                                    "\n" +
                                    "\n&eOptions:\n" +
                                    sb.toString()))));
                    player.spigot().sendMessage(text);
                });
                return;
            case "reload": {
                player.sendMessage("Reloading addons...");
                instance.addonManager.reloadAddons();
                player.sendMessage("Addons reloaded");
            }
        }
    }

    @Override
    public List<String> onTabComplete(Player p, String[] args) {
        if (args.length==1) {
            return Arrays.asList("addons", "enchant", "unload", "reload", "enchants");
        } else if (args[0].equalsIgnoreCase("enchant")) {
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
