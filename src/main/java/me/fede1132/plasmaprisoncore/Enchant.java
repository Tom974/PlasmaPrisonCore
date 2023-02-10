package me.fede1132.plasmaprisoncore;

import org.bukkit.enchantments.Enchantment;

public class Enchant {
  private final Enchantment enchant;
  
  private final int level;
  
  public Enchant(Enchantment enchantment, int level) {
    this.enchant = enchantment;
    this.level = level;
  }
  
  public Enchantment getEnchant() {
    return this.enchant;
  }
  
  public int getLevel() {
    return this.level;
  }
}
