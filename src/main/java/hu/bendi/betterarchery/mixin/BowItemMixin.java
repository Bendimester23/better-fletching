package hu.bendi.betterarchery.mixin;

import hu.bendi.betterarchery.arrows.ArrowAttribute;
import hu.bendi.betterarchery.interfaces.PartsHolder;
import hu.bendi.betterarchery.interfaces.StatsHolder;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Objects;

@Mixin(BowItem.class)
public abstract class BowItemMixin extends RangedWeaponItem {

   public BowItemMixin(Settings settings) {
      super(settings);
   }

   private static final float ACCURACY_OVERRIDE = 15;

   @Inject(method = "onStoppedUsing", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z", shift = At.Shift.BEFORE), cancellable = true)
   private void appendStats(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci, PlayerEntity playerEntity, boolean bl, ItemStack itemStack, int i, float f, boolean bl2, ArrowItem arrowItem, PersistentProjectileEntity persistentProjectileEntity) {
      ArrowAttribute attribute = itemStack.getSubNbt("ArrowData") != null ? ArrowAttribute.fromNbt(Objects.requireNonNull(itemStack.getSubNbt("ArrowData"))) : ArrowAttribute.DEFAULT;

      if (persistentProjectileEntity instanceof StatsHolder) {

         ((StatsHolder) persistentProjectileEntity).setStats(attribute);

         if (itemStack.getSubNbt("Parts") != null) {
            ((PartsHolder) persistentProjectileEntity).setParts(PartsHolder.Parts.fromNbt(Objects.requireNonNull(itemStack.getSubNbt("Parts"))));
         }
      }
      float pitchOffset = (float) (Math.random() * (1.0 - attribute.getAccuracy()) * ACCURACY_OVERRIDE) * (Math.random() > 0.5 ? -1 : 0);
      float yawOffset = (float) (Math.random() * (1.0 - attribute.getAccuracy()) * ACCURACY_OVERRIDE) * (Math.random() > 0.5 ? -1 : 0);

      persistentProjectileEntity.setVelocity(playerEntity, playerEntity.getPitch() + pitchOffset, playerEntity.getYaw() + yawOffset, 0.0F, f * 3.0F * attribute.getSpeed(), 1.0F);

      int j = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
      if (j > 0) {
         persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() + (double) j * 0.5D + 0.5D + attribute.getDamage());
      }
   }
/*
   public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
      if (user instanceof PlayerEntity playerEntity) {
         boolean bl = playerEntity.getAbilities().creativeMode || EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0;
         ItemStack itemStack = playerEntity.getArrowType(stack);
         if (!itemStack.isEmpty() || bl) {
            if (itemStack.isEmpty()) {
               itemStack = new ItemStack(Items.ARROW);
            }

            int i = this.getMaxUseTime(stack) - remainingUseTicks;
            float f = BowItem.getPullProgress(i);
            if (!((double) f < 0.1D)) {
                 boolean bl2 = bl && itemStack.isOf(Items.ARROW);
                 if (!world.isClient) {
                    ArrowAttribute attribute = itemStack.getSubNbt("ArrowData") != null ? ArrowAttribute.fromNbt(Objects.requireNonNull(itemStack.getSubNbt("ArrowData"))) : ArrowAttribute.DEFAULT;

                    ArrowItem arrowItem = (ArrowItem) (itemStack.getItem() instanceof ArrowItem ? itemStack.getItem() : Items.ARROW);
                    PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(world, itemStack, playerEntity);

                    if (persistentProjectileEntity instanceof StatsHolder) {

                       ((StatsHolder) persistentProjectileEntity).setStats(attribute);

                       if (itemStack.getSubNbt("Parts") != null) {
                          ((PartsHolder) persistentProjectileEntity).setParts(PartsHolder.Parts.fromNbt(Objects.requireNonNull(itemStack.getSubNbt("Parts"))));
                       }
                    }

                    float pitchOffset = (float) (Math.random() * (1.0 - attribute.getAccuracy()) * ACCURACY_OVERRIDE) * (Math.random() > 0.5 ? -1 : 0);
                    float yawOffset = (float) (Math.random() * (1.0 - attribute.getAccuracy()) * ACCURACY_OVERRIDE) * (Math.random() > 0.5 ? -1 : 0);

                    persistentProjectileEntity.setVelocity(playerEntity, playerEntity.getPitch() + pitchOffset, playerEntity.getYaw() + yawOffset, 0.0F, f * 3.0F * attribute.getSpeed(), 1.0F);
                    if (f == 1.0F) {
                       persistentProjectileEntity.setCritical(true);
                    }

                    int j = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
                    if (j > 0) {
                       persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() + (double) j * 0.5D + 0.5D + attribute.getDamage());
                    }

                    int k = EnchantmentHelper.getLevel(Enchantments.PUNCH, stack);
                    if (k > 0) {
                       persistentProjectileEntity.setPunch(k);
                    }
  
                    if (EnchantmentHelper.getLevel(Enchantments.FLAME, stack) > 0) {
                       persistentProjectileEntity.setOnFireFor(100);
                    }

                    stack.damage(1, playerEntity, (p) -> p.sendToolBreakStatus(playerEntity.getActiveHand()));
                    if (bl2 || playerEntity.getAbilities().creativeMode && (itemStack.isOf(Items.SPECTRAL_ARROW) || itemStack.isOf(Items.TIPPED_ARROW))) {
                       persistentProjectileEntity.pickupType = PickupPermission.CREATIVE_ONLY;
                    }
  
                    world.spawnEntity(persistentProjectileEntity);
                 }

                 world.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                 if (!bl2 && !playerEntity.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                    if (itemStack.isEmpty()) {
                       playerEntity.getInventory().removeOne(itemStack);
                    }
                 }
  
                 playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
              }
           }
        }
     }*/
}
