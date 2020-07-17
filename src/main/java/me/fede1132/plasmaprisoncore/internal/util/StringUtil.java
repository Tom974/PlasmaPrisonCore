package me.fede1132.plasmaprisoncore.internal.util;

import com.gmail.fendt873.f32lib.other.Placeholder;
import com.google.common.base.Charsets;
import me.clip.placeholderapi.PlaceholderAPI;
import me.fede1132.plasmaprisoncore.PlasmaPrisonCore;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class StringUtil {
    private static final PlasmaPrisonCore instance = PlasmaPrisonCore.getInstance();
    public static String[] getMessage(Player player, String path, Object def, Placeholder... placeholders) {
        Object got = instance.messages.getOrSetDefault(path, def);
        String[] out = null;
        if (got instanceof String) {
            out = new String[]{String.valueOf(got)};
        } else if (got instanceof List) {
            try {
                instance.messages.set(path,def);
                out = ((List<String>) got).toArray(new String[0]);
            } catch (ClassCastException | IllegalArgumentException e) {
                out = null;
            }
        }
        if (out==null) return new String[]{ChatColor.RED+"An error occurred. Contact an admin."};
        if (placeholders.length>0) {
            for (Placeholder placeholder:placeholders)
                for (int i=0;i<out.length;i++)
                    out[i] = placeholder.replace(out[i]);
        }
         for (int i=0;i<out.length;i++) {
             if (placeholders.length>0) {
                 for (Placeholder placeholder : placeholders) out[i] = placeholder.replace(out[i]);
             }
             out[i] = ChatColor.translateAlternateColorCodes('&',
                     new String(PlaceholderAPI.setPlaceholders(player, out[i]).getBytes(), Charsets.UTF_8));
         }
         return out;
    }

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&',s);
    }
}
