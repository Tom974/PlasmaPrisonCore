package me.fede1132.plasmaprisoncore.internal.util.regions.shapes;

import me.fede1132.plasmaprisoncore.internal.util.regions.Region;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class CuboidRegion extends Region {
    public CuboidRegion(World world, Location pos1, Location pos2) {
        super(world, pos1, pos2, null);
    }

    @Override
    public List<Location> generate() {
        List<Location> blocks = new ArrayList<>();;
        int highX = (int) Math.max(getPos1().getX(), getPos2().getX());
        int lowX = (int) Math.min(getPos1().getX(), getPos2().getX());
        int diffX = (highX<0?Math.negateExact(highX):highX) - (lowX<0?Math.negateExact(lowX):lowX);
        int highY = getPos1().getBlockY()<0?getPos2().getBlockY():getPos2().getBlockY()<0?getPos1().getBlockY():Math.max(getPos1().getBlockY(), getPos2().getBlockY());
        int lowY = getPos1().getBlockY()<0?0:getPos2().getBlockY()<0?0:Math.min(getPos1().getBlockY(), getPos2().getBlockY());
        int diffY = highY - lowY;
        int highZ = (int) Math.max(getPos1().getZ(), getPos2().getZ());
        int lowZ = (int) Math.min(getPos1().getZ(), getPos2().getZ());
        int diffZ = (highZ<0?Math.negateExact(highZ):highZ) - (lowZ<0?Math.negateExact(lowZ):lowZ);
        for (int x=0;x<=diffX;x++) {
            for (int z=0;z<=diffZ;z++) {
                for (int y=0;y<=diffY;y++) {
                    blocks.add(new Location(getWorld(), lowX+x, lowY+y, lowZ+z));
                }
            }
        }
        return blocks;
    }

    public static CuboidRegion fromCenter(World world, Location center, int radius, boolean withY) {
        Location pos1 = center.clone().add(radius, (withY?radius:0), radius);
        Location pos2 = center.clone().subtract(radius, (withY?radius:0), radius);
        return new CuboidRegion(world, pos1, pos2);
    }
}
