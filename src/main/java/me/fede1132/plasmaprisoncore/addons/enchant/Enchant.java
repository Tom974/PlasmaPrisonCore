package me.fede1132.plasmaprisoncore.addons.enchant;

import org.bukkit.Material;

import java.util.Map;

public abstract class Enchant {
    public static class EnchantOption<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;

        public EnchantOption(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            return this.value=value;
        }
    }
    private final String id;
    private final String displayName;
    private final Material[] applicableMaterials;
    public int max;
    public int cost;
    public String loreColor;
    public EnchantOption<String,Object>[] options;
    public Enchant(String id, String displayName, int max,
                   int cost, String loreColor, EnchantOption<String,Object>[] options,
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
