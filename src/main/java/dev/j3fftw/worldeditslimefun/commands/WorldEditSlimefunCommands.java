package dev.j3fftw.worldeditslimefun.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import dev.j3fftw.worldeditslimefun.WorldEditSlimefun;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.UnplaceableBlock;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.BlockPosition;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.function.Consumer;

@SuppressWarnings("unused")
@CommandAlias("wesf|sfedit")
public class WorldEditSlimefunCommands extends BaseCommand {

    @Default
    public void onDefault(Player player) {
        player.sendMessage(ChatColor.RED + "Please provide a valid subcommand.");
    }

    @Subcommand("wand")
    @CommandPermission("wesf.admin")
    public void onWand(Player player) {
        final ItemStack wand = SlimefunItem.getOptionalById("WEF_WAND").map(SlimefunItem::getItem).orElse(null);
        if (wand == null) {
            player.sendMessage(ChatColor.RED + "Wand not found!");
            return;
        }

        player.getInventory().addItem(wand);
    }

    @Subcommand("pos1")
    @CommandPermission("wesf.admin")
    public void onPos1(Player player) {
        WorldEditSlimefun.addPositionOne(player);
    }

    @Subcommand("pos2")
    @CommandPermission("wesf.admin")
    public void onPos2(Player player) {
        WorldEditSlimefun.addPositionTwo(player);
    }

    @Subcommand("paste")
    @CommandPermission("wesf.admin")
    @CommandCompletion("@slimefun_items")
    public void paste(Player player, String sfId) {
        BlockPosition pos1 = WorldEditSlimefun.getPositionOne(player);
        BlockPosition pos2 = WorldEditSlimefun.getPositionTwo(player);
        if (pos1 == null || pos2 == null) {
            return;
        }

        SlimefunItem sfItem = SlimefunItem.getById(sfId);
        if (sfItem == null || sfItem instanceof UnplaceableBlock) {
            return;
        }

        ItemStack item = sfItem.getItem();
        long start = System.currentTimeMillis();
        int amountOfBlocks = loopThroughSelection(pos1, pos2, block -> {
            block.setType(item.getType());
            BlockStorage.store(block, sfId);
            sfItem.callItemHandler(BlockPlaceHandler.class, handler -> {
                BlockPlaceEvent event = new BlockPlaceEvent(block, block.getState(),
                        block.getRelative(BlockFace.DOWN), item, player, true, EquipmentSlot.HAND);
                handler.onPlayerPlace(event);
            });
        });
        long time = System.currentTimeMillis() - start;

        player.sendMessage("Pasted " + amountOfBlocks + " " + sfItem.getItemName() + ChatColor.WHITE + " (s)");
        player.sendMessage("Took " + time + "ms to paste!");
    }

    @Subcommand("clear")
    @CommandCompletion("@boolean")
    @CommandPermission("wesf.admin")
    public void clear(Player player, boolean callEvent) {
        BlockPosition pos1 = WorldEditSlimefun.getPositionOne(player);
        BlockPosition pos2 = WorldEditSlimefun.getPositionTwo(player);
        if (pos1 == null || pos2 == null) {
            return;
        }

        long start = System.currentTimeMillis();
        int amountOfBlocks = loopThroughSelection(pos1, pos2,  block -> {
            if (callEvent && BlockStorage.hasBlockInfo(block)) {
                SlimefunItem sfItem = BlockStorage.check(block);
                sfItem.callItemHandler(BlockBreakHandler.class, handler -> {
                    BlockBreakEvent event = new BlockBreakEvent(block, player);
                    handler.onPlayerBreak(event, new ItemStack(Material.AIR), new ArrayList<>());
                });
            }
            block.setType(Material.AIR);
            BlockStorage.deleteLocationInfoUnsafely(block.getLocation(), true);
        });
        long time = System.currentTimeMillis() - start;

        player.sendMessage("Cleared " + amountOfBlocks + " blocks");
        player.sendMessage("Took " + time + "ms to clear!");
    }

    /**
     * @param pos1 The first corner of the selection
     * @param pos2 The second corner of the selection
     * @param blockRunnable What should happen to every block
     * @return The amount of blocks acted upon
     */
    private int loopThroughSelection(BlockPosition pos1, BlockPosition pos2, Consumer<Block> blockRunnable) {
        int amountOfBlocks = 0;
        int upperX = Math.max(pos1.getX(), pos2.getX());
        int upperY = Math.max(pos1.getY(), pos2.getY());
        int upperZ = Math.max(pos1.getZ(), pos2.getZ());
        int lowerX = Math.min(pos1.getX(), pos2.getX());
        int lowerY = Math.min(pos1.getY(), pos2.getY());
        int lowerZ = Math.min(pos1.getZ(), pos2.getZ());
        for (int x = lowerX; x <= upperX; x++) {
            for (int z = lowerZ; z <= upperZ; z++) {
                for (int y = lowerY; y <= upperY; y++) {
                    blockRunnable.accept(pos1.getWorld().getBlockAt(x, y, z));
                    amountOfBlocks++;
                }
            }
        }
        return amountOfBlocks;
    }
}
