package dev.j3fftw.worldeditslimefun.slimefun;

import dev.j3fftw.worldeditslimefun.utils.PositionManager;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.BlockPosition;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class WandListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        SlimefunItem slimefunItem = SlimefunItem.getByItem(event.getItem());
        if (slimefunItem == null || !slimefunItem.getId().equals("WESF_WAND")) {
            return;
        }

        event.setCancelled(true);
        
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        Action action = event.getAction();
        BlockPosition position = new BlockPosition(block);
        if (action == Action.LEFT_CLICK_BLOCK) {
            PositionManager.addPositionOne(event.getPlayer(), position);
        } else if (action == Action.RIGHT_CLICK_BLOCK) {
            PositionManager.addPositionTwo(event.getPlayer(), position);
        }
    }
}
