package net.mutinies.arcadecore.util;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.IBlockData;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class BuildUtil {
    public static void setNMSBlock(Block block, Material material, byte data, boolean physics) {
        World world = ((CraftWorld)block.getWorld()).getHandle();
        BlockPosition position = new BlockPosition(block.getX(), block.getY(), block.getZ());
        Chunk chunk = world.getChunkAt(block.getX() >> 4, block.getZ() >> 4);
        int idAndData = material.getId() + (data << 12);
        IBlockData blockData = net.minecraft.server.v1_8_R3.Block.getByCombinedId(idAndData);
        if (physics) {
            world.setTypeAndData(position, blockData, 3);
        } else {
            world.setTypeAndData(position, blockData, 2);
        }
//        chunk.a(position, blockData);
    }
}
