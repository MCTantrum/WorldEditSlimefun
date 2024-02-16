package dev.j3fftw.worldeditslimefun.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import dev.j3fftw.worldeditslimefun.WorldEditSlimefun;
import dev.j3fftw.worldeditslimefun.utils.PositionManager;
import dev.j3fftw.worldeditslimefun.utils.Utils;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.UnplaceableBlock;
import io.github.thebusybiscuit.slimefun4.libraries.dough.blocks.BlockPosition;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
@CommandAlias("wesf|sfedit")
@CommandPermission("wesf.admin")
public class WorldEditSlimefunCommands extends BaseCommand {

    public static void init(WorldEditSlimefun plugin) {
        PaperCommandManager manager = new PaperCommandManager(plugin);
        CommandCompletions<BukkitCommandCompletionContext> completions = manager.getCommandCompletions();

        completions.registerCompletion("slimefun_blocks", context -> Utils.SLIMEFUN_BLOCKS);
        completions.registerCompletion("slimefun_items", context -> Utils.SLIMEFUN_ITEMS);
        completions.registerCompletion("materials", context -> {
            List<String> inputs = new ArrayList<>();
            World world = context.getPlayer().getWorld();
            for (Material material : Utils.MATERIALS.values()) {
                if (material.isEnabledByFeature(world)) {
                    inputs.add(material.name());
                }
            }
            inputs.sort(Comparator.naturalOrder());
            return inputs;
        });

        manager.registerCommand(new WorldEditSlimefunCommands());
    }

    @Default
    public void onDefault(Player player) {
        player.sendMessage(ChatColor.RED + "Please provide a valid subcommand.");
    }

    @Subcommand("wand")
    public void onWand(Player player) {
        ItemStack wand = SlimefunItem.getOptionalById("WESF_WAND").map(SlimefunItem::getItem).orElse(null);
        if (wand == null) {
            player.sendMessage(ChatColor.RED + "Wand not found!");
            return;
        }

        player.getInventory().addItem(wand);
    }

    @Subcommand("pos1")
    public void onPos1(Player player) {
        PositionManager.addPositionOne(player);
    }

    @Subcommand("pos2")
    public void onPos2(Player player) {
        PositionManager.addPositionTwo(player);
    }

    @Subcommand("paste")
    @CommandCompletion("@slimefun_blocks true|false @slimefun_items|@materials")
    public void paste(Player player, String sfId, @Default("false") boolean energy, @Optional String[] inputs) {
        BlockPosition pos1 = PositionManager.getPositionOne(player);
        BlockPosition pos2 = PositionManager.getPositionTwo(player);
        if (pos1 == null || pos2 == null) {
            return;
        }

        SlimefunItem sfItem = SlimefunItem.getById(sfId);
        if (sfItem == null || sfItem instanceof UnplaceableBlock) {
            return;
        }

        boolean charge = energy && sfItem instanceof EnergyNetComponent component && component.isChargeable();
        boolean fillItems = inputs != null && Slimefun.getRegistry().getMenuPresets().containsKey(sfId) && hasValidInputs(inputs);

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

            if (charge) {
                BlockStorage.addBlockInfo(block, "energy-charge", String.valueOf(Integer.MAX_VALUE), false);
            }

            if (fillItems) {
                BlockMenu menu = BlockStorage.getInventory(block);
                int[] slots = menu.getPreset().getSlotsAccessedByItemTransport(ItemTransportFlow.INSERT);
                for (ItemStack input : getInputs(inputs)) {
                    if (menu.pushItem(input, slots) != null) {
                        break;
                    }
                }
            }
        });
        long time = System.currentTimeMillis() - start;

        player.sendMessage("Pasted " + amountOfBlocks + " " + sfItem.getItemName() + ChatColor.WHITE + " (s)");
        player.sendMessage("Took " + time + "ms to paste!");
    }

    @Subcommand("clear")
    @CommandCompletion("true|false")
    public void clear(Player player, @Default("false") boolean callEvent) {
        BlockPosition pos1 = PositionManager.getPositionOne(player);
        BlockPosition pos2 = PositionManager.getPositionTwo(player);
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

    private boolean hasValidInputs(String[] inputs) {
        boolean hasValid = false;
        for (int i = 0; i < inputs.length; i++) {
            boolean valid = isValidInput(inputs[i]);
            hasValid = hasValid || valid;
            if (!valid) {
                inputs[i] = null;
            }
        }
        return hasValid;
    }

    private boolean isValidInput(String input) {
        if (input == null || input.equals("AIR")) {
            return false;
        }

        if (SlimefunItem.getById(input) != null) {
            return true;
        }

        return Utils.isValidMaterial(input);
    }

    private List<ItemStack> getInputs(String[] inputs) {
        List<ItemStack> newInputs = new ArrayList<>();
        for (String input : inputs) {
            SlimefunItem slimefunItem = SlimefunItem.getById(input);
            Material material = Utils.getMaterial(input);
            if (slimefunItem != null) {
                newInputs.add(new CustomItemStack(slimefunItem.getItem(), slimefunItem.getItem().getMaxStackSize()));
            } else if (material != null) {
                newInputs.add(new ItemStack(material, material.getMaxStackSize()));
            }
        }
        return newInputs;
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
