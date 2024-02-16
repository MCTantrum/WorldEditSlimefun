package dev.j3fftw.worldeditslimefun.slimefun;

import dev.j3fftw.worldeditslimefun.WorldEditSlimefun;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class WandListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        final SlimefunItem slimefunItem = SlimefunItem.getByItem(event.getItem());
        if (slimefunItem == null || !slimefunItem.getId().equals("WESF_WAND")) {
            return;
        }

        event.setCancelled(true);
        final Action action = event.getAction();
        if (action == Action.LEFT_CLICK_BLOCK) {
            WorldEditSlimefun.addPositionOne(event.getPlayer());
        } else if (action == Action.RIGHT_CLICK_BLOCK) {
            WorldEditSlimefun.addPositionTwo(event.getPlayer());
        }
    }
}
