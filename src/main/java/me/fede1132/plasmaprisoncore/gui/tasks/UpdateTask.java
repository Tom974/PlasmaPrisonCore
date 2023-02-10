package me.fede1132.plasmaprisoncore.gui.tasks;

import me.fede1132.plasmaprisoncore.gui.Icon;
import org.bukkit.Bukkit;

public abstract class UpdateTask {
  private int task;
  
  public void setTask(int task) {
    this.task = task;
  }
  
  public void checkAndFire(Icon icon) {
    if (icon.inv == null || icon.inv.getViewers().size() == 0) {
      Bukkit.getScheduler().cancelTask(this.task);
      return;
    } 
    fire(icon);
    icon.inv.setItem(icon.getSlot(), icon.item);
  }
  
  public abstract void fire(Icon paramIcon);
}
