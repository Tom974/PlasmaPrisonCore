package me.mynqme.plasmaprisoncore.internal.util.regions;

import org.bukkit.Location;

public class RegionUtil {
    public static boolean contains(Location pos1, Location pos2, Location loc) {
        boolean x = Math.max(pos1.getX(), pos2.getX()) >= loc.getX() && Math.min(pos1.getX(), pos2.getX()) <= loc.getX();
        boolean y = Math.max(pos1.getY(), pos2.getY()) >= loc.getY() && Math.min(pos1.getY(), pos2.getY()) <= loc.getY();
        boolean z = Math.max(pos1.getZ(), pos2.getZ()) >= loc.getZ() && Math.min(pos1.getZ(), pos2.getZ()) <= loc.getZ();
        return x&&y&&z;
    }
}
