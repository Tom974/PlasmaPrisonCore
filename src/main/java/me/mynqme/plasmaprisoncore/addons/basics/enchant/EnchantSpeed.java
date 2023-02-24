package me.mynqme.plasmaprisoncore.addons.basics.enchant;

import me.mynqme.plasmaprisoncore.enchant.BreakResult;
import me.mynqme.plasmaprisoncore.enchant.Enchant;
import org.bukkit.event.block.BlockBreakEvent;

public class EnchantSpeed extends Enchant {
    public EnchantSpeed() {
        super("speed","Speed",3, 1,"&d▎ &c%name% &e%level%", 100);
    }

    @Override
    public BreakResult onBreak(BlockBreakEvent event) {
        return null;
    }
}
