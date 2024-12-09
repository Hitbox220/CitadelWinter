package Citadel.citadelWinter.events;

import Citadel.citadelWinter.CitadelWinter;
import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import static Citadel.citadelWinter.classes.Temperature.getBlockInteraction;
import static Citadel.citadelWinter.classes.Temperature.getBlockMarker;
import static Citadel.citadelWinter.classes.TemperatureData.*;

public class WinterBlockEvents extends AbstractEvent{
    @EventHandler
    public void onBlockPlaceEvent (BlockPlaceEvent event){
        if (!event.getBlockPlaced().getWorld().getName().equals("world")) return;
        if (event.isCancelled()) return;
        Block block = event.getBlockPlaced();
        String blockName = block.getType().name();
        for (String heatBlockName : heatBlocksData.keySet()){
            if (blockName.equals(heatBlockName)){
                Entity entity;
                if (heatBlockName.contains("CAMPFIRE")){
                    entity = block.getWorld().spawn(block.getLocation().add(0.5, campfireInterOffset, 0.5), Interaction.class);
                    ((Interaction) entity).setInteractionHeight(1 - campfireInterOffset);
                    ((Interaction) entity).setResponsive(true);
                } else {
                    entity = block.getWorld().spawn(block.getLocation(), Marker.class);
                }
                if (heatBlocksData.get(heatBlockName).fadeTime != 0){
                    entity.getPersistentDataContainer().set(heatBlockTicksKey, PersistentDataType.INTEGER, heatBlocksData.get(heatBlockName).fadeTime);
                }
                entity.getPersistentDataContainer().set(heatBlockTypeKey, PersistentDataType.STRING, heatBlockName);
            }
        }
    }
    @EventHandler
    public void onBlockBreakEvent (BlockBreakEvent event){
        if (!event.getBlock().getWorld().getName().equals("world")) return;
        if (event.isCancelled()) return;
        onBlockDestroy(event.getBlock());
    }
    @EventHandler
    public void onEntityExplodeEvent (EntityExplodeEvent event){
        for (Block block : event.blockList()){
            if (!block.getWorld().getName().equals("world")) return;
            if (event.isCancelled()) return;
            onBlockDestroy(block);
        }
    }
    @EventHandler
    public void onBlockFromTo (BlockFromToEvent event) {
        if (event.getToBlock().getType().name().contains("TORCH")) {
            if (!event.getToBlock().getWorld().getName().equals("world")) return;
            if (event.isCancelled()) return;
            onBlockDestroy(event.getToBlock());
        }
    }
    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if (event.getBlock().getType() == Material.ICE) {
            if (!event.getBlock().getWorld().getName().equals("world")) return;
            if (event.isCancelled()) return;
            checkRelatedDestroy(event.getBlock());
        }
    }
    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (!event.getBlock().getWorld().getName().equals("world")) return;
        if (event.isCancelled()) return;
        checkRelatedDestroy(event.getBlock());
    }

    private void onBlockDestroy(Block block){
        for (String heatBlockName : heatBlocksData.keySet()){
            if (block.getType().name().equalsIgnoreCase(heatBlockName)){
                if (heatBlockName.contains("CAMPFIRE")){
                    Interaction interaction = getBlockInteraction(block);
                    if (interaction != null) interaction.remove();
                    // Не завершать проверку, ведь возможно под костром располагался фонарь
                } else {
                    Marker marker = getBlockMarker(block);
                    if (marker != null) marker.remove();
                    return;
                }
            }
        }
        checkRelatedDestroy(block);
    }
    private void checkRelatedDestroy(Block block){
        for (BlockFace blockFace : new BlockFace[] {BlockFace.UP, BlockFace.DOWN, BlockFace.WEST, BlockFace.EAST, BlockFace.SOUTH, BlockFace.NORTH}){
            if (blockFace != BlockFace.DOWN && block.getRelative(blockFace).getType().name().contains("TORCH")){
                onBlockDestroy(block.getRelative(blockFace));
            } else if ((blockFace == BlockFace.UP || blockFace == BlockFace.DOWN) && block.getRelative(blockFace).getType().name().contains("LANTERN")){
                onBlockDestroy(block.getRelative(blockFace));
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
}