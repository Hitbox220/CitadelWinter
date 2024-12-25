package Citadel.citadelWinter.tasks;

import Citadel.citadelWinter.CitadelWinter;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

import static Citadel.citadelWinter.classes.Blizzard.calculateChunkBlizzard;
import static Citadel.citadelWinter.classes.Temperature.*;
import static Citadel.citadelWinter.classes.Temperature.addEntityTemperature;
import static Citadel.citadelWinter.classes.TemperatureData.*;

public class UpdateTemperature extends BukkitRunnable {
    private static final Server server = CitadelWinter.getInstance().getServer();
    private static final World overWorld = CitadelWinter.getInstance().getServer().getWorld("world");

    @Override
    public void run() {
        assert overWorld != null;
        for (Player player : server.getOnlinePlayers()){
            if (player.isDead()) continue;
            updateTemperaturePlayer(player);
        }
        for (Entity entity : overWorld.getEntitiesByClasses(Animals.class, Villager.class)){
            updateTemperatureEntity(entity);
        }
        updateStorm();
    }

    private void updateStorm(){
        assert overWorld != null;
        if (overWorld.getPersistentDataContainer().has(stormKey)){
            int duration = overWorld.getPersistentDataContainer().getOrDefault(stormKey, PersistentDataType.INTEGER, 0);
            CitadelWinter.getInstance().getComponentLogger().info(String.valueOf(duration));
            if (duration <= 0){
                overWorld.getPersistentDataContainer().remove(stormKey);
                return;
            }
            overWorld.getPersistentDataContainer().set(stormKey, PersistentDataType.INTEGER, duration - updateTemperatureTickRate);
        }
    }

    private void updateTemperaturePlayer(Player player){
        if (player.getWorld().getName().equals("world_nether")) {
            addEntityTemperature(player, netherHeat);
            return;
        }
        addEntityTemperature(player, calculateNearHeat(player.getLocation()));
        addEntityTemperature(player, calculateHeatPlayer(player));
        addEntityTemperature(player, calculateCold(player.getLocation()) * calculateArmorProtection(player));
    }

    private void updateTemperatureEntity(Entity entity){
        addEntityTemperature(entity, calculateNearHeat(entity.getLocation()));
        addEntityTemperature(entity, calculateCold(entity.getLocation()));
    }

    public static float calculateCold(Location location){
        float coldDelta = coldPerPeriod;
        BlizzardData blizzardData = calculateChunkBlizzard(location.getChunk());
        coldDelta *= blizzardData.cold;
        assert overWorld != null;
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

    public static float calculateNearHeat(Location from){
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
