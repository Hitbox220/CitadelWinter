package Citadel.citadelWinter.recipes;

import Citadel.citadelWinter.CitadelWinter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

import static Citadel.citadelWinter.classes.TemperatureData.infoColor;

public class TestRecipe {
    private static final Server server = CitadelWinter.getInstance().getServer();
    private static ItemStack thermometerItem = new ItemStack(Material.STICK);
    private static ItemMeta thermometerMeta;
    public static NamespacedKey thermometerKey = new NamespacedKey(CitadelWinter.getInstance(), "thermometer");

    public TestRecipe() {
        thermometerMeta = thermometerItem.getItemMeta();
        thermometerMeta.getPersistentDataContainer().set(thermometerKey, PersistentDataType.BOOLEAN, true);
        List<Component> lore = new ArrayList<>();
        lore.add(MiniMessage.miniMessage().deserialize(String.format("%sThermometer 3000", infoColor)));
        thermometerMeta.lore(lore);
        thermometerItem.setItemMeta(thermometerMeta);

        ShapedRecipe thermometerRecipe = new ShapedRecipe(thermometerKey, thermometerItem);
        thermometerRecipe.shape("A", "A", "B");
        thermometerRecipe.setIngredient('A', Material.GLASS_PANE);
        thermometerRecipe.setIngredient('B', Material.IRON_NUGGET);

        server.removeRecipe(thermometerKey);
        server.addRecipe(thermometerRecipe);
    }
}
