package me.mynqme.plasmaprisoncore.internal.util.regions;

import net.minecraft.server.v1_12_R1.PacketPlayOutMapChunk;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_12_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Region implements Iterable<Location>, Closeable {
    private final World world;
    private final Location pos1;
    private final Location pos2;
    @NotNull
    private final List<Location> blocks;
    @NotNull
    private final List<CraftChunk> chunks;
    private boolean closed;
    public Region(World world, Location pos1, Location pos2, List<Chunk> chunks) {
        this.world = world;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.blocks = generate();
        if (chunks==null) {
            this.chunks = new ArrayList<>();
            blocks.stream().filter(this::containsChunk).forEach(chunk->chunks.add((CraftChunk) world.getChunkAt(chunk)));
        } else {
            this.chunks = chunks.stream().map(chunk->(CraftChunk) chunk).collect(Collectors.toList());
        }
    }

    public abstract List<Location> generate();

    public void updateChunks(@NotNull final Material material) {
        catchClosed();
        chunks.forEach(chunk->{
            world.getPlayers().stream().filter(player->distance(chunk.getX(), chunk.getZ(),
                            player.getLocation().getBlockX(), player.getLocation().getBlockZ()))
            .forEach(player-> ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutMapChunk(chunk.getHandle(), 20)));
        });
    }

    public void merge(Region toMerge) {
        catchClosed();
        toMerge.stream().filter(block->!blocks.contains(block)).forEach(blocks::add);
    }

    public World getWorld() {
        catchClosed();
        return world;
    }

    public Location getPos1() {
        catchClosed();
        return pos1;
    }

    public Location getPos2() {
        catchClosed();
        return pos2;
    }

    public List<CraftChunk> getChunks() {
        catchClosed();
        return chunks;
    }

    @NotNull
    @Override
    public Iterator<Location> iterator() {
        catchClosed();
        return this.blocks.iterator();
    }

    public Stream<Location> stream() {
        catchClosed();
        return this.blocks.stream();
    }

    public List<Location> getBlocks() {
        catchClosed();
        return blocks;
    }

    private boolean containsChunk(Location loc) {
        catchClosed();
        return chunks.contains((CraftChunk)world.getChunkAt(loc));
    }

    private boolean distance(int x, int z, int x1, int z1) {
        catchClosed();
        int dist = Bukkit.getServer().getViewDistance() * 16;
        return Math.max(x, x1) - Math.min(x, x1) <= dist && Math.max(z, z1) - Math.min(z, z1) <= dist;
    }

    private void catchClosed() {
        if (closed) throw new NullPointerException("Actions made on a Region class closed.");
    }

    @Override
    public void close() {
        chunks.clear();
        blocks.clear();
        closed=true;
    }
}
