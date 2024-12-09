package Citadel.citadelWinter.tasks;

import Citadel.citadelWinter.CitadelWinter;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Marker;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import static Citadel.citadelWinter.classes.TemperatureData.*;

public class RepairMarkers extends BukkitRunnable {
    private static final World world = CitadelWinter.getInstance().getServer().getWorld("world");
    @Override
    public void run() {
        for (Entity entity : world.getEntitiesByClasses(Marker.class, Interaction.class)){
            if (!entity.getPersistentDataContainer().has(heatBlockTypeKey)) continue;
            if (!entity.getPersistentDataContainer().getOrDefault(heatBlockTypeKey, PersistentDataType.STRING, "CAMPFIRE")
                    .equalsIgnoreCase(world.getBlockAt(entity.getLocation()).getType().name())){
                entity.remove();
                CitadelWinter.getInstance().getComponentLogger().warn(String.format(
                        "Wrong %s at %s %s %s",
                        (entity.getType() == EntityType.MARKER ? "marker" : "interaction"),
                        entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ()
                ));
//                if (world.getBlockAt(343, 79, 151).getType() == Material.LECTERN){
//                    Lectern lectern = (Lectern) world.getBlockAt(343, 79, 151);
//                    ItemStack book = lectern.getInventory().getItem(0);
//                    CitadelWinter.getInstance().getComponentLogger().info(book.getType().name());
//                }
            }
        }
    }
}
