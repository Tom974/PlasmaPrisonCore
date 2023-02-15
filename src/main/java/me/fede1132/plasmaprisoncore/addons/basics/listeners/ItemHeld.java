package me.fede1132.plasmaprisoncore.addons.basics.listeners;

import me.fede1132.plasmaprisoncore.enchant.EnchantManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

public class ItemHeld implements Listener {
    private final List<PotionEffectType> effects = Arrays.asList(PotionEffectType.JUMP, PotionEffectType.FAST_DIGGING, PotionEffectType.SPEED);
    @EventHandler
    public void onHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack hand = event.getPlayer().getInventory().getItem(event.getNewSlot());
        if (hand == null || hand.getType() != Material.DIAMOND_PICKAXE) {
            player.getActivePotionEffects().stream().map(PotionEffect::getType).filter(effects::contains).forEach(player::removePotionEffect);
            return;
        }
        int haste = EnchantManager.getInst().getEnchantLevel(hand, "haste");
        int jump = EnchantManager.getInst().getEnchantLevel(hand, "jump");
        int speed = EnchantManager.getInst().getEnchantLevel(hand, "speed");
        if (haste>0) player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1000000, --haste));
        if (jump>0) player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000000, --jump));
        if (speed>0) player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, --speed));
    }
}
