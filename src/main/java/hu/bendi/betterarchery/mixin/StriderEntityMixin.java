package hu.bendi.betterarchery.mixin;

import hu.bendi.betterarchery.interfaces.IHasHair;
import hu.bendi.betterarchery.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StriderEntity.class)
public abstract class StriderEntityMixin extends AnimalEntity implements IHasHair {

    private boolean hasHair = false;
    private int hairTimer = 5;

    protected StriderEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    public void interact(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (this.world.isClient) return;
        if (player.getStackInHand(hand).isOf(Items.SHEARS)) {
            if (!getHasHair()) {
                cir.setReturnValue(ActionResult.FAIL);
                return;
            }
            cir.setReturnValue(ActionResult.success(true));
            setHasHair(false);
            dropStack(new ItemStack(ModItems.STRIDER_HAIR, (int) (Math.random() * 5)), 1.5f);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (hairTimer > 0 && !hasHair) hairTimer--;
        else {
            hairTimer = 600;
            hasHair = true;
        }
    }

    @Override
    public boolean getHasHair() {
        return hasHair;
    }

    @Override
    public void setHasHair(boolean hasHair) {
        this.hasHair = hasHair;
    }
}
