package net.mutinies.arcadecore.game.map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class VoidGenerator extends ChunkGenerator {
    @Override
    public byte[] generate(World world, Random random, int x, int z) {
        return new byte[4096];
    }
    
    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }
    
    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Collections.emptyList();
    }
    
    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0, 64, 0);
    }
}
