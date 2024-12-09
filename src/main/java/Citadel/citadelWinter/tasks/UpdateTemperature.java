package Citadel.citadelWinter.tasks;

import Citadel.citadelWinter.CitadelWinter;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Marker;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static Citadel.citadelWinter.classes.Temperature.addPlayerTemperature;
import static Citadel.citadelWinter.classes.Temperature.distanceDiscrete;
import static Citadel.citadelWinter.classes.TemperatureData.*;

public class UpdateTemperature extends BukkitRunnable {
    private static final Server server = CitadelWinter.getInstance().getServer();
    @Override
    public void run() {
        for (Player player : server.getOnlinePlayers()){
            if (player.isDead()) continue;

            if (heatBlocksData.containsKey(player.getInventory().getItemInMainHand().getType().name())){
                addPlayerTemperature(player, heatByItem);
            }
            if (heatBlocksData.containsKey(player.getInventory().getItemInOffHand().getType().name())){
                addPlayerTemperature(player, heatByItem);
            }

            Set<String> nearHeatBlocks = new HashSet<>();
            for (Entity entity : player.getLocation().getNearbyEntities(maxHeatBlockRadius, maxHeatBlockRadius, maxHeatBlockRadius)){
                if (!entity.getPersistentDataContainer().has(heatBlockTypeKey)) continue;
                String blockType = entity.getPersistentDataContainer().get(heatBlockTypeKey, PersistentDataType.STRING);
                if (nearHeatBlocks.contains(blockType)) continue;
                if (distanceDiscrete(player.getLocation(), entity.getLocation()) >= heatBlocksData.get(blockType).radius) continue;
                nearHeatBlocks.add(blockType);
            }
            for (String nearHeatBlock : nearHeatBlocks){
                addPlayerTemperature(player, heatBlocksData.get(nearHeatBlock).heat);
            }

            if (player.getLocation().getY() < groundHeatLowest) continue;
            float coldDelta = coldPerPeriod;
            for (ItemStack armor : player.getInventory().getArmorContents()){
                if (armor != null){
                    if (armor.getPersistentDataContainer().has(insulatedArmorKey)){
                        coldDelta *= insulatedArmorMultiplier;
                    }
                }
            }
            if (player.getLocation().getY() < groundHeatHighest) {
                coldDelta *= ((float) player.getLocation().getY() - groundHeatLowest) / groundHeatAmplitude;
            }
            addPlayerTemperature(player, coldDelta);
        }
    }
}
