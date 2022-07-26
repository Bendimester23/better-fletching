package hu.bendi.betterarchery.screen;

import hu.bendi.betterarchery.ArcheryMod;
import hu.bendi.betterarchery.arrows.ArrowAttribute;
import hu.bendi.betterarchery.arrows.ArrowMaterialRegistry;
import hu.bendi.betterarchery.client.gui.FletchingInputSlot;
import hu.bendi.betterarchery.client.gui.FletchingOutputSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;

public class FletchingScreenHandler extends ScreenHandler {
    public SimpleInventory input = new SimpleInventory(3);
    private final CraftingResultInventory output = new CraftingResultInventory();
    private final ServerPlayerEntity player;
    private final ScreenHandlerContext context;

    protected FletchingScreenHandler(ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
        this.player = null;
        this.context = null;
    }

    public FletchingScreenHandler(int syncId, PlayerInventory inventory) {
        this(syncId, inventory, ScreenHandlerContext.EMPTY, null);
    }

    public FletchingScreenHandler(int syncId, PlayerInventory inventory, ScreenHandlerContext context, ServerPlayerEntity player) {
        super(ArcheryMod.FLETCHING_SCREEN_HANDLER, syncId);
        this.player = player;
        //Output slot
        this.addSlot(new FletchingOutputSlot(output, 0, 80, 45));

        //Inputs
        this.addSlot(new FletchingInputSlot(input, 0, 21, 25, FletchingInputSlot.ArrowPart.HEAD));
        this.addSlot(new FletchingInputSlot(input, 1, 21, 46, FletchingInputSlot.ArrowPart.BODY));
        this.addSlot(new FletchingInputSlot(input, 2, 21, 67, FletchingInputSlot.ArrowPart.TAIL));

        //Player inventory
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 106 + i * 18));
            }
        }
        //Hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inventory, i, 8 + i * 18, 164));
        }
        this.context = context;
    }

    @Override
    public boolean canUse(PlayerEntity var1) {
        return true;
    }

    @Override
    public boolean canInsertIntoSlot(Slot slot) {
        return slot.inventory != output;
    }

    public void onCrafted() {
        input.removeStack(0, 1);
        input.removeStack(1, 1);
        input.removeStack(2, 1);
    }

    public void updateResult() {
        if (input.getStack(0).isEmpty() || input.getStack(1).isEmpty() || input.getStack(2).isEmpty()) {
            setResult(ItemStack.EMPTY);
            return;
        }
        var result = ArrowAttribute.Builder.create()
                .appendAttributes(ArrowMaterialRegistry.getHeadMaterial(input.getStack(0).getItem()))
                .appendAttributes(ArrowMaterialRegistry.getBodyMaterial(input.getStack(1).getItem()))
                .appendAttributes(ArrowMaterialRegistry.getTailMaterial(input.getStack(2).getItem()))
                .build();

        var stack = new ItemStack(Items.ARROW);

        stack.setSubNbt("ArrowData", result.toNbt());
        var tag = new NbtCompound();

        tag.putString("Head", input.getStack(0).getTranslationKey());
        tag.putString("Body", input.getStack(1).getTranslationKey());
        tag.putString("Tail", input.getStack(2).getTranslationKey());
        stack.setSubNbt("Parts", tag);

        stack.setCount(4);

        setResult(stack);
    }

    private void setResult(ItemStack result) {
        output.setStack(0, result);
        setPreviousTrackedSlot(0, result);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
        if (slotIndex == 0 && actionType == SlotActionType.PICKUP) onCrafted();
        updateResult();
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index == 0) {
                int maxAmount = Math.min(Math.min(input.getStack(0).getCount(), Math.min(input.getStack(1).getCount(), input.getStack(2).getCount())), 16);
                for (int i = 0; i < maxAmount; i++) {
                    this.onCrafted();
                }
                itemStack2.setCount(maxAmount * 4);
                this.context.run((world, pos) -> itemStack2.getItem().onCraft(itemStack2, world, player));
                if (!this.insertItem(itemStack2, 10, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickTransfer(itemStack2, itemStack);
            } else if (index >= 4 && index < 39) {
                if (!this.insertItem(itemStack2, 1, 10, false)) {
                    if (index < 37) {
                        if (!this.insertItem(itemStack2, 37, 39, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.insertItem(itemStack2, 10, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.insertItem(itemStack2, 10, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, itemStack2);
            if (index == 0) {
                player.dropItem(itemStack2, false);
            }
        }

        return itemStack;
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
        updateResult();
    }
}
