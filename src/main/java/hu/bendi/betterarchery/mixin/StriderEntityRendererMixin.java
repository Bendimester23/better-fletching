package hu.bendi.betterarchery.mixin;

import hu.bendi.betterarchery.client.entity.StriderHairFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.StriderEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.StriderEntityModel;
import net.minecraft.entity.passive.StriderEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StriderEntityRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class StriderEntityRendererMixin extends MobEntityRenderer<StriderEntity, StriderEntityModel<StriderEntity>> {
    public StriderEntityRendererMixin(EntityRendererFactory.Context context, StriderEntityModel<StriderEntity> entityModel, float f) {
        super(context, entityModel, f);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void addFeature(EntityRendererFactory.Context context, CallbackInfo ci) {
        this.addFeature(new StriderHairFeatureRenderer(this, new StriderEntityModel<>(context.getPart(EntityModelLayers.STRIDER_SADDLE))));
    }
}
