package hu.bendi.betterarchery.mixin;

import hu.bendi.betterarchery.ArcheryMod;
import hu.bendi.betterarchery.screen.FletchingScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.FletchingTableBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(FletchingTableBlock.class)
public class FletchingTableBlockMixin {

    private static final Text TITLE = new TranslatableText("container.fletching_table");

    /**
     * @author Bendi
     * @reason Open GUI on right click
     */
    @Overwrite
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        player.openHandledScreen(createScreenHanlerFactory(world, pos, (ServerPlayerEntity) player));
        return ActionResult.CONSUME;
    }

    public NamedScreenHandlerFactory createScreenHanlerFactory(World world, BlockPos pos, ServerPlayerEntity serverPlayer) {
        return new NamedScreenHandlerFactory() {

            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory inventory, PlayerEntity player) {
                ArcheryMod.LOGGER.debug(player.toString());
                return new FletchingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos), serverPlayer);
            }

            @Override
            public Text getDisplayName() {
                return TITLE;
            }
            
        };
    }

}
