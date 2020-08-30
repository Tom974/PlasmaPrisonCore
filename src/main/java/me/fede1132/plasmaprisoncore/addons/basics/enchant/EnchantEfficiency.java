package me.fede1132.plasmaprisoncore.addons.basics.enchant;

import me.fede1132.plasmaprisoncore.enchant.BreakResult;
import me.fede1132.plasmaprisoncore.enchant.Enchant;
import org.bukkit.event.block.BlockBreakEvent;

public class EnchantEfficiency extends Enchant {

    public EnchantEfficiency() {
        super("efficiency",
                "Efficiency", 100, 1, "3", 100);
    }

    @Override
    public BreakResult onBreak(BlockBreakEvent event) {
        return null;
    }
}
