package Citadel.citadelWinter.classes;

import static Citadel.citadelWinter.CitadelWinter.config;

public class TemperatureData {
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
    public static int heartTickRate = config.getInt("heartBeat.tickRate");
    public static float volumePerDegree = (float) (
            (config.getDouble("heartBeat.maxVolume") - config.getDouble("heartBeat.minVolume")) / coldAmplitude);
    public static float periodPerDegree = (config.getInt("heartBeat.maxPeriod") - config.getInt("heartBeat.minPeriod")) / coldAmplitude;

    public static String infoColor      = String.format("<#%s>", config.getString("colors.info"));
    public static String heatColor      = String.format("<#%s>", config.getString("colors.heat"));
    public static String warmColor      = String.format("<#%s>", config.getString("colors.warm"));
    public static String defaultColor   = String.format("<#%s>", config.getString("colors.default"));
    public static String coldColor      = String.format("<#%s>", config.getString("colors.cold"));
    public static String freezeColor    = String.format("<#%s>", config.getString("colors.freeze"));
}
