package Citadel.citadelWinter.classes;


import Citadel.citadelWinter.CitadelWinter;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

import static Citadel.citadelWinter.CitadelWinter.config;

public class TemperatureData {
    public static final NamespacedKey temperatureKey = new NamespacedKey(CitadelWinter.getInstance(), "temperature");
    public static final NamespacedKey thermometerKey = new NamespacedKey(CitadelWinter.getInstance(), "thermometer");
    public static final NamespacedKey thermalImagerKey = new NamespacedKey(CitadelWinter.getInstance(), "thermalImager");
    public static final NamespacedKey heatBlockTicksKey = new NamespacedKey(CitadelWinter.getInstance(), "heatBlockTicks");
    public static final NamespacedKey heatBlockTypeKey = new NamespacedKey(CitadelWinter.getInstance(), "heatBlockType");
    public static final NamespacedKey ignisChunkKey = new NamespacedKey(CitadelWinter.getInstance(), "ignisChunk");
    public static final NamespacedKey furnaceOnKey = new NamespacedKey(CitadelWinter.getInstance(), "furnaceOn");
    public static final NamespacedKey phantomSmeltingKey = new NamespacedKey(CitadelWinter.getInstance(), "phantomSmelting");
    public static final NamespacedKey stormKey = new NamespacedKey(CitadelWinter.getInstance(), "storm");

    public static final NamespacedKey insulatedArmorKey = new NamespacedKey(CitadelWinter.getInstance(), "insulatedArmor");

    public static float heatTemperature     = (float) config.getDouble("temperatures.heat");
    public static float defaultTemperature  = (float) config.getDouble("temperatures.default");
    public static float coldTemperature     = (float) config.getDouble("temperatures.cold");
    public static float freezeTemperature   = (float) config.getDouble("temperatures.freeze");

    public static int maxFreezeTick          = config.getInt("maxFreezeTick");
    public static float coldAmplitude        = coldTemperature - freezeTemperature;
    public static float freezeTicksPerDegree = maxFreezeTick / coldAmplitude;
    public static float freezeDamageTickMultiplier = (float) config.getDouble("freezeDamageTickMultiplier");

    public static float maxVolume = (float) config.getDouble("heartBeat.maxVolume");
    public static int minPeriod = config.getInt("heartBeat.minPeriod");
    public static float volumePerDegree = (float) (
            (config.getDouble("heartBeat.maxVolume") - config.getDouble("heartBeat.minVolume")) / coldAmplitude);
    public static float periodPerDegree = (config.getInt("heartBeat.maxPeriod") - config.getInt("heartBeat.minPeriod")) / coldAmplitude;

    public static String infoColor      = String.format("<#%s>", config.getString("colors.info"));
    public static String heatColor      = String.format("<#%s>", config.getString("colors.heat"));
    public static String warmColor      = String.format("<#%s>", config.getString("colors.warm"));
    public static String defaultColor   = String.format("<#%s>", config.getString("colors.default"));
    public static String coldColor      = String.format("<#%s>", config.getString("colors.cold"));
    public static String freezeColor    = String.format("<#%s>", config.getString("colors.freeze"));

    public static int heartTickRate = config.getInt("tickRates.heartBeat");
    public static int coldDamageTickRate = config.getInt("tickRates.coldDamage");
    public static int updateTemperatureTickRate = config.getInt("tickRates.updateTemperature");
    public static int manageTemperatureTickRate = config.getInt("tickRates.manageTemperature");
    public static int blocksFadeTickRate = config.getInt("tickRates.blocksFade");
    public static int blizzardDamageTickRate = config.getInt("tickRates.blizzardDamage");

    public static Map<String, HeatBlockData> heatBlocksData = new HashMap<>();
    public static float campfireInterOffset = (float) config.getDouble("blocks.CAMPFIRE.interactionOffset");
    public static int maxHeatBlockRadius = config.getInt("heat.maxRadius");
    public static float heatByItem = (float) config.getDouble("heat.heatItem");
    public static float insulatedArmorMultiplier = (float) config.getDouble("insulatedArmorMultiplier");

    public static float coldPerPeriod = (float) config.getDouble("coldPerTick") * updateTemperatureTickRate;
    public static float changingTemperatureMultiplier = (float) config.getDouble("changingTemperatureMultiplier");
    public static float netherHeat = (float) config.getDouble("netherHeat");

    public static float groundHeatLowest = config.getInt("height.groundHeatLowest");
    public static float groundHeatHighest = config.getInt("height.groundHeatHighest");
    public static float groundHeatAmplitude = (groundHeatHighest - groundHeatLowest);

    public static Map<String, Integer> fuel = new HashMap<>();

    public static BlizzardData[] blizzardsData = new BlizzardData[5];
    public static int blizzardRadiusMultiplier = config.getInt("blizzard.radiusMultiplier");
    public static int maxBlizzard = config.getInt("blizzard.maxBlizzard");
    public static Set<IgnisData> ignisChunks = new HashSet<>();

    public static float stormCold = (float) config.getDouble("storm.cold");
    public static float stormFade = (float) config.getDouble("storm.fade");

    public static void initialize(){
        ConfigurationSection blocksSection = config.getConfigurationSection("blocks");
        for (String blockName : Objects.requireNonNull(blocksSection).getKeys(false)){
            heatBlocksData.put(blockName, new HeatBlockData(
                    blocksSection.getInt(blockName + ".radius"),
                    (float) blocksSection.getDouble(blockName + ".heat"),
                    blocksSection.getInt(blockName + ".fadeTime")
            ));
        }

        ConfigurationSection fuelSection = config.getConfigurationSection("fuel");
        for (String fuelName : Objects.requireNonNull(fuelSection).getKeys(false)){
            fuel.put(fuelName, fuelSection.getInt(fuelName));
        }

        ConfigurationSection blizzardSection = config.getConfigurationSection("blizzard.chunks");
        int blizzardId = 0;
        for (String blizzardName : Objects.requireNonNull(blizzardSection).getKeys(false)){
            blizzardsData[blizzardId] = new BlizzardData(
                    blizzardName,
                    blizzardSection.getInt(blizzardName + ".cold"),
                    blizzardSection.getInt(blizzardName + ".fade"),
                    blizzardSection.getBoolean(blizzardName + ".allowFire"),
                    blizzardSection.getInt(blizzardName + ".damage")
            );
            blizzardId += 1;
        }

        CitadelWinter.getInstance().getComponentLogger().info("TemperatureData loaded!");
    }

    public static class HeatBlockData {
        public int radius;
        public float heat;
        public int fadeTime;
        public HeatBlockData(int radius, float heat, int fadeTime){
            this.radius = radius;
            // Значение сразу умножены на частоту обновления, то есть теплота за один период обновления температур
            this.heat = heat * updateTemperatureTickRate;
            // Количество тиков
            this.fadeTime = fadeTime * 20 * 60;
        }
    }
    public static class BlizzardData {
        public String type;
        public int cold;
        public int fade;
        public boolean allowFire;
        public int damage;
        public BlizzardData(String type, int cold, int fade, boolean allowFire, int damage){
            this.type = type;
            this.cold = cold;
            this.fade = fade;
            this.allowFire = allowFire;
            this.damage = damage;
        }
        public BlizzardData(BlizzardData blizzardData){
            this.type = blizzardData.type;
            this.cold = blizzardData.cold;
            this.fade = blizzardData.fade;
            this.allowFire = blizzardData.allowFire;
            this.damage = blizzardData.damage;
        }
    }

    public static class IgnisData {
        public int strength;
        public int x;
        public int z;
        public IgnisData (int strength, int x, int z){
            this.strength = strength;
            this.x = x;
            this.z = z;
        }
    }
}
