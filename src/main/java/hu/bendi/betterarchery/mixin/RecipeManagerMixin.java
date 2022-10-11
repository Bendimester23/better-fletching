package hu.bendi.betterarchery.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {

    @Redirect(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;"))
    public <K, V> ImmutableMap.Builder<K, V> deleteArrowRecipes(ImmutableMap.Builder<K, V> instance, K key, V value) {
        var i = (Identifier) key;
        if (Objects.equals(i.getNamespace(), "minecraft")) {
            if (Objects.equals(i.getPath(), "arrow")) return instance;
            if (Objects.equals(i.getPath(), "spectral_arrow")) return instance;
            if (Objects.equals(i.getPath(), "tipped_arrow")) return instance;
        }

        instance.put(key, value);

        return instance;
    }
}
