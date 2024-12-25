package Citadel.citadelWinter;

import Citadel.citadelWinter.classes.TemperatureData;
import Citadel.citadelWinter.commands.BlizzardCommand;
import Citadel.citadelWinter.commands.TemperatureCommand;
import Citadel.citadelWinter.commands.TestCommand;
import Citadel.citadelWinter.events.WinterBlockEvents;
import Citadel.citadelWinter.events.WinterPlayerEvents;
import Citadel.citadelWinter.recipes.Recipes;
import Citadel.citadelWinter.tasks.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import static Citadel.citadelWinter.classes.Blizzard.getIgnisData;
import static Citadel.citadelWinter.classes.TemperatureData.*;

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

        TemperatureData.initialize();
        setEvents();
        setCommands();
        setRecipes();
        setTasks();

        getIgnisData();
    }

    @Override
    public void onDisable() {

    }

    private void setEvents(){
        new WinterBlockEvents();
        new WinterPlayerEvents();
    }
    private void setCommands(){
        new TestCommand();
        new TemperatureCommand();
        new BlizzardCommand();
    }
    private void setRecipes(){
        Recipes.addRecipes();
    }
    private void setTasks(){
//        ColdDamage coldDamage = new ColdDamage();
//        coldDamage.runTaskTimer(this, 0, coldDamageTickRate);
//        HeartBeat heartBeat = new HeartBeat();
//        heartBeat.runTaskTimer(this, 0, heartTickRate);
        ManageTemperature manageTemperature = new ManageTemperature();
        manageTemperature.runTaskTimer(this, 0, manageTemperatureTickRate);
        UpdateTemperature updateTemperature = new UpdateTemperature();
        updateTemperature.runTaskTimer(this, 0, updateTemperatureTickRate);
        UpdateHeatBlocks updateHeatBlocks = new UpdateHeatBlocks();
        updateHeatBlocks.runTaskTimer(this, 0, blocksFadeTickRate);
//        RepairMarkers repairMarkers = new RepairMarkers();
//        repairMarkers.runTaskTimer(this, 0, 100);
    }

    public static CitadelWinter getInstance(){
        return instance;
    }
}
