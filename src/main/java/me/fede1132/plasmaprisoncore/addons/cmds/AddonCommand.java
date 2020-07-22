package me.fede1132.plasmaprisoncore.addons.cmds;

public @interface AddonCommand {
    String name();
    String description() default "An addon command";
    String[] alias();
}
