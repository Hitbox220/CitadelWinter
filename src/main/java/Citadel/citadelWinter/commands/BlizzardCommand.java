package Citadel.citadelWinter.commands;

import Citadel.citadelWinter.CitadelWinter;
import com.google.common.collect.Lists;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static Citadel.citadelWinter.classes.Blizzard.*;
import static Citadel.citadelWinter.classes.TemperatureData.*;

public class BlizzardCommand extends AbstractCommand {
    public BlizzardCommand(){
        super("blizzard");
    }
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args[0].equalsIgnoreCase("ignis")) {
            if (args[1].equalsIgnoreCase("add")) {
                int strength = Integer.parseInt(args[2]);
                int x;
                int z;
                if (args.length == 3) {
                    x = ((Player) sender).getLocation().getChunk().getX();
                    z = ((Player) sender).getLocation().getChunk().getZ();
                } else {
                    x = Integer.parseInt(args[3]);
                    z = Integer.parseInt(args[4]);
                }
                addIgnis(new IgnisData(strength, x, z));
            } else if (args[1].equalsIgnoreCase("remove")){
                int x;
                int z;
                if (args.length == 2) {
                    x = ((Player) sender).getLocation().getChunk().getX();
                    z = ((Player) sender).getLocation().getChunk().getZ();
                } else {
                    x = Integer.parseInt(args[2]);
                    z = Integer.parseInt(args[3]);
                }
                removeIgnis(x, z);
            } else if (args[1].equalsIgnoreCase("get")){
                sender.sendMessage(Arrays.toString(
                        Objects.requireNonNull(CitadelWinter.getInstance().getServer().getWorld("world")).getPersistentDataContainer().get(ignisChunkKey, PersistentDataType.INTEGER_ARRAY)
                ));
                for (IgnisData ignisChunkData : ignisChunks) {
                    sender.sendMessage(String.format("%d %d %d", ignisChunkData.strength, ignisChunkData.x, ignisChunkData.z));
                }
            }
        } else if (args[0].equalsIgnoreCase("blizzard")){
            if (args[1].equalsIgnoreCase("get")){
                Chunk chunk;
                if (args.length == 2) {
                    chunk = ((Player) sender).getLocation().getChunk();
                    sender.sendMessage(String.format("Blizzard level: %s", calculateChunkBlizzard(chunk).type));
                } else {
                    int x = Integer.parseInt(args[2]);
                    int z = Integer.parseInt(args[3]);
                    sender.sendMessage(String.format("Blizzard level: %s", calculateChunkBlizzardCoo(x, z).type));
                }
            }
        } else if (args[0].equalsIgnoreCase("storm")){
            if (args[1].equalsIgnoreCase("run")) {
                World overWorld = CitadelWinter.getInstance().getServer().getWorld("world");
                assert overWorld != null;
                overWorld.getPersistentDataContainer().set(stormKey, PersistentDataType.INTEGER, Integer.parseInt(args[2]));
            } else if (args[1].equalsIgnoreCase("stop")) {
                World overWorld = CitadelWinter.getInstance().getServer().getWorld("world");
                assert overWorld != null;
                overWorld.getPersistentDataContainer().remove(stormKey);
            }
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if(args.length == 1) return Lists.newArrayList("ignis", "blizzard", "storm");
        if(args.length == 2){
            if (args[0].equalsIgnoreCase("ignis")){
                return Lists.newArrayList("add", "remove", "get");
            } else if (args[0].equalsIgnoreCase("blizzard")){
                return Lists.newArrayList("get");
            } else if (args[0].equalsIgnoreCase("storm")){
                return Lists.newArrayList("stop", "run");
            }
        }
        return Lists.newArrayList();
    }
}
