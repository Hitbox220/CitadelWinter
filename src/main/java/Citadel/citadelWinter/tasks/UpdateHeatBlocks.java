package Citadel.citadelWinter.tasks;

import Citadel.citadelWinter.CitadelWinter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.block.data.Lightable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Marker;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

import static Citadel.citadelWinter.classes.Blizzard.calculateChunkBlizzard;
import static Citadel.citadelWinter.classes.TemperatureData.*;

public class UpdateHeatBlocks extends BukkitRunnable {
    private static final Server server = CitadelWinter.getInstance().getServer();
    private static final World overWorld = CitadelWinter.getInstance().getServer().getWorld("world");
    @Override
    public void run() {
        assert overWorld != null;
        for (Entity entity : overWorld.getEntitiesByClasses(Marker.class, Interaction.class)){
            if (!entity.getPersistentDataContainer().has(heatBlockTypeKey)) continue;
            if (repairMarkers(entity)){
                fadeBlock(entity);
                updateFurnace(entity);
            }
        }
    }

    private static void updateFurnace(Entity entity){
        if (!entity.getPersistentDataContainer().has(furnaceOnKey)) return;
        Lightable lightable = (Lightable) entity.getWorld().getBlockAt(entity.getLocation()).getBlockData();
        if (lightable.isLit()){
            entity.getPersistentDataContainer().set(furnaceOnKey, PersistentDataType.BOOLEAN, true);
        } else {
            entity.getPersistentDataContainer().set(furnaceOnKey, PersistentDataType.BOOLEAN, false);
        }
        Furnace furnace = (Furnace) entity.getWorld().getBlockState(entity.getLocation());
        FurnaceInventory inventory = furnace.getInventory();
        if (inventory.getSmelting() == null && inventory.getFuel() != null && inventory.getResult() == null && furnace.getBurnTime() <= 1){
            ItemStack phantomSmelting;
            if (!entity.getPersistentDataContainer().getOrDefault(heatBlockTypeKey, PersistentDataType.STRING, "FURNACE").equals("BLAST_FURNACE")){
                phantomSmelting = new ItemStack(Material.BEEF);
            } else {
                phantomSmelting = new ItemStack(Material.IRON_ORE);
            }
            ItemMeta phantomMeta = phantomSmelting.getItemMeta();
            phantomMeta.getPersistentDataContainer().set(phantomSmeltingKey, PersistentDataType.BOOLEAN, true);
            phantomSmelting.setItemMeta(phantomMeta);
            inventory.setSmelting(phantomSmelting);
            CitadelWinter.getInstance().getServer().getScheduler().runTaskLater(CitadelWinter.getInstance(), () -> {
                inventory.setSmelting(null);
            }, 1);
        }
    }

    private static void fadeBlock(Entity entity){
        if (!entity.getPersistentDataContainer().has(heatBlockTypeKey)) return;
        if (!entity.getPersistentDataContainer().has(heatBlockTicksKey)) return;
        if (entity.getPersistentDataContainer().getOrDefault(heatBlockTicksKey, PersistentDataType.INTEGER, 0) <= 0){
            Block heatBlock = entity.getWorld().getBlockAt(entity.getLocation());
            if (heatBlock.getType().name().contains("CAMPFIRE")){
                Lightable blockData = (Lightable) heatBlock.getBlockData();
                blockData.setLit(false);
                heatBlock.setBlockData(blockData);
            } else if (heatBlock.getType().name().contains("TORCH")) {
                heatBlock.setType(Material.AIR);
            }
            heatBlock.getWorld().playSound(heatBlock.getLocation(), Sound.EVENT_MOB_EFFECT_RAID_OMEN, 0.1F, 0.1F);
            heatBlock.getWorld().spawnParticle(Particle.TRIAL_OMEN, heatBlock.getLocation().add(0.5, 0.5, 0.5), 10);
            entity.remove();
        } else {
            int ticks = entity.getPersistentDataContainer().getOrDefault(heatBlockTicksKey, PersistentDataType.INTEGER, 0);
            BlizzardData blizzardData = calculateChunkBlizzard(entity.getLocation().getChunk());
            ticks -= blocksFadeTickRate * blizzardData.fade;
            assert overWorld != null;
            entity.getPersistentDataContainer().set(heatBlockTicksKey, PersistentDataType.INTEGER, ticks);
        }
    }

    private static boolean repairMarkers(Entity entity){
        String entityName = entity.getPersistentDataContainer().get(heatBlockTypeKey, PersistentDataType.STRING);
        assert entityName != null;
        if (!entityName.equalsIgnoreCase(entity.getWorld().getBlockAt(entity.getLocation()).getType().name())){
            entity.remove();
            return false;
        } else if (entityName.contains("CAMPFIRE")){
            Lightable lightable = (Lightable) entity.getWorld().getBlockAt(entity.getLocation()).getBlockData();
            if (!lightable.isLit()){
                entity.remove();
                return false;
            }
        }
        return true;
    }
}
