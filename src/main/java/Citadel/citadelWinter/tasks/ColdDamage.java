package Citadel.citadelWinter.tasks;

import Citadel.citadelWinter.CitadelWinter;
import org.bukkit.Server;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static Citadel.citadelWinter.classes.Temperature.damageTickMap;
import static Citadel.citadelWinter.classes.TemperatureData.*;

public class ColdDamage {
    private static final Server server = CitadelWinter.getInstance().getServer();

    public static void run() {
        Map<UUID, Integer> newMap = new HashMap<>(damageTickMap);
        for (UUID uuid : damageTickMap.keySet()){
            if (Math.random() > 1D / coldDamageTickRate) continue;
//            CitadelWinter.getInstance().getComponentLogger().info(damageTickMap.toString());
            LivingEntity entity = (LivingEntity) server.getEntity(uuid);
            if (entity == null) {
                newMap.remove(uuid);
                continue;
            }
            if (entity.isDead()){
                newMap.remove(uuid);
                continue;
            }
            entity.damage(damageTickMap.get(uuid) * freezeDamageTickMultiplier, DamageSource.builder(DamageType.FREEZE).build());
            newMap.put(uuid, damageTickMap.get(uuid) + 1);
        }
        damageTickMap = newMap;
    }
}
