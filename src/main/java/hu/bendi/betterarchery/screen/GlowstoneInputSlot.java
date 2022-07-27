package hu.bendi.betterarchery.screen;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GlowstoneInputSlot extends Slot {
    private final Supplier<Integer> canInsert;
    private final Consumer<Integer> onInsert;

    public GlowstoneInputSlot(int x, int y, Supplier<Integer> remaining, Consumer<Integer> onInsert) {
        super(new SimpleInventory(1), 0, x, y);
        this.canInsert = remaining;
        this.onInsert = onInsert;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return stack.isOf(Items.GLOWSTONE_DUST) && canInsert.get() >= stack.getCount();
    }

    @Override
    public ItemStack takeStack(int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack insertStack(ItemStack stack, int count) {
        if (!stack.isOf(Items.GLOWSTONE_DUST)) return stack;
        if (canInsert.get() < stack.getCount()) {
            var can = canInsert.get();
            onInsert.accept(can);
            stack.decrement(can);
            return stack;
        }
        onInsert.accept(stack.getCount());
        return ItemStack.EMPTY;
    }
}
