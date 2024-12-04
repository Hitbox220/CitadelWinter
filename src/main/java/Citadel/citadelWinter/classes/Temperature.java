package Citadel.citadelWinter.classes;

import Citadel.citadelWinter.CitadelWinter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static Citadel.citadelWinter.classes.TemperatureData.*;

public class Temperature {
    public static final NamespacedKey temperatureKey = new NamespacedKey(CitadelWinter.getInstance(), "temperature");
    public static final NamespacedKey thermometerKey = new NamespacedKey(CitadelWinter.getInstance(), "thermometer");
    public static Map<UUID, Integer> damageTickMap = new HashMap<>();
    public static Map<UUID, Integer> heartBeatPlayers = new HashMap<>();

    private static final Server server = CitadelWinter.getInstance().getServer();

    public static float getPlayerTemperature(Player player){
        return player.getPersistentDataContainer().getOrDefault(temperatureKey, PersistentDataType.FLOAT, defaultTemperature);
    }
    public static void setPlayerTemperature(Player player, float temperature){
        player.getPersistentDataContainer().set(temperatureKey, PersistentDataType.FLOAT, temperature);
    }

    public static void managePlayersTemperature(){
        for(Player player : server.getOnlinePlayers()) {
            if (player.isDead()) return;
            float playerTemperature = getPlayerTemperature(player);
            actByPlayerItems(player, playerTemperature);
            actByPlayerTemperature(player, playerTemperature);
        }
    }

    public static void actByPlayerTemperature(Player player , float playerTemperature){
        if (playerTemperature < freezeTemperature){
            player.setFreezeTicks(150);
            if (!damageTickMap.containsKey(player.getUniqueId())){
                damageTickMap.put(player.getUniqueId(), 0);
            }
        }else{
            damageTickMap.remove(player.getUniqueId());
            if (playerTemperature < coldTemperature){
                player.setFreezeTicks((int) ((coldAmplitude - playerTemperature) * freezeTicksPerDegree));
                if (!heartBeatPlayers.containsKey(player.getUniqueId())){
                    heartBeatPlayers.put(player.getUniqueId(), 0);
                }
            }else{
                heartBeatPlayers.remove(player.getUniqueId());
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
        }
    }

    private static String calculateColorByTemperature(float temperature){
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
}
