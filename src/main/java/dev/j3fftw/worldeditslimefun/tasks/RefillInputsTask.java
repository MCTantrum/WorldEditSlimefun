package dev.j3fftw.worldeditslimefun.tasks;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RefillInputsTask extends AbstractTask {

    protected final int[] slots;
    protected List<ItemStack> inputs = new ArrayList<>();

    public RefillInputsTask(SlimefunItem sfItem) {
        super(sfItem);

        BlockMenuPreset preset = Slimefun.getRegistry().getMenuPresets().get(sfItem.getId());
        this.slots = preset == null ? new int[0] : preset.getSlotsAccessedByItemTransport(ItemTransportFlow.INSERT);
    }

    @Override
    public void runTask(Block block) {
        BlockMenu menu = BlockStorage.getInventory(block);
        if (menu == null) {
            return;
        }

        for (ItemStack itemStack : this.inputs) {
            menu.pushItem(new ItemStack(itemStack), this.slots);
        }
    }


    public void setInputs(List<ItemStack> inputs) {
        this.inputs = inputs;
    }
}
