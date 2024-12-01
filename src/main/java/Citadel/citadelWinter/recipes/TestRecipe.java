package Citadel.citadelWinter.recipes;

import Citadel.citadelWinter.CitadelWinter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class TestRecipe {
    private static final Server server = CitadelWinter.getInstance().getServer();
    private static final ItemStack testItem = new ItemStack(Material.STICK);
    public static NamespacedKey testKey = new NamespacedKey(CitadelWinter.getInstance(), "test");

    public TestRecipe() {
        ShapedRecipe testRecipe = new ShapedRecipe(testKey, testItem);
        testRecipe.shape("A", "A", "B");
        testRecipe.setIngredient('A', Material.GLASS_PANE);
        testRecipe.setIngredient('B', Material.IRON_NUGGET);

        server.removeRecipe(testKey);
        server.addRecipe(testRecipe);
    }
}
