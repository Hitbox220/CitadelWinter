package Citadel.citadelWinter.commands;

import com.google.common.collect.Lists;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static Citadel.citadelWinter.classes.Temperature.getPlayerTemperature;
import static Citadel.citadelWinter.classes.Temperature.setPlayerTemperature;
import static Citadel.citadelWinter.classes.TemperatureData.defaultTemperature;

public class TemperatureCommand extends AbstractCommand {
    public TemperatureCommand(){
        super("temperature");
    }
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        Player player = sender.getServer().getPlayer(args[1]);
        if (player == null) {
            sender.sendMessage("Не удалось найти игрока!");
            return;
        }
        if (args[0].equalsIgnoreCase("set")){
            try {
                setPlayerTemperature(player, Float.parseFloat(args[2]));
            } catch (Exception e){
                sender.sendMessage("Не удалось задать значение температуры!");
            }
        } else if (args[0].equalsIgnoreCase("reset")){
            setPlayerTemperature(player, defaultTemperature);

        } else if (args[0].equalsIgnoreCase("get")){
            sender.sendMessage(String.valueOf(getPlayerTemperature(player)));
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if(args.length == 1) return Lists.newArrayList("set", "reset", "get");
        if(args.length == 2){
            List<String> playerNames = new ArrayList<>();
            for (Player player : sender.getServer().getOnlinePlayers()){
                playerNames.add(player.getName());
            }
            return Lists.newArrayList(playerNames);
        }
        return Lists.newArrayList();
    }
}
