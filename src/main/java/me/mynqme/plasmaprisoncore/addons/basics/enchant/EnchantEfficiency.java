package me.mynqme.plasmaprisoncore.addons.basics.enchant;

import me.mynqme.plasmaprisoncore.enchant.BreakResult;
import me.mynqme.plasmaprisoncore.enchant.Enchant;
import org.bukkit.event.block.BlockBreakEvent;

public class EnchantEfficiency extends Enchant {

    public EnchantEfficiency() {
        super("efficiency","Efficiency", 100, 1, "&d▎ &c%name% &e%level%", 100);
    }

    @Override
    public BreakResult onBreak(BlockBreakEvent event) {
        return null;
    }
}
