package hu.bendi.betterarchery.client.gui;

import hu.bendi.betterarchery.arrows.ArrowMaterialRegistry;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class FletchingInputSlot extends Slot {
    private final ArrowPart part;

    public FletchingInputSlot(Inventory inventory, int index, int x, int y, ArrowPart part) {
        super(inventory, index, x, y);
        this.part = part;
    }
    
    @Override
    public boolean canInsert(ItemStack stack) {
        switch (part) {
            case HEAD:
                return ArrowMaterialRegistry.getHeadMaterial(stack.getItem()) != null;
            case BODY:
                return ArrowMaterialRegistry.getBodyMaterial(stack.getItem()) != null;
            case TAIL:
                return ArrowMaterialRegistry.getTailMaterial(stack.getItem()) != null;
            default:
                return false;
        }
    }

    public static enum ArrowPart {
        HEAD,
        BODY,
        TAIL
    }
}
