package me.fede1132.testaddon;

import me.fede1132.plasmaprisoncore.enchant.Addon;

public class TestAddon extends Addon {
    @Override
    public void load() {
        registerListeners(new TestListener());
    }
}
