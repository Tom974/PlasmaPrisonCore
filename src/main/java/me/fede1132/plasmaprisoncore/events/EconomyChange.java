package me.fede1132.plasmaprisoncore.events;

import com.gmail.fendt873.f32lib.other.Placeholder;
import me.fede1132.plasmaprisoncore.actionbar.ActionBar;
import me.fede1132.plasmaprisoncore.internal.events.VaultEconomyEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class EconomyChange implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void onEconomyChange(VaultEconomyEvent event) {
        if (event.getNewAmount()>event.getCurrentAmount()) {
            ActionBar.ActionBarTypes.ECONOMY.fire(event.getPlayer(), new Placeholder("money",
                    String.valueOf((event.getNewAmount()-event.getCurrentAmount()))));
        }
    }
}
