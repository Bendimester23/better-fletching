package hu.bendi.betterarchery.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Objects;

import static net.minecraft.recipe.RecipeManager.deserialize;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {

    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    private boolean errored;
    @Shadow
    private Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipes;

    @Shadow
    private Map<Identifier, Recipe<?>> recipesById;

    /**
     * @author Bendi
     * @reason I don't know how to remove recipes
     */
    @Overwrite
    public void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler) {
        this.errored = false;
        Map<RecipeType<?>, ImmutableMap.Builder<Identifier, Recipe<?>>> map2 = Maps.newHashMap();
        ImmutableMap.Builder<Identifier, Recipe<?>> builder = ImmutableMap.builder();

        for (Map.Entry<Identifier, JsonElement> identifierJsonElementEntry : map.entrySet()) {
            Identifier identifier = identifierJsonElementEntry.getKey();
            if (Objects.equals(identifier.getNamespace(), "minecraft")) {
                if (Objects.equals(identifier.getPath(), "arrow")) continue;
                if (Objects.equals(identifier.getPath(), "spectral_arrow")) continue;
                if (Objects.equals(identifier.getPath(), "tipped_arrow")) continue;
            }

            try {
                Recipe<?> recipe = deserialize(identifier, JsonHelper.asObject(identifierJsonElementEntry.getValue(), "top element"));
                ((ImmutableMap.Builder) map2.computeIfAbsent(recipe.getType(), (recipeType) -> ImmutableMap.builder())).put(identifier, recipe);
                builder.put(identifier, recipe);
            } catch (IllegalArgumentException | JsonParseException var10) {
                LOGGER.error("Parsing error loading recipe {}", identifier, var10);
            }
        }

        this.recipes = map2.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (entryx) -> ((ImmutableMap.Builder) entryx.getValue()).build()));
        this.recipesById = builder.build();
        LOGGER.info("Loaded {} recipes", map2.size());
    }
}
