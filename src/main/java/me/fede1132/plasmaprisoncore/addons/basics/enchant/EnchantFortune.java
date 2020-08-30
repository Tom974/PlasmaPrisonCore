package me.fede1132.plasmaprisoncore.addons.basics.enchant;

import me.fede1132.plasmaprisoncore.enchant.BreakResult;
import me.fede1132.plasmaprisoncore.enchant.Enchant;
import org.bukkit.event.block.BlockBreakEvent;

public class EnchantFortune extends Enchant {
    public EnchantFortune() {
        super("fortune",
                "Fortune", 100, 1, "c", 100);
    }

    @Override
    public BreakResult onBreak(BlockBreakEvent event) {
        return null;
    }
}
