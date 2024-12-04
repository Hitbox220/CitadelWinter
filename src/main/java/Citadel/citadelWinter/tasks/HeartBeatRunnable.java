package Citadel.citadelWinter.tasks;

import Citadel.citadelWinter.CitadelWinter;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

import static Citadel.citadelWinter.classes.Temperature.*;
import static Citadel.citadelWinter.classes.TemperatureData.*;

public class HeartBeatRunnable extends BukkitRunnable {
    private static Server server = CitadelWinter.getInstance().getServer();

    @Override
    public void run() {
        for (UUID uuid : heartBeatPlayers.keySet()){
            Player player = server.getPlayer(uuid);
            if (player == null) {
                damageTickMap.remove(uuid);
                return;
            }
            if (player.isDead()){
                heartBeatPlayers.remove(uuid);
                return;
            }
            float playerTemperature = getPlayerTemperature(player);
            if (heartBeatPlayers.get(uuid) >= calculatePeriodByTemperature(playerTemperature)){
                heartBeatPlayers.put(uuid, 0);
                player.playSound(player, Sound.ENTITY_WARDEN_HEARTBEAT, calculateVolumeByTemperature(playerTemperature), 0.1F);
                player.sendMessage(String.valueOf(calculatePeriodByTemperature(playerTemperature)));
            }else{
                heartBeatPlayers.put(uuid, heartBeatPlayers.get(uuid) + heartTickRate);
            }
        }
    }

    private int calculatePeriodByTemperature(float temperature){
        if (temperature <= freezeTemperature) return minPeriod;
        return minPeriod + (int) ((temperature - freezeTemperature) * periodPerDegree);
    }
    private float calculateVolumeByTemperature(float temperature){
        if (temperature <= freezeTemperature) return maxVolume;
        return maxVolume - ((temperature - freezeTemperature) * volumePerDegree);
    }
}
