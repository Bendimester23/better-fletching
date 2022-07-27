package hu.bendi.betterarchery.mixin;

import hu.bendi.betterarchery.block.FletchingTableBlockEntity;
import hu.bendi.betterarchery.block.ModBlocks;
import hu.bendi.betterarchery.item.ModItems;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.FletchingTableBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(FletchingTableBlock.class)
public class FletchingTableBlockMixin extends CraftingTableBlock implements BlockEntityProvider {


    public FletchingTableBlockMixin(Settings settings) {
        super(settings);
    }

    /**
     * @author Bendi
     * @reason Open GUI on right click
     */
    @Overwrite
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        var be = world.getBlockEntity(pos, ModBlocks.FLETCHING_TABLE_BLOCK_ENTITY);
        if (be.isEmpty()) return ActionResult.FAIL;
        var be2 = be.get();
        if (player.getStackInHand(hand).isOf(ModItems.SPECTRAL_UPGRADE)) {
            if (be2.hasSpectralUpgrade) return ActionResult.FAIL;

            var stack = player.getStackInHand(hand);
            if (stack.getCount() == 1) stack = ItemStack.EMPTY;
            else stack.setCount(stack.getCount() - 1);
            player.setStackInHand(hand, stack);
            player.getInventory().markDirty();

            player.playSound(SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            be2.hasSpectralUpgrade = true;
            be2.markDirty();
            return ActionResult.SUCCESS;
        }
        player.openHandledScreen(be2);
        return ActionResult.CONSUME;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FletchingTableBlockEntity(pos, state);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        if (world.isClient) return;
        var be = world.getBlockEntity(pos, ModBlocks.FLETCHING_TABLE_BLOCK_ENTITY);
        if (be.isEmpty()) return;
        var be2 = be.get();

        if (be2.hasSpectralUpgrade) dropStack(world, pos, new ItemStack(ModItems.SPECTRAL_UPGRADE));
        if (be2.glowstone_level > 0) dropStack(world, pos, new ItemStack(Items.GLOWSTONE_DUST, be2.glowstone_level));
    }
}
