package me.fede1132.plasmaprisoncore.addons.basics.enchant;

import me.clip.autosell.AutoSellAPI;
import me.clip.autosell.objects.Shop;
import me.fede1132.plasmaprisoncore.enchant.BreakResult;
import me.fede1132.plasmaprisoncore.enchant.Enchant;
import org.bukkit.event.block.BlockBreakEvent;

public class EnchantMerchant extends Enchant {
    public EnchantMerchant() {
        super("merchant", "Merchant", 1, 1, "&d▎ &c%name% &e%level%", 100);
    }

    @Override
    public BreakResult onBreak(BlockBreakEvent event) {
        return null;
    }
}
