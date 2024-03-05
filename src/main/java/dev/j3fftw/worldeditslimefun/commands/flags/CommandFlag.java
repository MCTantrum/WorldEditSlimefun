package dev.j3fftw.worldeditslimefun.commands.flags;

import co.aikar.commands.BukkitCommandCompletionContext;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.block.Block;

import java.util.Collection;

public abstract class CommandFlag<T> {
    protected T value;

    public CommandFlag<T> setValue(T value) {
        this.value = value;
        return this;
    }

    public abstract void apply(SlimefunItem sfItem, Block block);
    public abstract boolean canApply(SlimefunItem sfItem);
    public abstract Collection<String> getTabSuggestions(BukkitCommandCompletionContext context);

    public abstract CommandFlag<T> ofValue(String value);
}