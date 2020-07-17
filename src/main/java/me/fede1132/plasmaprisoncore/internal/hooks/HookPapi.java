package me.fede1132.plasmaprisoncore.internal.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class HookPapi extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "plasmaprison";
    }

    @Override
    public String getAuthor() {
        return "PlasmaDevTeam";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        String[] args = params.split("_");
        if (args.length==0) return "";
        switch (args[0].toLowerCase()) {
            case "": {
                break;
            }
        }
        return "";
    }
}
