package hu.bendi.betterarchery.mixin;

import hu.bendi.betterarchery.interfaces.IHasHair;
import hu.bendi.betterarchery.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
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

import java.util.Random;

@Mixin(StriderEntity.class)
public abstract class StriderEntityMixin extends AnimalEntity implements IHasHair {
    private static final TrackedData<Boolean> HAS_HAIR;
    private int hairTimer = 5;

    static {
        HAS_HAIR = DataTracker.registerData(StriderEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    }

    private final Random random = new Random();

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
            dropStack(new ItemStack(ModItems.STRIDER_HAIR, (int) (random.nextFloat() * 5)), 1.5f);
        }
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void initDataTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(HAS_HAIR, false);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (getHasHair()) return;
        if (hairTimer > 0) hairTimer--;
        else {
            hairTimer = 600;
            setHasHair(true);
        }
    }

    @Override
    public boolean getHasHair() {
        return this.dataTracker.get(HAS_HAIR);
    }

    @Override
    public void setHasHair(boolean has) {
        this.dataTracker.set(HAS_HAIR, has);
    }
}
