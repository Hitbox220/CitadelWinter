package Citadel.citadelWinter.recipes;

import Citadel.citadelWinter.CitadelWinter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static Citadel.citadelWinter.classes.TemperatureData.*;

public class Recipes {
    private static final Server server = CitadelWinter.getInstance().getServer();

    private static List<CraftingRecipe> initializeRecipes(){
        List<CraftingRecipe> recipes = new ArrayList<CraftingRecipe>();

        // Thermometer
        ItemStack thermometerItem = new ItemStack(Material.STICK);
        ItemMeta thermometerMeta = thermometerItem.getItemMeta();
        thermometerMeta.getPersistentDataContainer().set(thermometerKey, PersistentDataType.BOOLEAN, true);
        List<Component> lore = new ArrayList<>();
        lore.add(MiniMessage.miniMessage().deserialize(String.format("%sThermometer 3000", infoColor)));
        thermometerMeta.lore(lore);
        thermometerMeta.setMaxStackSize(1);
        thermometerItem.setItemMeta(thermometerMeta);

        ShapedRecipe thermometerRecipe = new ShapedRecipe(thermometerKey, thermometerItem);
        thermometerRecipe.shape("A", "A", "B");
        thermometerRecipe.setIngredient('A', Material.GLASS_PANE);
        thermometerRecipe.setIngredient('B', Material.IRON_NUGGET);
        thermometerRecipe.setCategory(CraftingBookCategory.EQUIPMENT);
        recipes.add(thermometerRecipe);

        return recipes;
    }

    private static void addInsulatedArmorRecipes(){
        String[] armorPeaces = new String[] {"HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS"};
        CitadelWinter pluginInstance = CitadelWinter.getInstance();
        List<Component> lore = new ArrayList<>();
        lore.add(MiniMessage.miniMessage().deserialize(String.format("%sBalls Heater 3000", infoColor)));

        for (int i = 0; i < 4; i++){
            NamespacedKey key = new NamespacedKey(pluginInstance, "insulated"+armorPeaces[i]);
            Material material = Material.getMaterial("LEATHER_"+armorPeaces[i]);
            ItemStack item = new ItemStack(material);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.lore(lore);
            itemMeta.getPersistentDataContainer().set(insulatedArmorKey, PersistentDataType.BOOLEAN, true);
            item.setItemMeta(itemMeta);

            ShapedRecipe recipe = new ShapedRecipe(key, item);
            recipe.shape("AAA", "ABA", "AAA");
            recipe.setIngredient('A', Material.WHITE_WOOL);
            recipe.setIngredient('B', material);
            recipe.setCategory(CraftingBookCategory.EQUIPMENT);
            server.removeRecipe(key);
            server.addRecipe(recipe);
        }
    }

    public static void addRecipes() {
        for (CraftingRecipe recipe : initializeRecipes()){
            server.removeRecipe(recipe.getKey());
        }
        for (CraftingRecipe recipe : initializeRecipes()){
            server.addRecipe(recipe);
        }
        addInsulatedArmorRecipes();
    }

}
