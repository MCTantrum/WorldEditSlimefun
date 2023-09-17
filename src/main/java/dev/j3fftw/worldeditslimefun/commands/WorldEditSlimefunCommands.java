package dev.j3fftw.worldeditslimefun.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
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
    @Description("This command sets the location for position 1 for your paste/clear command.")
    public void onPos1(Player player) {
        WorldEditSlimefun.getInstance().addPositionOne(player);
        player.sendMessage("Set position 1 to " + beautifyBlockPosition(player));
    }

    @Subcommand("pos2")
    @CommandPermission("wesf.admin")
    @Description("This command sets the location for position 2 for your paste/clear command.")
    public void onPos2(Player player) {
        WorldEditSlimefun.getInstance().addPositionTwo(player);
        player.sendMessage("Set position 2 to " + beautifyBlockPosition(player));
    }

    public String beautifyBlockPosition(Player player) {
        final BlockPosition position = new BlockPosition(player.getLocation());
        return "%s, %s, %s (%s)".formatted(position.getX(), position.getY(), position.getZ(), position.getWorld().getName());
    }

    @Subcommand("paste")
    @Syntax("[slimefun_item]")
    @CommandCompletion("@slimefun_items")
    @CommandPermission("wesf.admin")
    @Description("This command pastes the block you specified in the world with the position you chose. It has 1 argument that is the slimefun item id. This can be any placeable Slimefun or Slimefun Addon block.")
    public void paste(Player player, String sfId) {
        BlockPosition pos1 = WorldEditSlimefun.getInstance().getPositionOne(player);
        BlockPosition pos2 = WorldEditSlimefun.getInstance().getPositionTwo(player);
        if (pos1 == null || pos2 == null) {
            return;
        }

        SlimefunItem sfItem = SlimefunItem.getById(sfId);
        if (sfItem == null || sfItem instanceof UnplaceableBlock) {
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
        player.sendMessage("Pasted " + ammountOfBlocks + " " + sfItem.getItemName() + "(s)");
        player.sendMessage("Took " + time + "ms to paste!");
    }

    @Subcommand("clear")
    @Syntax("[call_event]")
    @CommandCompletion("@boolean")
    @CommandPermission("wesf.admin")
    @Description("This command clears the blocks you have selected with the position commands. When true it fires a blockbreakevent coming from a player. This is used to clear all the handlers. When false it just removes the block and the blockstorage data.")
    public void clear(Player player, boolean callEvent) {
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
                    if (callEvent && BlockStorage.hasBlockInfo(location)) {
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
        player.sendMessage("Removed " + ammountOfBlocks + " blocks");
        player.sendMessage("Took " + time + "ms to paste!");
    }
}
