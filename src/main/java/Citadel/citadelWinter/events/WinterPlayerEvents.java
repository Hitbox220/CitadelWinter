package Citadel.citadelWinter.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static Citadel.citadelWinter.classes.Temperature.setEntityTemperature;
import static Citadel.citadelWinter.classes.TemperatureData.*;

public class WinterPlayerEvents extends AbstractEvent{
    @EventHandler
    public void onPlayerQuitEvent (PlayerQuitEvent event){

    }
    @EventHandler
    public void onPlayerDeathEvent (PlayerDeathEvent event){
        setEntityTemperature(event.getPlayer(), defaultTemperature);
    }
}