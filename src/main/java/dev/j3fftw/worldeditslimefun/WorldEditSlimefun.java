package dev.j3fftw.worldeditslimefun;

import dev.j3fftw.worldeditslimefun.managers.WorldEditSlimefunDispatchManager;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.BlockPosition;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class WorldEditSlimefun extends JavaPlugin implements SlimefunAddon {

    private static WorldEditSlimefun instance;
    private WorldEditSlimefunDispatchManager dispatchManager;
    @Override
    public void onEnable() {
        // Plugin startup logic
        setInstance(this);
        this.dispatchManager = new WorldEditSlimefunDispatchManager(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        setInstance(null);
    }

    @Nonnull
    private final Map<UUID, BlockPosition> storedPositionOne = new HashMap<>();

    @Nonnull
    private final Map<UUID, BlockPosition> storedPositionTwo = new HashMap<>();

    public void addPositionOne(@Nonnull Player player) {
        UUID uuid = player.getUniqueId();
        storedPositionOne.put(uuid, new BlockPosition(player.getLocation()));
    }

    public void addPositionTwo(@Nonnull Player player) {
        UUID uuid = player.getUniqueId();
        storedPositionTwo.put(uuid, new BlockPosition(player.getLocation()));
    }

    @Nullable
    public BlockPosition getPositionOne(@Nonnull Player player) {
        return storedPositionOne.get(player.getUniqueId());
    }

    @Nullable
    public BlockPosition getPositionTwo(@Nonnull Player player) {
        return storedPositionTwo.get(player.getUniqueId());
    }

    public static WorldEditSlimefun getInstance() {
        return instance;
    }

    private static void setInstance(WorldEditSlimefun ins) {
        instance = ins;
    }

    @NotNull
    @Override
    public JavaPlugin getJavaPlugin() {
        return this;
    }

    @Nullable
    @Override
    public String getBugTrackerURL() {
        return "empty";
    }
}
