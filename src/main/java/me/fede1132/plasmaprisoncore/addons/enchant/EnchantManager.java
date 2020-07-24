package me.fede1132.plasmaprisoncore.addons.enchant;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;

public class EnchantManager {
    public final HashMap<String, Enchant> registeredEnchants = new HashMap<>();

    public boolean hasEnchant(ItemStack item, String id) {
        NBTItem nbti = new NBTItem(item);
        if (!nbti.hasKey("PlasmaPrison")) return false;
        NBTCompound plasmaPrison = nbti.getCompound("PlasmaPrison");
        if (!plasmaPrison.hasKey("enchants")) return false;
        return plasmaPrison.getStringList("enchants").stream().anyMatch(str->str.split(":")[0].equals(id));
    }

    public int getEnchantLevel(ItemStack item, String id) {
        NBTItem nbti = new NBTItem(item);
        if (!nbti.hasKey("PlasmaPrison")) return 0;
        NBTCompound plasmaPrison = nbti.getCompound("PlasmaPrison");
        if (!plasmaPrison.hasKey("enchants")) return 0;
        Optional<String> enchant = plasmaPrison.getStringList("enchants").stream().filter(str->str.split(":")[0].equals(id)).findFirst();
        return enchant.map(s -> Integer.parseInt(s.split(":")[1])).orElse(0);
    }

    public ItemStack enchant(ItemStack item, Enchant enchant, int level) {
        if (item.getEnchantments().size()==0) item.addEnchantment(new Glow(), 1);
        NBTItem nbti = new NBTItem(item);
        if (!nbti.hasKey("PlasmaPrison")) nbti.addCompound("PlasmaPrison");
        NBTCompound plasmaPrison = nbti.getCompound("PlasmaPrison");
        NBTList<String> enchants = plasmaPrison.getStringList("enchants");
        Iterator<String> iterator = enchants.stream().filter(str->str.split(":")[0].equals(enchant.getId())).iterator();
        if(iterator.hasNext()) enchants.remove(iterator.next());
        enchants.add(enchant.getId()+":"+level);
        return nbti.getItem();
    }
}
