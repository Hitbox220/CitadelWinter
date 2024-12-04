package Citadel.citadelWinter.commands;

import com.google.common.collect.Lists;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TestCommand extends AbstractCommand {
    public TestCommand(){
        super("test");
    }
    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        // Добавлять если нужно проверить является ли отправитель команды игроком
        if (!(sender instanceof Player player)){
            sender.sendMessage("Only players can use this command!");
            return;
        }
        player.sendMessage("Test command!");
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        if(args.length == 1) return Lists.newArrayList("arg0_0", "arg0_1", "arg0_2", "arg0_3");
        if(args.length == 2) return Lists.newArrayList("arg1_0", "arg1_1", "arg1_2", "arg1_3");
        return Lists.newArrayList();
    }
}
