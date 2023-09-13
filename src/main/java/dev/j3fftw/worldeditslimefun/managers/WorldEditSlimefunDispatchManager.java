package dev.j3fftw.worldeditslimefun.managers;

import co.aikar.commands.PaperCommandManager;
import com.google.common.base.Preconditions;
import dev.j3fftw.worldeditslimefun.commands.WorldEditSlimefunCommands;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.UnplaceableBlock;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;


public final class WorldEditSlimefunDispatchManager extends PaperCommandManager {

    private static WorldEditSlimefunDispatchManager instance;

    public WorldEditSlimefunDispatchManager(Plugin plugin) {
        super(plugin);

        Preconditions.checkArgument(instance == null, "Cannot create a new instance of the DispatchManager");
        instance = this;

        registerCommand(new WorldEditSlimefunCommands());

        getCommandCompletions().registerCompletion("slimefun_items", context -> {
            List<SlimefunItem> list = Slimefun.getRegistry().getEnabledSlimefunItems();
            List<String> unPlaceableItems = new ArrayList<>();
            for (SlimefunItem item : list) {
                if (!(item instanceof UnplaceableBlock) && item.getItem().getType().isBlock()) {
                    unPlaceableItems.add(item.getId());
                }
            }
            return unPlaceableItems;
        });

        getCommandCompletions().registerCompletion("boolean", context -> List.of("true", "false"));
    }
}
