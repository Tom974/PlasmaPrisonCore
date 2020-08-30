package me.fede1132.plasmaprisoncore.enchant;

import me.fede1132.f32lib.other.Placeholder;
import me.fede1132.f32lib.other.StringUtil;
import me.fede1132.f32lib.shaded.nbt.NBTCompound;
import me.fede1132.f32lib.shaded.nbt.NBTItem;
import me.fede1132.plasmaprisoncore.addons.basics.enchant.EnchantEfficiency;
import me.fede1132.plasmaprisoncore.addons.basics.enchant.EnchantFortune;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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
        double curr=enchant.cost;
        for (int i=0;i<=enchant.max;i++) {
            try {
                double res = (Double) js.eval(Placeholder.replace(enchant.jsScript,
                        new Placeholder("current_level", i),
                        new Placeholder("current_cost", curr)));
                curr=res;
                enchant.costs.put(i, (int)res);
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        }
        this.registeredEnchants.put(enchant.getId(), enchant);
    }

    public boolean hasEnchant(ItemStack item, String id) {
        NBTItem nbti = new NBTItem(item);
        if (!nbti.hasKey("PlasmaPrison")) return false;
        NBTCompound plasmaPrison = nbti.getCompound("PlasmaPrison");
        if (!plasmaPrison.hasKey("enchants")) return false;
        return plasmaPrison.getCompound("enchants").hasKey(id);
    }

    public int getEnchantLevel(ItemStack item, String id) {
        if (item==null||item.getType()==Material.AIR) return 0;
        NBTItem nbti = new NBTItem(item);
        if (!nbti.hasKey("PlasmaPrison")) return 0;
        NBTCompound plasmaPrison = nbti.getCompound("PlasmaPrison");
        if (!plasmaPrison.hasKey("enchants")) return 0;
        NBTCompound enchants = plasmaPrison.getCompound("enchants");
        return enchants.hasKey(id)?enchants.getInteger(id):0;
    }

    public ItemStack enchant(ItemStack item, Enchant enchant, int level) {
        NBTItem nbti = new NBTItem(item);
        if (!nbti.hasKey("PlasmaPrison")) nbti.addCompound("PlasmaPrison");
        NBTCompound plasmaPrison = nbti.getCompound("PlasmaPrison");
        if (!plasmaPrison.hasKey("enchants")) plasmaPrison.addCompound("enchants");
        NBTCompound enchants = plasmaPrison.getCompound("enchants");
        if (level>0) enchants.setInteger(enchant.getId(), level);
        else if (enchants.hasKey(enchant.getId())) enchants.removeKey(enchant.getId());
        item = nbti.getItem();
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore()==null?new ArrayList<>():meta.getLore();
        List<String> list = new ArrayList<>(enchants.getKeys());
        Collections.sort(list);
        List<String> sublist = lore.subList(0, Math.min(list.size(), lore.size()));
        if (level<=0&&enchants.hasKey(enchant.getId())) {
            enchants.removeKey(enchant.getId());
            list=new ArrayList<>(enchants.getKeys());
        }
        sublist.clear();
        for (String s : list) {
            Enchant e1 = registeredEnchants.get(s);
            sublist.add(getText(e1.loreColor, e1.displayName, enchants.getInteger(s)));
        }
        meta.setLore(lore);
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
        return StringUtil.color("&" + loreColor + displayName + " " + level);
    }
}
