package Citadel.citadelWinter.commands;

import com.google.common.collect.Lists;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static Citadel.citadelWinter.classes.Temperature.getEntityTemperature;
import static Citadel.citadelWinter.classes.Temperature.setEntityTemperature;
import static Citadel.citadelWinter.classes.TemperatureData.defaultTemperature;
import static Citadel.citadelWinter.recipes.Recipes.thermalImagerItem;
import static Citadel.citadelWinter.recipes.Recipes.thermometerItem;

public class TemperatureCommand extends AbstractCommand {
    public TemperatureCommand(){
        super("temperature");
    }
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        Player player = null;
        if (!args[0].equalsIgnoreCase("items")){
            player = sender.getServer().getPlayer(args[1]);
        }
        assert player != null;

        if (args[0].equalsIgnoreCase("set")){
            try {
                setEntityTemperature(player, Float.parseFloat(args[2]));
            } catch (Exception e){
                sender.sendMessage("Не удалось задать значение температуры!");
            }
        } else if (args[0].equalsIgnoreCase("reset")){
            setEntityTemperature(player, defaultTemperature);

        } else if (args[0].equalsIgnoreCase("get")){
            sender.sendMessage(String.valueOf(getEntityTemperature(player)));
        } else if (args[0].equalsIgnoreCase("items")){
            ((Player) sender).getInventory().addItem(thermometerItem);
            ((Player) sender).getInventory().addItem(thermalImagerItem);
        }
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if(args.length == 1) return Lists.newArrayList("set", "reset", "get", "items");
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
