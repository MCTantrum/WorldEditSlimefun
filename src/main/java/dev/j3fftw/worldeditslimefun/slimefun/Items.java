package dev.j3fftw.worldeditslimefun.slimefun;

import dev.j3fftw.worldeditslimefun.WorldEditSlimefun;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class Items {
    public static void init(WorldEditSlimefun plugin) {
        ItemGroup WESF_GROUP = new ItemGroup(
                new NamespacedKey(plugin, "world_edit_slimefun"),
                new CustomItemStack(Material.STONE_AXE, "&fWorld Edit Slimefun (Dummy Group)")
        );

        new SlimefunItem(
                WESF_GROUP,
                new SlimefunItemStack(
                        "WESF_WAND",
                        Material.STONE_AXE,
                        "&f(WESF) Selection Wand",
                        "&fLeft Click to select position 1",
                        "&fRight Click to select position 2"
                ),
                RecipeType.NULL,
                new ItemStack[9]
        ).register(plugin);
    }
}
