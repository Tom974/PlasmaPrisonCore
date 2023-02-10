package me.fede1132.plasmaprisoncore.enchant;

import me.fede1132.plasmaprisoncore.internal.util.SimpleEntry;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;
import java.util.Map;

public abstract class Enchant {
    private final String id;
    public String displayName;
    public int max;
    public long cost;
    public String loreColor;
    public int maxChance;
    public SimpleEntry<String,Object>[] options;
    public String jsScript;
    public HashMap<Integer, Long> costs = new HashMap<>();
    public int refundPercent = 40;
    @SafeVarargs
    public Enchant(String id, String displayName, int max, int cost, String loreColor, int maxChance, SimpleEntry<String,Object>... options) {
        this.id = id;
        this.displayName = displayName;
        this.max = max;
        this.cost = cost;
        this.maxChance = maxChance;
        this.loreColor = loreColor;
        this.options = options;
    }

    public String getId() {
        return id;
    }

    public abstract BreakResult onBreak(BlockBreakEvent event);

    public long calcCost(int from, int to) {
        return costs.entrySet().stream().filter(entry->entry.getKey()>from&&entry.getKey()<=to).mapToLong(Map.Entry::getValue).sum();
    }

}
