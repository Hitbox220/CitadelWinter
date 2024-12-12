package Citadel.citadelWinter.classes;

import Citadel.citadelWinter.CitadelWinter;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

import static Citadel.citadelWinter.classes.TemperatureData.*;
import static java.lang.Math.abs;

public class Blizzard {
    private static final World world = CitadelWinter.getInstance().getServer().getWorld("world");

    public static void getIgnisData (){
        assert world != null;
        if (world.getPersistentDataContainer().has(ignisChunkKey)) {
            int [] ignisData = world.getPersistentDataContainer().getOrDefault(ignisChunkKey, PersistentDataType.INTEGER_ARRAY, new int[] {0, 0, 0});
            CitadelWinter.getInstance().getComponentLogger().info("Ignis sanctum:");
            for (int i = 0; i < ignisData.length; i += 3){
                ignisChunks.add(new IgnisData(ignisData[i], ignisData[i+1], ignisData[i+2]));
                CitadelWinter.getInstance().getComponentLogger().info("Strength: {} on {} {}", ignisData[i], ignisData[i + 1], ignisData[i + 2]);
            }
        }
    }

    public static void addIgnis(IgnisData ignisData){
        assert world != null;
        ignisChunks.add(ignisData);
        int[] addData = new int[] {ignisData.strength, ignisData.x, ignisData.z};
        int [] newData;
        if (world.getPersistentDataContainer().has(ignisChunkKey)){
            int[] previousData = world.getPersistentDataContainer().get(ignisChunkKey, PersistentDataType.INTEGER_ARRAY);
            assert previousData != null;
            newData = ArrayUtils.addAll(previousData, addData);
        } else {
            newData = addData;
        }
        world.getPersistentDataContainer().set(ignisChunkKey, PersistentDataType.INTEGER_ARRAY, newData);
        ignisChunks.add(ignisData);
    }

    public static void removeIgnis(int x, int z){
        assert world != null;
        int[] previousData = world.getPersistentDataContainer().get(ignisChunkKey, PersistentDataType.INTEGER_ARRAY);
        if (previousData == null) {
            CitadelWinter.getInstance().getComponentLogger().info("There are no Ignis in world PersistentDataContainer!");
            return;
        }
        for (int i = 0; i < previousData.length; i += 3){
            if (previousData[i+1] == x && previousData[i+2] == z){
                int[] newData = ArrayUtils.addAll(
                        Arrays.copyOf(previousData, i),
                        Arrays.copyOfRange(previousData, i+3, previousData.length)
                );
                world.getPersistentDataContainer().set(ignisChunkKey, PersistentDataType.INTEGER_ARRAY, newData);
                ignisChunks.removeIf(ignisDataChunk -> ignisDataChunk.x == x && ignisDataChunk.z == z);
                return;
            }
        }
        CitadelWinter.getInstance().getComponentLogger().info("Can not find this Ignis!");
    }

    public static int calculateChunkBlizzard(Chunk chunk){
        return calculateChunkBlizzardCoo(chunk.getX(), chunk.getZ());
    }

    public static int calculateChunkBlizzardCoo(int x, int z) {
        int finalBlizzard = 4;
        for (IgnisData ignisChunksData : ignisChunks){
            int distanceX = abs(x - ignisChunksData.x);
            int distanceZ = abs(z - ignisChunksData.z);
            int distance = Math.max(distanceX, distanceZ);
            int blizzard = distance / blizzardRadiusMultiplier;
            if (blizzard == 0) return 0;
            if (blizzard < finalBlizzard) finalBlizzard = blizzard;
        }
        return finalBlizzard;
    }
}
