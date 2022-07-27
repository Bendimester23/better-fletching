package hu.bendi.betterarchery.block;

import hu.bendi.betterarchery.screen.FletchingScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class FletchingTableBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory {

    private static final Text TITLE = new TranslatableText("container.fletching_table");

    public boolean hasSpectralUpgrade = false;
    public boolean hasTippedUpgrade = false;

    public short glowstone_level = 0;

    public FletchingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.FLETCHING_TABLE_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        byte upgrades = 0;
        upgrades |= (hasSpectralUpgrade ? 1 : 0);
        upgrades |= (hasTippedUpgrade ? 1 : 0) << 1;

        nbt.putByte("Upgrades", upgrades);
        nbt.putShort("GlowstoneLevel", glowstone_level);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        byte upgrades = nbt.getByte("Upgrades");
        hasSpectralUpgrade = (upgrades & 1) == 1;
        hasTippedUpgrade = ((upgrades >> 1) & 1) == 1;

        glowstone_level = nbt.getShort("GlowstoneLevel");
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBoolean(hasSpectralUpgrade);
        buf.writeShort(glowstone_level);
    }

    @Override
    public Text getDisplayName() {
        return TITLE;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new FletchingScreenHandler(syncId, inv, ScreenHandlerContext.create(player.world, pos), (ServerPlayerEntity) player);
    }
}
