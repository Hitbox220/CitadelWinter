package Citadel.citadelWinter.events;

import Citadel.citadelWinter.CitadelWinter;
import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Marker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import static Citadel.citadelWinter.classes.Blizzard.calculateChunkBlizzard;
import static Citadel.citadelWinter.classes.Temperature.getBlockInteraction;
import static Citadel.citadelWinter.classes.Temperature.getBlockMarker;
import static Citadel.citadelWinter.classes.TemperatureData.*;
import static Citadel.citadelWinter.tasks.UpdateTemperature.calculateCold;
import static Citadel.citadelWinter.tasks.UpdateTemperature.calculateNearHeat;

public class WinterBlockEvents extends AbstractEvent{
    @EventHandler
    public void onBlockPlaceEvent (BlockPlaceEvent event){
        if (!event.getBlockPlaced().getWorld().getName().equals("world")) return;
        if (event.isCancelled()) return;
        Block block = event.getBlockPlaced();
        String blockName = block.getType().name();
//        CitadelWinter.getInstance().getComponentLogger().info(blockName);
        for (String heatBlockName : heatBlocksData.keySet()){
//            CitadelWinter.getInstance().getComponentLogger().info(heatBlockName);
            if (blockName.equals(heatBlockName)){
                Entity entity;
                if (heatBlockName.contains("CAMPFIRE")){
                    entity = block.getWorld().spawn(block.getLocation().add(0.5, campfireInterOffset, 0.5), Interaction.class);
                    ((Interaction) entity).setInteractionHeight(1 - campfireInterOffset);
                    ((Interaction) entity).setResponsive(true);
                } else {
                    entity = block.getWorld().spawn(block.getLocation(), Marker.class);
                }

                if (blockName.contains("FURNACE") || blockName.equals("SMOKER")){
                    entity.getPersistentDataContainer().set(furnaceOnKey, PersistentDataType.BOOLEAN, false);
                }

                BlizzardData blizzardData = calculateChunkBlizzard(block.getLocation().getChunk());
                if (heatBlocksData.get(heatBlockName).fadeTime != 0){
                    entity.getPersistentDataContainer().set(heatBlockTicksKey, PersistentDataType.INTEGER, heatBlocksData.get(heatBlockName).fadeTime);
                }
                if (!blizzardData.allowFire) {
                    entity.getPersistentDataContainer().set(heatBlockTicksKey, PersistentDataType.INTEGER, 0);
                }
                entity.getPersistentDataContainer().set(heatBlockTypeKey, PersistentDataType.STRING, heatBlockName);
//                CitadelWinter.getInstance().getComponentLogger().info(entity.getPersistentDataContainer().get(heatBlockTypeKey, PersistentDataType.STRING));
            }
        }
    }

    @EventHandler
    public void onPlayerInteractCampfire (PlayerInteractEntityEvent event){
        if (!event.getRightClicked().getWorld().getName().equals("world")) return;
        if (event.isCancelled()) return;
        if (!(event.getRightClicked() instanceof Interaction)) return;

        Player player = event.getPlayer();
        ItemStack item = null;
        if (event.getHand() == EquipmentSlot.HAND) item = player.getInventory().getItemInMainHand();
        if (event.getHand() == EquipmentSlot.OFF_HAND) item = player.getInventory().getItemInOffHand();

        if (item == null) return;
        String itemTypeName = item.getType().name();
        for (String fuelName : fuel.keySet()){
            if (itemTypeName.contains(fuelName)){
                Interaction interaction = (Interaction) event.getRightClicked();
                int ticks = interaction.getPersistentDataContainer().getOrDefault(heatBlockTicksKey, PersistentDataType.INTEGER, 0);
                ticks += fuel.get(fuelName);
                String campfireType = interaction.getPersistentDataContainer().getOrDefault(heatBlockTypeKey, PersistentDataType.STRING, "CAMPFIRE");
                int campfireFadeTime = heatBlocksData.get(campfireType).fadeTime;
                if (ticks > campfireFadeTime) {
                    ticks = campfireFadeTime;
                }
                interaction.getPersistentDataContainer().set(heatBlockTicksKey, PersistentDataType.INTEGER, ticks);
                item.add(-1);
                Particle particle = Particle.FLAME;
                if (campfireType.equals("SOUL_CAMPFIRE")){
                    particle = Particle.SOUL_FIRE_FLAME;
                }
                interaction.getWorld().spawnParticle(particle, interaction.getLocation().add(0, 0.1, 0),
                        20, 0.5, 0.5, 0.5, 0.1);
                interaction.getWorld().spawnParticle(Particle.LARGE_SMOKE, interaction.getLocation().add(0, 0.3, 0),
                        5, 0.5, 0.5, 0.5, 0.1);
                interaction.getWorld().playSound(interaction, Sound.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1, 1);
            }
        }
    }

    @EventHandler
    public void onBlockGrow (BlockGrowEvent event){
        if (event.isCancelled()) return;
        if (!event.getBlock().getWorld().getName().equals("world")) return;
        if (!canGrow(event.getBlock().getLocation())) event.setCancelled(true);
    }
    @EventHandler
    public void onTreeGrow (StructureGrowEvent event){
        if (event.isCancelled()) return;
        if (!event.getLocation().getWorld().getName().equals("world")) return;
        if (!canGrow(event.getLocation())) event.setCancelled(true);
    }

    private boolean canGrow(Location location){
        float totalHeat = calculateNearHeat(location);
        totalHeat += calculateCold(location);
        return totalHeat > 0;
    }
}