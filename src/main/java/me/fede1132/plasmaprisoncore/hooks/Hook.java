package me.fede1132.plasmaprisoncore.hooks;

import org.bukkit.entity.Player;

public abstract class Hook {
  private final String hook;
  
  private boolean enabled;
  
  public Hook(String hook, boolean enabled) {
    this.hook = hook;
    this.enabled = enabled;
  }
  
  public String translate(Player player, String input) {
    return input;
  }
  
  public void toggle() {
    this.enabled = !this.enabled;
  }
  
  public String getHookName() {
    return this.hook;
  }
  
  public boolean isEnabled() {
    return this.enabled;
  }
}
