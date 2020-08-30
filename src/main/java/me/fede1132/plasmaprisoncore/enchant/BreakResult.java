package me.fede1132.plasmaprisoncore.enchant;

import org.bukkit.Material;

import java.util.List;

public class BreakResult {
    private final List<Material> oldBlocks;
    private final int changes;
    public BreakResult(List<Material> oldBlocks, int changes) {
        this.oldBlocks = oldBlocks;
        this.changes = changes;
    }

    public List<Material> getOldBlocks() {
        return oldBlocks;
    }

    public int getChanges() {
        return changes;
    }
}
