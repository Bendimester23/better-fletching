package hu.bendi.betterarchery.mixin;

import hu.bendi.betterarchery.arrows.ArrowAttribute;
import hu.bendi.betterarchery.interfaces.PartsHolder;
import hu.bendi.betterarchery.interfaces.StatsHolder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(SpectralArrowEntity.class)
public abstract class SpectralArrowEntityMixin extends PersistentProjectileEntity implements StatsHolder, PartsHolder {

    private ArrowAttribute stats;
    private Parts parts;

    protected SpectralArrowEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    /**
     * @author Bendi
     * @reason Set NBT tags
     */
    @Overwrite
    public ItemStack asItemStack() {
        var stack = new ItemStack(Items.SPECTRAL_ARROW);
        stack.setSubNbt("ArrowData", stats == null ? ArrowAttribute.DEFAULT.toNbt() : stats.toNbt());
        if (parts != null) stack.setSubNbt("Parts", parts.toNbt());
        return stack;
    }


    @Override
    public ArrowAttribute getStats() {
        return stats;
    }

    @Override
    public void setStats(ArrowAttribute stats) {
        this.stats = stats;
    }

    @Override
    public PartsHolder.Parts getParts() {
        return parts;
    }

    @Override
    public void setParts(Parts parts) {
        this.parts = parts;
    }
}
