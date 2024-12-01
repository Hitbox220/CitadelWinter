package Citadel.citadelWinter.commands;

import Citadel.citadelWinter.CitadelWinter;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 *  Класс, на котором основаны все прочие команды
 *  Автоматически регистрирует команду при инициализации, поддерживает TabCompleter
 *  См примеры реализации в TestCommand и plugin.yml
 *  Для регистрации команды нужно просто создать экземпляр нового класса команды в методе setEvents главного класса
 */
public abstract class AbstractCommand implements CommandExecutor, TabCompleter {

    public AbstractCommand(String...commandNames){
        for (String commandName : commandNames){
            PluginCommand pluginCommand = CitadelWinter.getInstance().getCommand(commandName);
            if(pluginCommand != null){
                pluginCommand.setExecutor(this);
                pluginCommand.setTabCompleter(this);
            }
        }
    }

    public abstract void execute(CommandSender sender, String label, String[] args);
    public List<String> complete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args){
        execute(sender, label, args);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        return filter(complete(sender, args), args);
    }

    private List<String> filter(List<String> list, String[] args){
        if(list == null) return null;
        String last = args[args.length - 1];
        List<String> result = new ArrayList<>();
        for(String arg : list) {
            if(arg.toLowerCase().startsWith(last.toLowerCase())) result.add(arg);
        }
        return result;
    }
}

