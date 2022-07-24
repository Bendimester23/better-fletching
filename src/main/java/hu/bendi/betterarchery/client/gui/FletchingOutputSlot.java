package hu.bendi.betterarchery.client.gui;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class FletchingOutputSlot extends Slot {

    private Runnable callback;

    public FletchingOutputSlot(Inventory inventory, int index, int x, int y, Runnable callback) {
        super(inventory, index, x, y);
        this.callback = callback;
    }
    
    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack takeStack(int amount) {
        callback.run();
        return super.takeStack(amount);
    }
}
