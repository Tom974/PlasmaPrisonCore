package me.mynqme.plasmaprisoncore.enchant;

import me.mynqme.plasmaprisoncore.Placeholder;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import me.mynqme.plasmaprisoncore.addons.basics.enchant.EnchantEfficiency;
import me.mynqme.plasmaprisoncore.addons.basics.enchant.EnchantFortune;
// import me.mynqme.plasmaprisoncore.internal.hooks.PapiPlaceholder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;

public class EnchantManager {
    private static EnchantManager inst;
    private final ScriptEngine js = new ScriptEngineManager().getEngineByName("javascript");
    public final HashMap<String, Enchant> registeredEnchants = new HashMap<>();
    public EnchantManager() {
        inst = this;
    }

    public void register(Enchant enchant) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"(!) Registering enchant "+enchant.getId()+"...");
        long curr=enchant.cost;
        long base=enchant.cost;
        for (int i=0;i<=enchant.max;i++) {
            try {
                Object res = js.eval(Placeholder.replace(enchant.jsScript, new Placeholder("current_level", i), new Placeholder("current_cost", curr), new Placeholder("base", base)));
                long value = res instanceof Long ? (long) res : (res instanceof Double ? Math.round((Double) res) : (Integer) res);
                curr=value;
                enchant.costs.put(i, value);
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"(!) Adding enchant "+enchant.getId()+"...");
        this.registeredEnchants.put(enchant.getId(), enchant);
        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_GREEN+"(!) Done Registering enchant "+enchant.getId()+"...");
    }

    public boolean hasEnchant(ItemStack item, String id) {
        NBTItem nbti = new NBTItem(item);
        if (!nbti.hasTag("PlasmaPrison")) return false;
        NBTCompound plasmaPrison = nbti.getCompound("PlasmaPrison");
        if (!plasmaPrison.hasTag("enchants")) return false;
        return plasmaPrison.getCompound("enchants").hasTag(id);
    }

    public NBTCompound getEnchantCompound(ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        if (!nbti.hasTag("PlasmaPrison")) return nbti.addCompound("PlasmaPrison");
        NBTCompound plasmaPrison = nbti.getCompound("PlasmaPrison");
        if (!plasmaPrison.hasTag("enchants")) plasmaPrison.addCompound("enchants");
        return plasmaPrison.getCompound("enchants");
    }

    public int getEnchantLevel(ItemStack item, String id) {
        if (item==null||item.getType()==Material.AIR) return 0;
        NBTItem nbti = new NBTItem(item);
        if (!nbti.hasTag("PlasmaPrison")) return 0;
        NBTCompound plasmaPrison = nbti.getCompound("PlasmaPrison");
        if (!plasmaPrison.hasTag("enchants")) return 0;
        NBTCompound enchants = plasmaPrison.getCompound("enchants");
        return enchants.hasTag(id)?enchants.getInteger(id):0;
    }

    public ItemStack removeEnchant(ItemStack item, Enchant enchant) {
        NBTItem nbti = new NBTItem(item);
        if (!nbti.hasTag("PlasmaPrison")) nbti.addCompound("PlasmaPrison");
        NBTCompound plasmaPrison = nbti.getCompound("PlasmaPrison");
        if (!plasmaPrison.hasTag("enchants")) plasmaPrison.addCompound("enchants");
        NBTCompound enchants = plasmaPrison.getCompound("enchants");
        enchants.setInteger(enchant.getId(), 0);
        if (enchants.hasTag(enchant.getId())) enchants.removeKey(enchant.getId());
        item = nbti.getItem();
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore()==null?new ArrayList<>():meta.getLore();
        List<String> list = new ArrayList<>(enchants.getKeys());
        Collections.sort(list);
        if (enchants.hasTag(enchant.getId())) {
            enchants.removeKey(enchant.getId());
            list=new ArrayList<>(enchants.getKeys());
        }
        meta.setLore(lore);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        if (item.getEnchantments().size()==0) {
            item.addEnchantment(Enchantment.DURABILITY, 1);
        }

        return item;
    }

    public ItemStack enchant(ItemStack item, Enchant enchant, int level, Player player) {
        NBTItem nbt = new NBTItem(item);
        if (!nbt.hasTag("PlasmaPrison")) nbt.addCompound("PlasmaPrison");
        NBTCompound plasmaPrison = nbt.getCompound("PlasmaPrison");
        if (!plasmaPrison.hasTag("enchants")) plasmaPrison.addCompound("enchants");
        NBTCompound enchants = plasmaPrison.getCompound("enchants");
        if (level > 0) enchants.setInteger(enchant.getId(), Integer.valueOf(level));
        else if (enchants.hasTag(enchant.getId())) enchants.removeKey(enchant.getId());
        item = nbt.getItem();
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
        
        List<String> list = new ArrayList<>(enchants.getKeys());
        Collections.sort(list);
        List<String> sublist = lore.subList(0, Math.min(list.size(), lore.size()));
        if (level<=0&&enchants.hasTag(enchant.getId())) {
            enchants.removeKey(enchant.getId());
            list=new ArrayList<>(enchants.getKeys());
        }
        sublist.clear();
        for (String s : list) {
            Enchant e1 = registeredEnchants.get(s);
            sublist.add(getText(e1.loreColor, e1.displayName, enchants.getInteger(s)));
        }

        sublist.add(0, ChatColor.translateAlternateColorCodes('&', "&5&lEnchantments"));
        sublist.add(0, ChatColor.translateAlternateColorCodes('&', "&m"));
        meta.setLore(sublist);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        if (item.getEnchantments().size()==0) {
            item.addEnchantment(Enchantment.DURABILITY, 1);
        }
        if (enchant instanceof EnchantEfficiency) {
            item.addUnsafeEnchantment(Enchantment.DIG_SPEED, level);
        } else if (enchant instanceof EnchantFortune) {
            item.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, level);
        }

        return item;
    }

    public static EnchantManager getInst() {
        return inst;
    }

    private String getText(String loreColor, String displayName, int level) {
        // return ChatColor.translateAlternateColorCodes('&', loreColor + displayName + " " + level);
        return ChatColor.translateAlternateColorCodes('&', loreColor.replace("%name%", displayName).replace("%level%", level + ""));
    }
}
