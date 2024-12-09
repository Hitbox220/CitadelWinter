package Citadel.citadelWinter.tasks;

import Citadel.citadelWinter.CitadelWinter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import static Citadel.citadelWinter.classes.Temperature.*;
import static Citadel.citadelWinter.classes.TemperatureData.heatBlockTicksKey;

public class ManageTemperature extends BukkitRunnable {
    private static Server server = CitadelWinter.getInstance().getServer();
    @Override
    public void run() {
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
        }
    }
}
