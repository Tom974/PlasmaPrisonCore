package me.fede1132.plasmaprisoncore.gui;

import me.fede1132.plasmaprisoncore.gui.tasks.ClickTask;
import me.fede1132.plasmaprisoncore.gui.tasks.UpdateTask;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Icon {
  public final Inventory inv;
  
  public ItemStack item;
  
  private int slot;
  
  private ClickTask clickTask;
  
  private UpdateTask updateTask;
  
  public Icon(Inventory inv, ItemStack item, int slot) {
    this.inv = inv;
    this.item = item;
    this.slot = slot;
  }
  
  public void addClickAction(ClickTask task) {
    this.clickTask = task;
  }
  
  public void fireClick(InventoryClickEvent event) {
    // this.clickTask.fire(this, event); TODO: FIX
  }
  
  public void setUpdateAction(int delay, int interval, UpdateTask task) {
    this.updateTask = task;
    int id = Bukkit.getScheduler().runTaskTimerAsynchronously(JavaPlugin.getPlugin(null), () -> this.updateTask.checkAndFire(this), delay, interval).getTaskId();
    task.setTask(id);
  }
  
  public int getSlot() {
    return this.slot;
  }
  
  public void set() {
    if (this.slot == -1) {
      this.slot = this.inv.firstEmpty();
      if (this.slot == -1)
        this.slot = 0; 
    } 
    this.inv.setItem(this.slot, this.item);
  }
}
