package Citadel.citadelWinter.tasks;

import Citadel.citadelWinter.CitadelWinter;
import io.papermc.paper.registry.keys.BlockTypeKeys;
import org.bukkit.World;
import org.bukkit.block.Campfire;
import org.bukkit.block.data.Lightable;
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
        assert world != null;
        for (Entity entity : world.getEntitiesByClasses(Marker.class, Interaction.class)){
            if (!entity.getPersistentDataContainer().has(heatBlockTypeKey)) continue;
            String entityName = entity.getPersistentDataContainer().get(heatBlockTypeKey, PersistentDataType.STRING);
            assert entityName != null;
            if (!entityName.equalsIgnoreCase(world.getBlockAt(entity.getLocation()).getType().name())){
                entity.remove();
                CitadelWinter.getInstance().getComponentLogger().warn(String.format(
                        "Wrong %s at %s %s %s",
                        (entity.getType() == EntityType.MARKER ? "marker" : "interaction"),
                        entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ()
                ));
            } else if (entityName.contains("CAMPFIRE")){
//                Campfire campfire = (Campfire) world.getBlockAt(entity.getLocation());
                Lightable lightable = (Lightable) world.getBlockAt(entity.getLocation()).getBlockData();
                if (!lightable.isLit()){
                    entity.remove();
                }
            }
        }
    }
}
