package me.mynqme.plasmaprisoncore;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.mynqme.plasmaprisoncore.hooks.HookPapi;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import de.leonhard.storage.Yaml;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public class StringUtil {
  public static String color(String s, Placeholder... placeholders) {
    if (placeholders != null && placeholders.length > 0)
      for (Placeholder placeholder : placeholders)
        s = placeholder.replace(s);  
    s = specialTranslate(s);
    return ChatColor.translateAlternateColorCodes('&', s);
  }
  
  public static String color(Player player, String s, Placeholder... placeholders) {
    s = Placeholder.replace(s, new Placeholder[] { new Placeholder("name", player.getName()), new Placeholder("name", player
            .getName()), new Placeholder("displayname", player
            .getDisplayName()), new Placeholder("health", 
            Double.valueOf(player.getHealth())), new Placeholder("health_max", 
            Double.valueOf(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())), new Placeholder("world", player.getWorld().getName()) });
    if (placeholders != null && placeholders.length > 0)
      for (Placeholder placeholder : placeholders)
        s = placeholder.replace(s);  
    s = specialTranslate(s);
    HookPapi hookPapi = new HookPapi();
    return color(hookPapi.isEnabled() ? hookPapi.translate(player, s) : s, new Placeholder[0]);
  }
  
  public static String[] color(List<String> s, Placeholder... placeholders) {
    String[] str = new String[s.size()];
    for (int i = 0; i < str.length; i++)
      str[i] = color(s.get(i), placeholders); 
    return str;
  }
  
  public static String[] color(Player player, List<String> s) {
    String[] str = new String[s.size()];
    for (int i = 0; i < str.length; i++)
      str[i] = color(player, new String(((String)s.get(i)).getBytes(), Charsets.UTF_8), new Placeholder[0]); 
    return str;
  }
  
  public static String[] getOrSetDefault(Yaml file, String path, Object def) {
    if (!file.contains(path))
      file.set(path, def); 
    if (def instanceof String)
      return new String[] { color(new String(((String)file.getOrSetDefault(path, def)).getBytes(), Charsets.UTF_8), new Placeholder[0]) }; 
    if (def instanceof List)
      return color((List<String>)file.getOrSetDefault(path, def), new Placeholder[0]); 
    return new String[] { "" };
  }
  
  public static String[] getOrSetDefault(Player player, Yaml file, String path, Object def) {
    if (!file.contains(path))
      file.set(path, def); 
    if (def instanceof String)
      return new String[] { color(player, new String(((String)file.getOrSetDefault(path, def)).getBytes(), Charsets.UTF_8), new Placeholder[0]) }; 
    if (def instanceof List)
      return color(player, (List<String>)file.getOrSetDefault(path, def)); 
    return new String[] { "" };
  }
  
  public static String[] getOrDefault(Yaml file, String path, Object def) {
    if (!file.contains(path))
      file.set(path, def); 
    if (def instanceof String)
      return new String[] { color(new String(((String)file.getOrSetDefault(path, def)).getBytes(), Charsets.UTF_8), new Placeholder[0]) }; 
    if (def instanceof List)
      return color((List<String>)file.getOrSetDefault(path, def), new Placeholder[0]); 
    return new String[] { "" };
  }
  
  public static String[] getOrDefault(Player player, Yaml file, String path, Object def) {
    if (!file.contains(path))
      file.set(path, def); 
    if (def instanceof String)
      return new String[] { color(player, new String(((String)file.getOrSetDefault(path, def)).getBytes(), Charsets.UTF_8), new Placeholder[0]) }; 
    if (def instanceof List)
      return color(player, (List<String>)file.getOrSetDefault(path, def)); 
    return new String[] { "" };
  }
  
  public static void sendMessage(Player player, String string) {
    string = color(player, string, new Placeholder[0]);
    if (string.startsWith(MessageType.ACTION_BAR.prefix)) {
      string = string.replace(MessageType.ACTION_BAR.prefix, "");
      // (new MessageUtil.ActionBar(string)).show(player); // TODO: Fix this
      // send actionbar message
//      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(string)); // or use this?
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', string));
    } else if (string.startsWith(MessageType.TITLE.prefix)) {
      Matcher matcher = Pattern.compile("[\\[].*[]]").matcher(string);
      if (!matcher.find()) {
        player.sendMessage(color("&cError while sending this message. (Invalid message type tag)", new Placeholder[0]));
        return;
      } 
      String group = matcher.group(0);
      string = matcher.replaceAll("");
      String[] contents = string.split("[{]n[}]");
      if (contents.length < 2)
        contents = new String[] { string, "" }; 
      String[] args = group.split("\\|");
      if (args.length < 3)
        args = new String[] { (args.length >= 1) ? args[0] : "20", (args.length == 2) ? args[1] : "60", "20" }; 
      // (new MessageUtil.Title(contents[0], contents[1], 
          
      //     validInteger(args[0]) ? Integer.parseInt(args[0]) : 20, 
      //     validInteger(args[1]) ? Integer.parseInt(args[1]) : 60, 
      //     validInteger(args[2]) ? Integer.parseInt(args[2]) : 20))
      //   .show(player);
      // TODO: fix this code
    } else {
      player.sendMessage(string);
    } 
  }
  
  public static String specialTranslate(String string) {
    Pattern pattern = Pattern.compile(".*(\\[.*?])");
    Matcher matcher = pattern.matcher(string);
    if (!matcher.find())
      return string; 
    String group = matcher.group(1);
    String[] arguments = group.replace("[", "").replace("]", "").split("\\|");
    if (arguments.length == 0)
      return string; 
    if (arguments[0].equals("bar") && arguments.length > 6) {
      for (int i = 1; i < 4; ) {
        if (!validInteger(arguments[i]))
          return string; 
        i++;
      } 
      String completed = arguments[5];
      String notCompleted = arguments[6];
      string = string.replace(group, getProgressBar(Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2]), Integer.parseInt(arguments[3]), arguments[4], completed, notCompleted));
    } 
    return string;
  }
  
  public static boolean validMaterial(String s) {
    return (Material.getMaterial(s) != null);
  }
  
  public static boolean validInteger(String s) {
    try {
      Integer.parseInt(s);
      return true;
    } catch (NumberFormatException e) {
      return false;
    } 
  }
  
  public static boolean validDouble(String s) {
    try {
      Double.parseDouble(s);
      return true;
    } catch (NumberFormatException e) {
      return false;
    } 
  }
  
  private static String getProgressBar(int current, int max, int totalBars, String symbol, String completedColor, String notCompletedColor) {
    float percent = current / max;
    int progressBars = (int)(totalBars * percent);
    return Strings.repeat("" + completedColor + symbol, progressBars) + 
      Strings.repeat("" + notCompletedColor + symbol.toCharArray()[0], totalBars - progressBars);
  }
  
  public enum MessageType {
    TITLE("[title"),
    ACTION_BAR("[action-bar]");
    
    private String prefix;
    
    MessageType(String prefix) {
      this.prefix = prefix;
    }
    
    public String getPrefix() {
      return this.prefix;
    }
  }
}
