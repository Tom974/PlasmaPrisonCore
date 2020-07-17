package me.fede1132.plasmaprisoncore.actionbar;

import com.gmail.fendt873.f32lib.other.Placeholder;
import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import me.fede1132.plasmaprisoncore.internal.util.StringUtil;
import org.bukkit.entity.Player;
import java.util.HashMap;

public class ActionBar {
    private static int STAY_TIME = 3;
    private static final PlasmaPrisonCore instance = PlasmaPrisonCore.getInstance();
    private static final HashMap<Player, HashMap<ActionBarTypes, Integer>> bars = new HashMap<>();
    private static final HashMap<Player, ActionBarTypes> fireQueue = new HashMap<>();
    private static final String spacer = StringUtil.color(instance.messages.getString("action-bar.spacer"));
    public enum ActionBarTypes {
        ECONOMY("");
        private final String defPath = "action-bar." + this.toString().toLowerCase();

        private String text;
        ActionBarTypes(String text) {
            this.text = text;
        }

        public boolean isEnabled() {
            return instance.messages.getOrSetDefault(defPath + ".enabled", true);
        }

        public void fire(Player player, Placeholder... placeholders) {
            this.text = StringUtil.getMessage(player, defPath + ".text", "&cERROR", placeholders)[0];
            fireQueue.put(player,this);
        }
    }

    private void check(Player player) {
        HashMap<ActionBarTypes, Integer> queue = (HashMap<ActionBarTypes, Integer>) bars.get(player).clone();
        queue.remove(player);
        StringBuilder sb = new StringBuilder();
        queue.forEach((k,v)->{
            if (v>STAY_TIME) {
                queue.remove(k);
                return;
            }
            sb.append(k.text).append(spacer);
        });
        player.sendActionBar(StringUtil.color(sb.substring(0,sb.length()-spacer.length()-1)));
    }
}
