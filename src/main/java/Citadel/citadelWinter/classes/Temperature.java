package Citadel.citadelWinter.classes;

import Citadel.citadelWinter.CitadelWinter;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Marker;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static Citadel.citadelWinter.classes.TemperatureData.*;
import static java.lang.Math.abs;

public class Temperature {
    public static Map<UUID, Integer> damageTickMap = new HashMap<>();
    public static Map<UUID, Integer> heartBeatPlayers = new HashMap<>();

    private static final Server server = CitadelWinter.getInstance().getServer();

    public static float getEntityTemperature(Entity entity){
        return entity.getPersistentDataContainer().getOrDefault(temperatureKey, PersistentDataType.FLOAT, defaultTemperature);
    }
    public static void setEntityTemperature(Entity entity, float temperature){
        entity.getPersistentDataContainer().set(temperatureKey, PersistentDataType.FLOAT, temperature);
    }
    public static void addEntityTemperature(Entity entity, float temperature){
        float newTemperature = getEntityTemperature(entity) + temperature * changingTemperatureMultiplier;
        if (newTemperature <= defaultTemperature || temperature < 0){
            entity.getPersistentDataContainer().set(temperatureKey, PersistentDataType.FLOAT, newTemperature);
        } else {
            entity.getPersistentDataContainer().set(temperatureKey, PersistentDataType.FLOAT, defaultTemperature);
        }
    }

    public static String calculateColorByTemperature(float temperature){
        if (temperature < freezeTemperature){
            return freezeColor;
        }else if (temperature < coldTemperature){
            return coldColor;
        }else if (temperature < defaultTemperature){
            return defaultColor;
        }else if (temperature < heatTemperature){
            return warmColor;
        }else{
            return heatColor;
        }
    }
    
    /**
     * Возвращает расстояние между двумя Location не в декартовой системе, а по количеству блоков,
     * которое нужно пройти, чтобы перейти из одной точки в другую.
     * Задает плоский ромб или объемный октаэдр
     */
    public static float distanceDiscrete(Location a, Location b){
        Location distance = a.subtract(b);
        return (float) (abs(distance.getX()) + abs(distance.getY()) + abs(distance.getZ()));
    }

    /** Пузырьковая сортировка 3 координат расстояния между точками.
     * Задает куб
     **/
    public static float getMaximumDistanceAxis(Location a, Location b) {
        Location distance = a.subtract(b);
        float x = (float) distance.x();
        float y = (float) distance.y();
        float z = (float) distance.z();
        float xY = Math.max(x, y);
        return Math.max(z, xY);
    }

    public static @Nullable Marker getBlockMarker(Block block){
        Marker returnMarker = null;
        for (Marker marker : block.getLocation().getNearbyEntitiesByType(Marker.class, 0.1)){
            if (returnMarker == null){
                returnMarker = marker;
            } else {
                CitadelWinter.getInstance().getComponentLogger().warn(String.format(
                        "Wrong marker at %s %s %s",
                        marker.getLocation().getX(), marker.getLocation().getY(), marker.getLocation().getZ()
                ));
                marker.remove();
            }
        }
        return returnMarker;
    }

    public static @Nullable Interaction getBlockInteraction(Block block){
        Interaction returnInteraction = null;
        for (Interaction interaction : block.getLocation().getNearbyEntitiesByType(Interaction.class, 0.5)){
            Location distance = interaction.getLocation().subtract(0.5, campfireInterOffset, 0.5).subtract(block.getLocation());
            if (distance.toVector().isZero()){
                if (returnInteraction == null){
                    returnInteraction = interaction;
                } else {
                    CitadelWinter.getInstance().getComponentLogger().warn(String.format(
                            "Wrong interaction at %s %s %s",
                            interaction.getLocation().getX(), interaction.getLocation().getY(), interaction.getLocation().getZ()
                    ));
                    interaction.remove();
                }
            }
        }
        return returnInteraction;
    }
}
