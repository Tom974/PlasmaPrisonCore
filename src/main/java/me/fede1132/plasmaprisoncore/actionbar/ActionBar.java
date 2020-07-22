package me.fede1132.plasmaprisoncore.actionbar;

import com.gmail.fendt873.f32lib.other.Placeholder;
import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import me.fede1132.plasmaprisoncore.internal.util.SimpleEntry;
import me.fede1132.plasmaprisoncore.internal.util.StringUtil;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class ActionBar {
    private static final int STAY_TIME = 3;
    private static final PlasmaPrisonCore instance = PlasmaPrisonCore.getInstance();
    private static final HashMap<Player, HashMap<ActionBarTypes, Integer>> bars = new HashMap<>();
    private static final List<SimpleEntry<Player, ActionBarTypes>> fireQueue = new ArrayList<>();
    private static final String spacer = StringUtil.color(instance.messages.getString("action-bar.spacer"));
    public enum ActionBarTypes {
        ECONOMY;
        private final String defPath = "action-bar." + this.toString().toLowerCase();

        private String text;
        public boolean isEnabled() {
            return instance.messages.getOrSetDefault(defPath + ".enabled", true);
        }

        public void fire(Player player, Placeholder... placeholders) {
            this.text = StringUtil.getMessage(player, defPath + ".text", "&cERROR", placeholders)[0];
            fireQueue.add(new SimpleEntry<>(player,this));
        }
    }

    public static void check(Player player) {
        if (!ActionBar.bars.containsKey(player)) return;
        List<SimpleEntry<Player, ActionBarTypes>> queue = fireQueue.stream().filter(entry->entry.getKey().equals(player)).collect(Collectors.toList());
        HashMap<ActionBarTypes, Integer> bars = ActionBar.bars.get(player);
        queue.forEach(entry->{
            fireQueue.remove(entry);
            bars.put(entry.getValue(), 1);
        });
        ActionBar.bars.remove(player);
        StringBuilder sb = new StringBuilder();
        bars.forEach((k,v)->{
            if (v>STAY_TIME) {
                bars.remove(k);
                return;
            }
            sb.append(k.text).append(spacer);
            bars.put(k,++v);
        });
        player.sendActionBar(StringUtil.color(sb.substring(0,sb.length()-spacer.length()-1)));
        ActionBar.bars.put(player,bars);
    }
}
