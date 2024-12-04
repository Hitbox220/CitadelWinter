package Citadel.citadelWinter.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import static Citadel.citadelWinter.classes.Temperature.managePlayersTemperature;

public class ManageTemperatureRunnable extends BukkitRunnable {
    @Override
    public void run() {
        managePlayersTemperature();
    }
}
