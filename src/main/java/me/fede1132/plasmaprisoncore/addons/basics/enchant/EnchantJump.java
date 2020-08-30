package me.fede1132.plasmaprisoncore.addons.basics.enchant;

import me.fede1132.plasmaprisoncore.enchant.BreakResult;
import me.fede1132.plasmaprisoncore.enchant.Enchant;
import me.fede1132.plasmaprisoncore.internal.util.SimpleEntry;
import org.bukkit.event.block.BlockBreakEvent;

public class EnchantJump extends Enchant {
    public EnchantJump() {
        super("jump",
                "Jump",
                3, 1, "3", 100);
    }

    @Override
    public BreakResult onBreak(BlockBreakEvent event) {
        return null;
    }
}
