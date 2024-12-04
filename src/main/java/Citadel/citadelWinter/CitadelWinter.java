package Citadel.citadelWinter;

import Citadel.citadelWinter.commands.TemperatureCommand;
import Citadel.citadelWinter.commands.TestCommand;
import Citadel.citadelWinter.events.TestEvent;
import Citadel.citadelWinter.recipes.Recipes;
import Citadel.citadelWinter.tasks.ColdDamageRunnable;
import Citadel.citadelWinter.tasks.HeartBeatRunnable;
import Citadel.citadelWinter.tasks.ManageTemperatureRunnable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import static Citadel.citadelWinter.classes.TemperatureData.heartTickRate;

public final class CitadelWinter extends JavaPlugin {
    private static CitadelWinter instance;
    public static FileConfiguration config;

    @Override
    public void onEnable() {
        instance = this;
        config = this.getConfig();
        saveResource("config.yml", true);
//        saveDefaultConfig();
        getComponentLogger().info(config.getString("enableMessage"));

        setEvents();
        setCommands();
        setRecipes();
        setTasks();
    }

    @Override
    public void onDisable() {

    }

    private void setEvents(){
        new TestEvent();
    }
    private void setCommands(){
        new TestCommand();
        new TemperatureCommand();
    }
    private void setRecipes(){
        Recipes.Initialize();
    }
    private void setTasks(){
        ColdDamageRunnable coldDamageRunnable = new ColdDamageRunnable();
        coldDamageRunnable.runTaskTimer(this, 0, 40);
        HeartBeatRunnable heartBeatRunnable = new HeartBeatRunnable();
        heartBeatRunnable.runTaskTimer(this, 0, heartTickRate);
        ManageTemperatureRunnable manageTemperatureRunnable = new ManageTemperatureRunnable();
        manageTemperatureRunnable.runTaskTimer(this, 0, 1);
    }

    public static CitadelWinter getInstance(){
        return instance;
    }
}
