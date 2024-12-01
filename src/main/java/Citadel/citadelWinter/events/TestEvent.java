package Citadel.citadelWinter.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class TestEvent extends AbstractEvent{
    @EventHandler
    public void onPlayerInteractEvent (PlayerInteractEvent event){
        Player player = event.getPlayer();
        player.sendMessage("Test event!");
    }
}