package Citadel.citadelWinter.tasks;

import Citadel.citadelWinter.CitadelWinter;
import Citadel.citadelWinter.classes.Blizzard;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

import static Citadel.citadelWinter.classes.Blizzard.calculateChunkBlizzard;
import static Citadel.citadelWinter.classes.Temperature.*;
import static Citadel.citadelWinter.classes.TemperatureData.*;

public class ManageTemperature extends BukkitRunnable {
    private static Server server = CitadelWinter.getInstance().getServer();
    private static final World overWorld = CitadelWinter.getInstance().getServer().getWorld("world");

    @Override
    public void run() {
        for(Player player : server.getOnlinePlayers()) {
            managePlayer(player);
        }
        assert overWorld != null;
        for (Entity entity : overWorld.getEntitiesByClasses(Animals.class, Villager.class)){
            manageEntity((LivingEntity) entity);
        }
        ColdDamage.run();
        HeartBeat.run();
    }

    public static void manageEntity(LivingEntity entity){
        if (entity.isDead()) return;
        float entityTemperature = getEntityTemperature(entity);
        actByEntityTemperature(entity, entityTemperature);
        actByBlizzard(entity);
    }

    public static void managePlayer(Player player){
        if (player.isDead()) return;
        float playerTemperature = getEntityTemperature(player);
        actByPlayerTemperature(player, playerTemperature);
        actByPlayerItems(player, playerTemperature);
        actByBlizzard(player);
    }

    public static void actByBlizzard(LivingEntity entity){
        if (Math.random() > 1D / blizzardDamageTickRate) return;
        if (entity.getLocation().getWorld().getName().equals("world")){
            BlizzardData blizzardData = calculateChunkBlizzard(entity.getLocation().getChunk());
            if (blizzardData.damage != 0){
                entity.damage(blizzardData.damage, DamageSource.builder(DamageType.FREEZE).build());
            }
        }
    }

    public static void actByPlayerTemperature(Player player, float playerTemperature){
        actByEntityTemperature(player, playerTemperature);
        if (playerTemperature < coldTemperature){
            if (!heartBeatPlayers.containsKey(player.getUniqueId())){
                heartBeatPlayers.put(player.getUniqueId(), 0);
            }
        }else{
            heartBeatPlayers.remove(player.getUniqueId());
        }
    }

    public static void actByEntityTemperature(LivingEntity entity, float entityTemperature){
        if (entityTemperature < freezeTemperature){
            entity.setFreezeTicks(150);
            if (!damageTickMap.containsKey(entity.getUniqueId())){
                damageTickMap.put(entity.getUniqueId(), 0);
            }
        }else{
            damageTickMap.remove(entity.getUniqueId());
            if (entityTemperature < coldTemperature){
                entity.setFreezeTicks((int) ((coldAmplitude - entityTemperature) * freezeTicksPerDegree));
            }
        }
    }

    public static void actByPlayerItems(Player player, float playerTemperature){
        if (player.getInventory().getItemInMainHand().getPersistentDataContainer().has(thermometerKey)
                || player.getInventory().getItemInOffHand().getPersistentDataContainer().has(thermometerKey)){
            player.sendActionBar(
                    MiniMessage.miniMessage().deserialize(
                            String.format("%sТемпература: %s%.1f°C", infoColor, calculateColorByTemperature(playerTemperature), playerTemperature)
                    ));
        } else if (player.getInventory().getItemInMainHand().getPersistentDataContainer().has(thermalImagerKey)
                || player.getInventory().getItemInOffHand().getPersistentDataContainer().has(thermalImagerKey)){
            Block block = player.getTargetBlockExact(5);
            if (block != null){
                Entity marker = null;
                if (getBlockInteraction(block) != null){
                    marker = getBlockInteraction(block);
                } else if (getBlockMarker(block) != null) {
                    marker = getBlockMarker(block);
                }
                if (marker != null){
                    player.sendActionBar(MiniMessage.miniMessage().deserialize(
                            String.valueOf(marker.getPersistentDataContainer().get(heatBlockTicksKey, PersistentDataType.INTEGER)))
                    );
                }
            } else {
                Entity targetEntity = player.getTargetEntity(5);
                if (targetEntity != null){
                    player.sendActionBar(MiniMessage.miniMessage().deserialize(
                            String.valueOf(targetEntity.getPersistentDataContainer().get(temperatureKey, PersistentDataType.FLOAT)))
                    );
                }
            }
        }
    }
}
