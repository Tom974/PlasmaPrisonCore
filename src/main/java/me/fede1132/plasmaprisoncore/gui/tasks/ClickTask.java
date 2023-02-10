package me.fede1132.plasmaprisoncore.gui.tasks;

import me.fede1132.plasmaprisoncore.gui.Icon;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface ClickTask {
  void fire(Icon paramIcon, InventoryClickEvent paramInventoryClickEvent);
}
