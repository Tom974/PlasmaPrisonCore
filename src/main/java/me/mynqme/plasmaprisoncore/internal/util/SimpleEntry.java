package me.mynqme.plasmaprisoncore.internal.util;

import java.util.Map;

public class SimpleEntry<K, V> implements Map.Entry<K, V> {
    private final K key;
    private V value;
    public SimpleEntry(K key, V value) {
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
        this.value = value;
        return value;
    }
}
