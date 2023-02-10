package me.fede1132.plasmaprisoncore;

import org.jetbrains.annotations.NotNull;

public class Placeholder {
  private final String name;
  
  private final String replacement;
  
  public Placeholder(@NotNull String name, @NotNull Object replacement) {
    this.name = "[{]" + name + "[}]";
    this.replacement = String.valueOf(replacement);
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getReplacement() {
    return this.replacement;
  }
  
  public String replace(@NotNull String input) {
    return input.replaceAll(this.name, this.replacement);
  }
  
  public static String replace(String input, Placeholder... placeholders) {
    for (Placeholder placeholder : placeholders)
      input = placeholder.replace(input); 
    return input;
  }
}
