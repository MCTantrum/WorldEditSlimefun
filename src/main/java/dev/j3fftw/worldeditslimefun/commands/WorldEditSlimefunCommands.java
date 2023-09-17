package dev.j3fftw.worldeditslimefun.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import dev.j3fftw.worldeditslimefun.WorldEditSlimefun;
import io.github.thebusybiscuit.slimefun4.api.items.ItemHandler;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.UnplaceableBlock;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.BlockPosition;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

@CommandAlias("wesf|sfedit")
public class WorldEditSlimefunCommands extends BaseCommand {

    @Default
    public void onDefault(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Please provide a valid subcommand.");
    }

    @Subcommand("pos1")
    @CommandPermission("wesf.admin")
    public void onPos1(CommandSender sender) {
        if (sender instanceof Player player) {
            WorldEditSlimefun.getInstance().addPositionOne(player);
            player.sendMessage("Set position 1 to " + player.getLocation());
        }
    }

    @Subcommand("pos2")
    @CommandPermission("wesf.admin")
    public void onPos2(CommandSender sender) {
        if (sender instanceof Player player) {
            WorldEditSlimefun.getInstance().addPositionTwo(player);
            player.sendMessage("Set position 2 to " + player.getLocation());
        }
    }

    @Subcommand("paste")
    @CommandCompletion("@SLIMEFUN_ITEMS")
    @CommandPermission("wesf.admin")
    public void paste(CommandSender sender, String arg) {
        if (sender instanceof Player player) {
            BlockPosition pos1 = WorldEditSlimefun.getInstance().getPositionOne(player);
            BlockPosition pos2 = WorldEditSlimefun.getInstance().getPositionTwo(player);
            if (pos1 == null || pos2 == null) {
                return;
            }
            SlimefunItem sfItem = SlimefunItem.getById(arg);
            if (sfItem == null) {
                return;
            }
            if (sfItem instanceof UnplaceableBlock) {
                return;
            }
            long start = System.currentTimeMillis();
            int ammountOfBlocks = 0;

            int upperX = Math.max(pos1.getX(), pos2.getX());
            int upperY = Math.max(pos1.getY(), pos2.getY());
            int upperZ = Math.max(pos1.getZ(), pos2.getZ());
            int lowerX = Math.min(pos1.getX(), pos2.getX());
            int lowerY = Math.min(pos1.getY(), pos2.getY());
            int lowerZ = Math.min(pos1.getZ(), pos2.getZ());
            for (int x = lowerX; x <= upperX; x++) {
                for (int z = lowerZ; z <= upperZ; z++) {
                    for (int y = lowerY; y <= upperY; y++) {
                        Location location = new Location(player.getWorld(), x, y, z);
                        player.getWorld().setType(location, sfItem.getItem().getType());
                        BlockStorage.store(location.getBlock(), sfItem.getId());
                        for (ItemHandler handler : sfItem.getHandlers()) {
                            if (handler instanceof BlockPlaceHandler blockPlaceHandler) {
                                Block block = location.getBlock();
                                BlockPlaceEvent event = new BlockPlaceEvent(block, block.getState(),
                                    block.getRelative(BlockFace.DOWN), sfItem.getItem(), player, true);
                                blockPlaceHandler.onPlayerPlace(event);
                            }
                        }
                        ammountOfBlocks++;
                    }
                }
            }
            long time = System.currentTimeMillis() - start;
            player.sendMessage("Pasted " + ammountOfBlocks + " " + sfItem.getId());
            player.sendMessage("Took " + time + "ms to paste!");
        }
    }

    @Subcommand("clear")
    @CommandPermission("wesf.admin")
    public void clear(CommandSender sender, boolean arg) {
        if (sender instanceof Player player) {
            BlockPosition pos1 = WorldEditSlimefun.getInstance().getPositionOne(player);
            BlockPosition pos2 = WorldEditSlimefun.getInstance().getPositionTwo(player);
            if (pos1 == null || pos2 == null) {
                return;
            }

            long start = System.currentTimeMillis();
            int ammountOfBlocks = 0;
            int upperX = Math.max(pos1.getX(), pos2.getX());
            int upperY = Math.max(pos1.getY(), pos2.getY());
            int upperZ = Math.max(pos1.getZ(), pos2.getZ());
            int lowerX = Math.min(pos1.getX(), pos2.getX());
            int lowerY = Math.min(pos1.getY(), pos2.getY());
            int lowerZ = Math.min(pos1.getZ(), pos2.getZ());
            for (int x = lowerX; x <= upperX; x++) {
                for (int z = lowerZ; z <= upperZ; z++) {
                    for (int y = lowerY; y <= upperY; y++) {
                        Location location = new Location(player.getWorld(), x, y, z);
                        if (arg && BlockStorage.hasBlockInfo(location)) {
                            SlimefunItem sfItem = SlimefunItem.getById(BlockStorage.check(location).getId());
                            for (ItemHandler handler : sfItem.getHandlers()) {
                                if (handler instanceof BlockBreakHandler blockBreakHandler) {
                                    Block block = location.getBlock();
                                    BlockBreakEvent event = new BlockBreakEvent(block, player);
                                    blockBreakHandler.onPlayerBreak(event, null, null);
                                }
                            }
                        }
                        player.getWorld().setType(location, Material.AIR);
                        BlockStorage.deleteLocationInfoUnsafely(location, true);
                        ammountOfBlocks++;
                    }
                }
            }
            long time = System.currentTimeMillis() - start;
            player.sendMessage("deleted " + ammountOfBlocks + " blocks");
            player.sendMessage("Took " + time + "ms to paste!");
        }
    }
}
