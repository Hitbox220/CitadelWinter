package Citadel.citadelWinter.tasks;

import Citadel.citadelWinter.CitadelWinter;
import org.bukkit.Server;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

import static Citadel.citadelWinter.classes.Temperature.damageTickMap;
import static Citadel.citadelWinter.classes.TemperatureData.freezeDamageTickMultiplier;

public class ColdDamage extends BukkitRunnable {
    private static final Server server = CitadelWinter.getInstance().getServer();
    @Override
    public void run() {
        for (UUID uuid : damageTickMap.keySet()){
            Player player = server.getPlayer(uuid);
            if (player == null) {
                damageTickMap.remove(uuid);
                return;
            }
            if (player.isDead()){
                damageTickMap.remove(uuid);
                return;
            }
            player.damage(damageTickMap.get(uuid) * freezeDamageTickMultiplier, DamageSource.builder(DamageType.FREEZE).build());
            damageTickMap.put(uuid, damageTickMap.get(uuid) + 1);
        }
    }
}
