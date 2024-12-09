package Citadel.citadelWinter.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Marker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;

import static Citadel.citadelWinter.classes.Temperature.setPlayerTemperature;
import static Citadel.citadelWinter.classes.TemperatureData.*;

public class WinterPlayerEvents extends AbstractEvent{
    @EventHandler
    public void onPlayerQuitEvent (PlayerQuitEvent event){

    }
    @EventHandler
    public void onPlayerDeathEvent (PlayerDeathEvent event){
        setPlayerTemperature(event.getPlayer(), defaultTemperature);
    }
}