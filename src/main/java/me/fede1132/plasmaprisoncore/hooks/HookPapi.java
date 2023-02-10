package me.fede1132.plasmaprisoncore.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HookPapi extends Hook {
  public HookPapi() {
    super("PlaceholderAPI", Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"));
  }
  
  public String translate(Player player, String input) {
    return PlaceholderAPI.setPlaceholders(player, input);
  }
}
