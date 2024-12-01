package Citadel.citadelWinter.events;

import Citadel.citadelWinter.CitadelWinter;
import Template.template.Template;
import org.bukkit.Server;
import org.bukkit.event.Listener;

/**
 *  Класс, на котором основаны все прочие обработчики событий
 *  Автоматически регистрирует команду при инициализации
 *  См примеры реализации в TestEvent
 *  Для регистрации события нужно просто создать экземпляр нового класса команды в методе setEvents главного класса
 */
public abstract class AbstractEvent implements Listener {
    private static final Server server = CitadelWinter.getInstance().getServer();
    public AbstractEvent() {
        server.getPluginManager().registerEvents(this, CitadelWinter.getInstance());
    }
}
