package me.fede1132.plasmaprisoncore.internal.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HookPapi extends PlaceholderExpansion {
    private static HookPapi instance;
    public List<PapiPlaceholder> placeholders = new ArrayList<>();
    public HookPapi() {
        instance = this;
    }

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
    public String onPlaceholderRequest(Player player, String params) {
        if (params.length()==0) return "";
        return placeholders.stream().filter(placeholder->params.startsWith(placeholder.getParam())).limit(1).map(placeholder->
                placeholder.onRequest(player, params.replace(placeholder.getParam()+"_", "").split("_"))).collect(Collectors.joining(", "));
    }

    public static HookPapi inst() {
        return instance;
    }
}
