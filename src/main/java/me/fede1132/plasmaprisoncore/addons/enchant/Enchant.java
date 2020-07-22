package me.fede1132.plasmaprisoncore.addons.enchant;

import me.fede1132.plasmaprisoncore.internal.util.SimpleEntry;
import org.bukkit.Material;

public abstract class Enchant {
    private final String id;
    private final String displayName;
    private final Material[] applicableMaterials;
    public int max;
    public int cost;
    public String loreColor;
    public SimpleEntry<String,Object>[] options;
    public Enchant(String id, String displayName, int max,
                   int cost, String loreColor, SimpleEntry<String,Object>[] options,
                   Material... applicableMaterials) {
        this.id = id;
        this.displayName = displayName;
        this.applicableMaterials = applicableMaterials;
        this.max = max;
        this.cost = cost;
        this.loreColor = loreColor;
        this.options = options;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material[] getApplicableMaterials() {
        return applicableMaterials;
    }
}
