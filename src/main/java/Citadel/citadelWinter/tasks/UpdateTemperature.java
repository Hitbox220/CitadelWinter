package Citadel.citadelWinter.tasks;

import Citadel.citadelWinter.CitadelWinter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Marker;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static Citadel.citadelWinter.classes.Blizzard.calculateChunkBlizzard;
import static Citadel.citadelWinter.classes.Temperature.*;
import static Citadel.citadelWinter.classes.Temperature.addPlayerTemperature;
import static Citadel.citadelWinter.classes.TemperatureData.*;

public class UpdateTemperature extends BukkitRunnable {
    private static final Server server = CitadelWinter.getInstance().getServer();
    private static final World world = CitadelWinter.getInstance().getServer().getWorld("world");

    @Override
    public void run() {
        assert world != null;
        for (Player player : server.getOnlinePlayers()){
            if (player.isDead()) continue;
            updateTemperaturePlayer(player);
        }
    }

    private void updateTemperaturePlayer(Player player){
        if (player.getWorld().getName().equals("world_nether")) {
            addPlayerTemperature(player, netherHeat);
            return;
        }
        addPlayerTemperature(player, calculateNearHeat(player.getLocation()));
        addPlayerTemperature(player, calculateHeatPlayer(player));
        addPlayerTemperature(player, calculateCold(player.getLocation()) * calculateArmorProtection(player));
    }

    private float calculateCold(Location location){
        float coldDelta = coldPerPeriod;
        int blizzardType = calculateChunkBlizzard(location.getChunk());
        coldDelta *= blizzardsData[blizzardType].cold;
        return coldDelta;
    }

    private float calculateHeatPlayer(Player player){
        float finalHeat = 0;
        if (heatBlocksData.containsKey(player.getInventory().getItemInMainHand().getType().name())){
            finalHeat += heatByItem;
        }
        if (heatBlocksData.containsKey(player.getInventory().getItemInOffHand().getType().name())){
            finalHeat += heatByItem;
        }
        return finalHeat;
    }

    private float calculateArmorProtection(Player player){
        float finalProtection = 1;
        for (ItemStack armor : player.getInventory().getArmorContents()){
            if (armor != null){
                if (armor.getPersistentDataContainer().has(insulatedArmorKey)){
                    finalProtection *= insulatedArmorMultiplier;
                }
            }
        }
        return finalProtection;
    }

    private float calculateNearHeat(Location from){
        Set<String> nearHeatBlocks = new HashSet<>();
        float finalHeat = 0;
        for (Entity entity : from.getNearbyEntities(maxHeatBlockRadius, maxHeatBlockRadius, maxHeatBlockRadius)){
            if (!entity.getPersistentDataContainer().has(heatBlockTypeKey)) continue;
            String blockType = entity.getPersistentDataContainer().get(heatBlockTypeKey, PersistentDataType.STRING);
            assert blockType != null;
            if (blockType.contains("FURNACE") || blockType.equals("SMOKER")){
                if (!entity.getPersistentDataContainer().getOrDefault(furnaceOnKey, PersistentDataType.BOOLEAN, false)){
                    continue;
                }
            }
            if (nearHeatBlocks.contains(blockType)) continue;
            if (getMaximumDistanceAxis(from, entity.getLocation()) >= heatBlocksData.get(blockType).radius) continue;
            nearHeatBlocks.add(blockType);
        }
        for (String nearHeatBlock : nearHeatBlocks){
            finalHeat += heatBlocksData.get(nearHeatBlock).heat;
        }
        return finalHeat;
    }
}
