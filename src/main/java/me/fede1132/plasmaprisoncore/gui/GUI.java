package me.fede1132.plasmaprisoncore.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

public abstract class GUI implements Listener, InventoryHolder {
  public final Inventory inv;
  
  private final Player player;
  
  private final String name;
  
  private final int size;
  
  private final boolean clickCancelled;
  
  public Icon[] icons;
  
  public GUI(Plugin plugin, Player player, String name, int size, boolean cancelClick) {
    this.player = player;
    this.name = ChatColor.translateAlternateColorCodes('&', name);
    this.size = size;
    this.clickCancelled = cancelClick;
    this.icons = new Icon[size];
    GUIHolder holder = new GUIHolder();
    this.inv = Bukkit.createInventory(holder, size, this.name);
    body();
    Bukkit.getPluginManager().registerEvents(this, plugin);
  }
  
  public abstract void body();
  
  public void fill(Icon item, int from, int to) {
    for (int i = from; i <= to; ) {
      this.inv.setItem(i, item.item);
      i++;
    } 
  }
  
  public void fill(Icon item, int... slots) {
    for (int slot : slots) {
      this.inv.setItem(slot, item.item);
      this.icons[slot] = item;
    } 
  }
  
  public void fill(Icon icon) {
    for (int i = 0; i < this.size; i++) {
      this.inv.setItem(i, icon.item);
      this.icons[i] = icon;
    } 
  }
  
  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (event.getClickedInventory().getHolder() != this)
      return; 
    if (event.getClickedInventory() == null)
      return; 
    if (!event.getView().getTitle().equals(this.name))
      return; 
    if (event.getCurrentItem() == null)
      return; 
    event.setCancelled(this.clickCancelled);
    assert this.icons[event.getSlot()] != null && event != null;
    this.icons[event.getSlot()].fireClick(event); 
  }
  
  @EventHandler
  public void onClose(InventoryCloseEvent event) {
    if (event.getInventory().equals(this.inv))
      HandlerList.unregisterAll(this); 
  }
  
  public void open() {
    this.player.openInventory(this.inv);
  }
  
  public Inventory getInv() {
    return this.inv;
  }
  
  public Player getPlayer() {
    return this.player;
  }
  
  public String getName() {
    return this.name;
  }
  
  public int getSize() {
    return this.size;
  }
  
  public boolean isClickCancelled() {
    return this.clickCancelled;
  }
  
  public Inventory getInventory() {
    return this.inv;
  }
}
