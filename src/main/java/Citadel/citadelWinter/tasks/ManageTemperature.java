package Citadel.citadelWinter.tasks;

import Citadel.citadelWinter.CitadelWinter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Server;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import static Citadel.citadelWinter.classes.Blizzard.calculateChunkBlizzard;
import static Citadel.citadelWinter.classes.Temperature.*;
import static Citadel.citadelWinter.classes.TemperatureData.*;

public class ManageTemperature extends BukkitRunnable {
    private static Server server = CitadelWinter.getInstance().getServer();
    private static int currentTick = 0;

    @Override
    public void run() {
        currentTick += 1;
        if (currentTick > blizzardDamageTickRate) currentTick = 0;
        for(Player player : server.getOnlinePlayers()) {
            if (player.isDead()) return;
            float playerTemperature = getPlayerTemperature(player);
            actByPlayerItems(player, playerTemperature);
            actByPlayerTemperature(player, playerTemperature);

            if (player.getTargetBlockExact(5) != null){
                if (player.getTargetBlockExact(5).getType().name().contains("CAMPFIRE")){
                    if (getBlockInteraction(player.getTargetBlockExact(5)) != null) {
                        player.sendActionBar(MiniMessage.miniMessage().deserialize(
                                String.valueOf(getBlockInteraction(player.getTargetBlockExact(5)).getPersistentDataContainer().get(heatBlockTicksKey, PersistentDataType.INTEGER)))
                        );
                    }
                }
            }
            if (player.getLocation().getWorld().getName().equals("world")){
                int blizzardType = calculateChunkBlizzard(player.getLocation().getChunk());
                if (blizzardsData[blizzardType].damage != 0){
                    if (currentTick == 0){
                        player.damage(blizzardsData[blizzardType].damage, DamageSource.builder(DamageType.FREEZE).build());
                    }
                }
            }
        }
    }
}
