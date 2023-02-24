package me.mynqme.plasmaprisoncore.addons.basics.enchant;

import me.mynqme.plasmaprisoncore.enchant.BreakResult;
import me.mynqme.plasmaprisoncore.enchant.Enchant;
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
