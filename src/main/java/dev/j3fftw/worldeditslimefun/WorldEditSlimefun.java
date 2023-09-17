package dev.j3fftw.worldeditslimefun;

import co.aikar.commands.PaperCommandManager;
import dev.j3fftw.worldeditslimefun.commands.WorldEditSlimefunCommands;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.UnplaceableBlock;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.BlockPosition;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class WorldEditSlimefun extends JavaPlugin implements SlimefunAddon {

    @Nonnull
    private static final Map<UUID, BlockPosition> STORED_POSITION_ONE = new HashMap<>();

    @Nonnull
    private static final Map<UUID, BlockPosition> STORED_POSITION_TWO = new HashMap<>();

    @Override
    public void onEnable() {
        PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.registerCommand(new WorldEditSlimefunCommands());
        paperCommandManager.getCommandCompletions().registerCompletion("boolean", context -> List.of("true", "false"));
        paperCommandManager.getCommandCompletions().registerCompletion("slimefun_items", context -> {
            List<SlimefunItem> slimefunItems = Slimefun.getRegistry().getEnabledSlimefunItems();
            List<String> placeableItems = new ArrayList<>();
            for (SlimefunItem item : slimefunItems) {
                if (!(item instanceof UnplaceableBlock) && item.getItem().getType().isBlock()) {
                    placeableItems.add(item.getId());
                }
            }
            return placeableItems;
        });
    }

    @Override
    public void onDisable() {}

    public static void addPositionOne(@Nonnull Player player) {
        STORED_POSITION_ONE.put(player.getUniqueId(), new BlockPosition(player.getLocation()));
    }

    public static void addPositionTwo(@Nonnull Player player) {
        STORED_POSITION_TWO.put(player.getUniqueId(), new BlockPosition(player.getLocation()));
    }

    @Nullable
    public static BlockPosition getPositionOne(@Nonnull Player player) {
        return STORED_POSITION_ONE.get(player.getUniqueId());
    }

    @Nullable
    public static BlockPosition getPositionTwo(@Nonnull Player player) {
        return STORED_POSITION_TWO.get(player.getUniqueId());
    }

    @NotNull
    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Nullable
    @Override
    public String getBugTrackerURL() {
        return "https://github.com/Slimefun-Addon-Community/WorldEditSlimefun/issues";
    }
}
