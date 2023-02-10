package me.fede1132.plasmaprisoncore;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTListCompound;
import me.fede1132.plasmaprisoncore.Enchant;
import de.leonhard.storage.Yaml;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ItemBuilder implements Cloneable {
  public ItemStack item;
  
  private Player player;
  
  public String name;
  
  public String[] lore;
  
  private boolean unbreakable;
  
  private String skull;
  
  private ItemFlag[] flags;
  
  public static ItemStack getItem(Player player, Yaml file, String path, Placeholder... placeholders) {
    String material = (String)file.getOrSetDefault(path + ".material", "STONE");
    if (!StringUtil.validMaterial(material) && !material.startsWith("head|"))
      material = Material.STONE.toString(); 
    String raw = (String)file.getOrSetDefault(path + ".amount", "1");
    int amount = StringUtil.validInteger(raw) ? Integer.parseInt(raw) : 1;
    raw = (String)file.getOrDefault(path + ".data", "0");
    short data = (short)(StringUtil.validInteger(raw) ? Integer.parseInt(raw) : 0);
    ItemBuilder builder = material.startsWith("head|") ? new ItemBuilder(material, amount) : new ItemBuilder(Material.valueOf(material), amount, data);
    builder.setDisplayName(((player == null) ? StringUtil.getOrSetDefault(file, path + ".name", "&cInvalid name in config!") : StringUtil.getOrSetDefault(player, file, path + ".name", "&cInvalid name in config!"))[0]);
    builder.setLore((player == null) ? StringUtil.getOrDefault(file, path + ".lore", Collections.EMPTY_LIST) : StringUtil.getOrSetDefault(player, file, path + ".lore", Collections.EMPTY_LIST));
    builder.setPlaceholders(placeholders);
    builder.setPlayer(player);
    return builder.build();
  }
  
  private Placeholder[] placeholders = new Placeholder[0];
  
  private Enchant[] enchants = new Enchant[0];
  
  public ItemBuilder(String material) {
    head(material, 1);
  }
  
  public ItemBuilder(String material, int amount) {
    head(material, amount);
  }
  
  public ItemBuilder(Material material) {
    this.item = new ItemStack(material);
  }
  
  public ItemBuilder(Material material, int amount) {
    this.item = new ItemStack(material, amount);
  }
  
  public ItemBuilder(Material material, int amount, short data) {
    this.item = new ItemStack(material, amount, data);
  }
  
  public ItemBuilder(Material material, short data) {
    this.item = new ItemStack(material, 1, data);
  }
  
  public ItemBuilder setPlayer(Player player) {
    this.player = player;
    return this;
  }
  
  public ItemBuilder setSkull(boolean b) {
    if (b && (this.item == null || this.item.getType() != Material.SKULL_ITEM))
      this.item = new ItemStack(Material.SKULL_ITEM, (this.item == null) ? 1 : this.item.getAmount(), (short)3); 
    return this;
  }
  
  public ItemBuilder setPlaceholders(Placeholder... placeholders) {
    this.placeholders = placeholders;
    return this;
  }
  
  public ItemBuilder setDisplayName(String name) {
    this.name = name;
    return this;
  }
  
  public ItemBuilder setLore(String... lore) {
    this.lore = lore;
    return this;
  }
  
  public ItemBuilder setUnbreakable(boolean set) {
    this.unbreakable = set;
    return this;
  }
  
  public ItemBuilder addEnchants(Enchant... enchants) {
    this.enchants = enchants;
    return this;
  }
  
  public ItemBuilder addFlags(ItemFlag... flags) {
    this.flags = flags;
    return this;
  }
  
  public ItemBuilder setSkullOwner(String owner) {
    this.skull = owner;
    return this;
  }
  
  public ItemStack build() {
    for (int i = 0; i < this.lore.length; i++) {
      String str = this.lore[i];
      this.lore[i] = (this.player == null) ? StringUtil.color(str, this.placeholders) : StringUtil.color(this.player, str, this.placeholders);
    } 
    this.name = (this.player == null) ? StringUtil.color(this.name, this.placeholders) : StringUtil.color(this.player, this.name, this.placeholders);
    ItemMeta meta = this.item.getItemMeta();
    if (this.name != null)
      meta.setDisplayName(this.name); 
    if (this.lore != null)
      meta.setLore(Arrays.asList(this.lore));
      meta.setUnbreakable(this.unbreakable);
    if (this.skull != null)
      this.skull = (this.player == null) ? StringUtil.color(this.skull, this.placeholders) : StringUtil.color(this.player, this.skull, this.placeholders); 
    if (this.flags != null)
      meta.addItemFlags(this.flags); 
    this.item.setItemMeta(meta);
    for (Enchant enchant : this.enchants)
      this.item.addUnsafeEnchantment(enchant.getEnchant(), enchant.getLevel()); 
    if (this.item.getType() == Material.SKULL_ITEM && this.skull != null && this.skull.length() > 16) {
      NBTItem nbti = new NBTItem(this.item);
      NBTCompound skull = nbti.addCompound("SkullOwner");
      skull.setString("Name", UUID.randomUUID().toString().replace("-", ""));
      skull.setString("Id", UUID.randomUUID().toString());
      NBTListCompound texture = skull.addCompound("Properties").getCompoundList("textures").addCompound();
      texture.setString("Value", this.skull);
      this.item = nbti.getItem();
    } 
    return this.item;
  }
  
  public ItemBuilder clone() {
    try {
      return (ItemBuilder)super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
      return null;
    } 
  }
  
  private void head(String material, int amount) {
    if (material.startsWith("head|")) {
        material = material.replace("head|", "");
        this.skull = material;
        setSkull(true);
        this.item.setAmount(amount);
    } else {
        this.item = new ItemStack(StringUtil.validMaterial(material) ? Material.getMaterial(material) : Material.STONE, (amount > 0) ? amount : 1);
    } 
  }
  
  public String toString() {
    return (new NBTItem(this.item)).toString();
  }
}
