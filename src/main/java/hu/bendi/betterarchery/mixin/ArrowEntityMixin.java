package hu.bendi.betterarchery.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.world.World;

@Mixin(ArrowEntity.class)
public abstract class ArrowEntityMixin extends PersistentProjectileEntity {

    protected ArrowEntityMixin(EntityType<? extends PersistentProjectileEntity> type, double x, double y, double z,
            World world) {
        super(type, x, y, z, world);
    }
}
