package Citadel.citadelWinter.tasks;

import Citadel.citadelWinter.CitadelWinter;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static Citadel.citadelWinter.classes.Temperature.*;
import static Citadel.citadelWinter.classes.TemperatureData.*;

public class HeartBeat {
    private static final Server server = CitadelWinter.getInstance().getServer();

    public static void run() {
        Map<UUID, Integer> newMap = new HashMap<>(heartBeatPlayers);
        for (UUID uuid : heartBeatPlayers.keySet()){
            if (Math.random() > 1D / heartTickRate) continue;
            Player player = server.getPlayer(uuid);
            if (player == null) {
                newMap.remove(uuid);
                continue;
            }
            if (player.isDead()){
                newMap.remove(uuid);
                continue;
            }
            float playerTemperature = getEntityTemperature(player);
            if (newMap.get(uuid) >= calculatePeriodByTemperature(playerTemperature)){
                newMap.put(uuid, 0);
                player.playSound(player, Sound.ENTITY_WARDEN_HEARTBEAT, calculateVolumeByTemperature(playerTemperature), 0.1F);
            }else{
                newMap.put(uuid, heartBeatPlayers.get(uuid) + heartTickRate);
            }
        }
        heartBeatPlayers = newMap;
    }

    private static int calculatePeriodByTemperature(float temperature){
        if (temperature <= freezeTemperature) return minPeriod;
        return minPeriod + (int) ((temperature - freezeTemperature) * periodPerDegree);
    }
    private static float calculateVolumeByTemperature(float temperature){
        if (temperature <= freezeTemperature) return maxVolume;
        return maxVolume - ((temperature - freezeTemperature) * volumePerDegree);
    }
}
