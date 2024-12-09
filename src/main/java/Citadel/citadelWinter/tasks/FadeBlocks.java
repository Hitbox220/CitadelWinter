package Citadel.citadelWinter.tasks;

import Citadel.citadelWinter.CitadelWinter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Lightable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Marker;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static Citadel.citadelWinter.classes.Temperature.addPlayerTemperature;
import static Citadel.citadelWinter.classes.TemperatureData.*;

public class FadeBlocks extends BukkitRunnable {
    private static final Server server = CitadelWinter.getInstance().getServer();
    private static final World overWorld = CitadelWinter.getInstance().getServer().getWorld("world");
    @Override
    public void run() {
        for (Entity heatBlockMarker : Objects.requireNonNull(overWorld).getEntitiesByClasses(Marker.class, Interaction.class)){
            if (!heatBlockMarker.getPersistentDataContainer().has(heatBlockTypeKey)) continue;
            if (!heatBlockMarker.getPersistentDataContainer().has(heatBlockTicksKey)) continue;
            if (heatBlockMarker.getPersistentDataContainer().get(heatBlockTicksKey, PersistentDataType.INTEGER) <= 0){
                Block heatBlock = overWorld.getBlockAt(heatBlockMarker.getLocation());
                if (heatBlock.getType().name().contains("CAMPFIRE")){
                    Lightable blockData = (Lightable) heatBlock.getBlockData();
                    blockData.setLit(false);
                    heatBlock.setBlockData(blockData);
                } else {
                    heatBlock.setType(Material.AIR);
                }
                heatBlock.getWorld().playSound(heatBlock.getLocation(), Sound.EVENT_MOB_EFFECT_RAID_OMEN, 0.1F, 0.1F);
                heatBlock.getWorld().spawnParticle(Particle.TRIAL_OMEN, heatBlock.getLocation().add(0.5, 0.5, 0.5), 10);
                heatBlockMarker.remove();
            } else {
                heatBlockMarker.getPersistentDataContainer().set(heatBlockTicksKey, PersistentDataType.INTEGER,
                        heatBlockMarker.getPersistentDataContainer().get(heatBlockTicksKey, PersistentDataType.INTEGER) - blocksFadeTickRate * 40);
            }
        }
    }
}
