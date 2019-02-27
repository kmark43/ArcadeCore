package net.mutinies.arcadecore.util;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;

public class EntityUtil {
    public static void preventCollisions(ArmorStand armorStand) {
        Location l = armorStand.getLocation();
        ((CraftArmorStand) armorStand).getHandle().a(new AxisAlignedBB(l.getX(), l.getY(), l.getZ(), l.getX(), l.getY(), l.getZ()));
    }
}
