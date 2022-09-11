package hu.bendi.betterarchery.screen;

import hu.bendi.betterarchery.ArcheryMod;
import hu.bendi.betterarchery.arrows.ArrowAttribute;
import hu.bendi.betterarchery.arrows.ArrowMaterialRegistry;
import hu.bendi.betterarchery.block.FletchingTableBlockEntity;
import hu.bendi.betterarchery.block.ModBlocks;
import hu.bendi.betterarchery.network.ModNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;

public class FletchingScreenHandler extends ScreenHandler {
    public SimpleInventory input = new SimpleInventory(3);
    private final CraftingResultInventory output = new CraftingResultInventory();
    private final ServerPlayerEntity player;
    private final ScreenHandlerContext context;
    public boolean hasSpectralUpgrade = false;
    public boolean hasTippedUpgrade = false;
    public short glowStoneLevel = 0;
    private byte selectedType = 0;

    public FletchingScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY, null);
        hasSpectralUpgrade = buf.readBoolean();
        hasTippedUpgrade = buf.readBoolean();
        glowStoneLevel = buf.readShort();
        selectedType = buf.readByte();
        if (hasSpectralUpgrade) addSpectralSlots(true);
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

        if (player != null) {
            context.run((w, p) -> {
                var be3 = w.getBlockEntity(p, ModBlocks.FLETCHING_TABLE_BLOCK_ENTITY);
                if (be3.isEmpty()) return;
                if (!be3.get().hasSpectralUpgrade) return;
                addSpectralSlots(false);
            });
        }

        context.run((world, blockPos) -> world.getBlockEntity(blockPos, ModBlocks.FLETCHING_TABLE_BLOCK_ENTITY).ifPresent(be -> {
            glowStoneLevel = be.glowstone_level;
            selectedType = be.selected_type;
            hasSpectralUpgrade = be.hasSpectralUpgrade;
            hasTippedUpgrade = be.hasTippedUpgrade;
        }));

        this.context = context;
    }

    private void addSpectralSlots(boolean isClient) {
        this.addSlot(new GlowstoneInputSlot(-43, 10, () -> 256 - glowStoneLevel, integer -> {
            glowStoneLevel += integer;
            if (isClient) return;
            context.run((world, blockPos) -> world.getBlockEntity(blockPos, ModBlocks.FLETCHING_TABLE_BLOCK_ENTITY).ifPresent(be -> {
                be.glowstone_level += integer;
                glowStoneLevel = be.glowstone_level;
                be.markDirty();
            }));
        }));
    }

    @Override
    public boolean canUse(PlayerEntity var1) {
        return true;
    }

    @Override
    public boolean canInsertIntoSlot(Slot slot) {
        return slot.inventory != output;
    }

    public void clientClickType(byte clicked) {
        var buf = PacketByteBufs.create();
        buf.writeInt(syncId);
        buf.writeByte(clicked);
        ClientPlayNetworking.send(ModNetworking.FLETCHING_SET_TYPE_PACKET, buf);
    }

    public byte getSelectedType() {
        return selectedType;
    }

    public void setSelectedType(byte to) {
        if (to == 1 && !this.hasSpectralUpgrade) {
            ArcheryMod.LOGGER.info("WTF");
            return;
        }
        if (to == 2 && !this.hasTippedUpgrade) return;

        this.selectedType = to;
        this.context.run((world, blockPos) -> {
            var be2 = world.getChunk(blockPos).getBlockEntity(blockPos);
            if (!(be2 instanceof FletchingTableBlockEntity be)) return;

            be.selected_type = to;
            be.markDirty();
            var buf = PacketByteBufs.create();
            buf.writeInt(syncId);
            buf.writeByte(to);

            ServerPlayNetworking.send(player, ModNetworking.FLETCHING_SET_TYPE_PACKET, buf);
            updateResult();
        });
        updateResult();
    }

    public void onCrafted() {
        input.removeStack(0, 1);
        input.removeStack(1, 1);
        input.removeStack(2, 1);
        glowStoneLevel -= 1;
    }

    public void updateResult() {
        if (input.getStack(0).isEmpty() || input.getStack(1).isEmpty() || input.getStack(2).isEmpty()) {
            setResult(ItemStack.EMPTY);
            return;
        }

        if (selectedType == 1 && glowStoneLevel < 1) {
            setResult(ItemStack.EMPTY);
            return;
        }

        var result = ArrowAttribute.Builder.create()
                .appendAttributes(ArrowMaterialRegistry.getHeadMaterial(input.getStack(0).getItem()))
                .appendAttributes(ArrowMaterialRegistry.getBodyMaterial(input.getStack(1).getItem()))
                .appendAttributes(ArrowMaterialRegistry.getTailMaterial(input.getStack(2).getItem()))
                .build();

        var stack = new ItemStack(selectedType == 0 ? Items.ARROW : Items.SPECTRAL_ARROW);

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
        if (slotIndex == 0 && actionType == SlotActionType.PICKUP && !output.isEmpty()) onCrafted();
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
                itemStack2.getItem().onCraft(itemStack2, player.world, player);
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

    public ServerPlayerEntity getPlayer() {
        return player;
    }

    public short getGlowStoneLevel() {
        return glowStoneLevel;
    }
}
