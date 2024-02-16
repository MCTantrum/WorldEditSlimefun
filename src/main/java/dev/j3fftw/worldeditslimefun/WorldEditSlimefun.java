package dev.j3fftw.worldeditslimefun;

import co.aikar.commands.PaperCommandManager;
import dev.j3fftw.worldeditslimefun.commands.WorldEditSlimefunCommands;
import dev.j3fftw.worldeditslimefun.slimefun.Items;
import dev.j3fftw.worldeditslimefun.slimefun.WandListener;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.UnplaceableBlock;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.libraries.dough.updater.BlobBuildUpdater;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
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
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }

        if (getConfig().getBoolean("auto-update", true) && getDescription().getVersion().startsWith("DEV - ")) {
            new BlobBuildUpdater(this, getFile(), "WorldEditSlimefun", "Dev").start();
        }

        new Metrics(this, 20799);

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

        Items.init(this);
        Bukkit.getPluginManager().registerEvents(new WandListener(), this);
    }

    @Override
    public void onDisable() {}

    @Nonnull
    public static String beautifyBlockPosition(@Nonnull BlockPosition position) {
        return "%s, %s, %s (%s)".formatted(position.getX(), position.getY(), position.getZ(), position.getWorld().getName());
    }

    public static void addPositionOne(@Nonnull Player player) {
        addPositionOne(player, new BlockPosition(player.getLocation()));
    }

    @ParametersAreNonnullByDefault
    public static void addPositionOne(Player player, BlockPosition position) {
        STORED_POSITION_ONE.put(player.getUniqueId(), position);
        player.sendMessage("Set position 1 to " + beautifyBlockPosition(position));
    }

    public static void addPositionTwo(@Nonnull Player player) {
        addPositionTwo(player, new BlockPosition(player.getLocation()));
    }

    @ParametersAreNonnullByDefault
    public static void addPositionTwo(Player player, BlockPosition position) {
        STORED_POSITION_TWO.put(player.getUniqueId(), position);
        player.sendMessage("Set position 2 to " + beautifyBlockPosition(position));
    }

    @Nullable
    public static BlockPosition getPositionOne(@Nonnull Player player) {
        return STORED_POSITION_ONE.get(player.getUniqueId());
    }

    @Nullable
    public static BlockPosition getPositionTwo(@Nonnull Player player) {
        return STORED_POSITION_TWO.get(player.getUniqueId());
    }

    @Nonnull
    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Nonnull
    @Override
    public String getBugTrackerURL() {
        return "https://github.com/Slimefun-Addon-Community/WorldEditSlimefun/issues";
    }
}
