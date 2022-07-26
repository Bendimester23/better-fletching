package hu.bendi.betterarchery.mixin;

import hu.bendi.betterarchery.arrows.ArrowAttribute;
import hu.bendi.betterarchery.interfaces.PartsHolder;
import hu.bendi.betterarchery.interfaces.StatsHolder;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(ArrowEntity.class)
public abstract class ArrowEntityMixin extends PersistentProjectileEntity implements StatsHolder, PartsHolder {

    @Shadow
    @Final
    private Set<StatusEffectInstance> effects;

    @Shadow
    private Potion potion;

    @Shadow
    private boolean colorSet;
    private ArrowAttribute stats;
    private Parts parts;

    protected ArrowEntityMixin(EntityType<? extends PersistentProjectileEntity> type, double x, double y, double z,
                               World world) {
        super(type, x, y, z, world);
    }

    @Shadow
    public abstract int getColor();

    /**
     * @author Bendi
     * @reason Set NBT tags
     */
    @Overwrite
    public ItemStack asItemStack() {
        if (this.effects.isEmpty() && this.potion == Potions.EMPTY) {
            var stack = new ItemStack(Items.ARROW);
            stack.setSubNbt("ArrowData", stats == null ? ArrowAttribute.DEFAULT.toNbt() : stats.toNbt());
            if (parts != null) stack.setSubNbt("Parts", parts.toNbt());
            return stack;
        } else {
            ItemStack itemStack = new ItemStack(Items.TIPPED_ARROW);
            itemStack.setSubNbt("ArrowData", stats == null ? ArrowAttribute.DEFAULT.toNbt() : stats.toNbt());
            if (parts != null) itemStack.setSubNbt("Parts", parts.toNbt());
            PotionUtil.setPotion(itemStack, this.potion);
            PotionUtil.setCustomPotionEffects(itemStack, this.effects);
            if (this.colorSet) {
                itemStack.getOrCreateNbt().putInt("CustomPotionColor", this.getColor());
            }

            return itemStack;
        }
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
