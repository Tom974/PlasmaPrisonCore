package me.fede1132.plasmaprisoncore.addons.basics.enchant;

import me.fede1132.plasmaprisoncore.enchant.BreakResult;
import me.fede1132.plasmaprisoncore.enchant.Enchant;
import org.bukkit.event.block.BlockBreakEvent;

public class EnchantHaste extends Enchant {
    public EnchantHaste() {
        super(
                "haste",
                "Haste",
                1,1,"9",100
        );
    }

    @Override
    public BreakResult onBreak(BlockBreakEvent event) {
        return null;
    }
}
