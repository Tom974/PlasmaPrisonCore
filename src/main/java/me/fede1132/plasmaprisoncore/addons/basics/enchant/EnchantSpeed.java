package me.fede1132.plasmaprisoncore.addons.basics.enchant;

import me.fede1132.plasmaprisoncore.enchant.BreakResult;
import me.fede1132.plasmaprisoncore.enchant.Enchant;
import org.bukkit.event.block.BlockBreakEvent;

public class EnchantSpeed extends Enchant {
    public EnchantSpeed() {
        super("speed",
                "Speed",
                3, 1,
                "b", 100);
    }

    @Override
    public BreakResult onBreak(BlockBreakEvent event) {
        return null;
    }
}
