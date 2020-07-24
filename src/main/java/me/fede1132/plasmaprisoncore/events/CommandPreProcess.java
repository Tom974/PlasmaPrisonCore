package me.fede1132.plasmaprisoncore.events;

import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import me.fede1132.plasmaprisoncore.addons.enchant.Enchant;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

public class CommandPreProcess implements Listener {
    private final PlasmaPrisonCore instance = PlasmaPrisonCore.getInstance();
    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
        String[] args = event.getMessage().replace("/","").split(" ");
        if ((args.length==0&&!event.getMessage().toLowerCase().startsWith("/plasmaprison"))
                ||(args.length>0&&!args[0].toLowerCase().equals("plasmaprison"))) return;
        if (!event.getPlayer().hasPermission("plasmaprison.admin")) return;
        event.setCancelled(true);
        if (args.length<2) {
            event.getPlayer().sendMessage("Missing arguments..");
            return;
        }
        switch (args[1].toLowerCase()) {
            case "addons": {
                if (instance.addonManager.addons.size()>0) {
                    event.getPlayer().sendMessage("Loaded addons:");
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
                        event.getPlayer().spigot().sendMessage(dash,tc);
                    });
                    return;
                }
                event.getPlayer().sendMessage(ChatColor.RED + "No addons found!");
                return;
            }
            case "unload": {
                if (args.length<3) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You must need to specify at least one addon to unload!");
                    return;
                }
                if (instance.addonManager.addons.keySet().stream().anyMatch(addon->args[2].equals(addon))) {
                    instance.addonManager.unload(args[2]);
                    event.getPlayer().sendMessage(
                            ChatColor.GREEN + "Successfully unloaded " + ChatColor.WHITE + args[2]);
                    return;
                }
                event.getPlayer().sendMessage(ChatColor.RED + "No addon found for provided input "
                        + ChatColor.WHITE + args[2]);
                return;
            }
            case "enchant": {
                if (args.length<4||event.getPlayer().getInventory().getItemInMainHand()==null) return;
                if (!instance.enchantManager.registeredEnchants.containsKey(args[2])) {
                    event.getPlayer().sendMessage(args[2] + ChatColor.RED + " is not a valid enchantment!");
                    return;
                }
                int i;
                try {
                    i=Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    event.getPlayer().sendMessage(args[3] + ChatColor.RED + " is not a valid number!");
                    return;
                }
                Enchant enchant = instance.enchantManager.registeredEnchants.get(args[2]);
                if (i>enchant.max) {
                    event.getPlayer().sendMessage(ChatColor.RED + "The maximum enchant level for this enchantment is " + ChatColor.WHITE + enchant.max);
                    return;
                }
                ItemStack hand = event.getPlayer().getInventory().getItemInMainHand();
                event.getPlayer().getInventory().setItemInMainHand(instance.enchantManager.enchant(hand,enchant,i));
                event.getPlayer().sendMessage(ChatColor.GREEN + "Successfully enchant the held item with "
                        + ChatColor.WHITE + args[2]);
                return;
            }
            case "reload": {
                event.getPlayer().sendMessage("Reloading addons...");
                instance.addonManager.reloadAddons();
                event.getPlayer().sendMessage("Addons reloaded");
            }
        }
    }
}
