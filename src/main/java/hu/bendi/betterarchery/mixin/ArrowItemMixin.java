package hu.bendi.betterarchery.mixin;

import hu.bendi.betterarchery.arrows.ArrowAttribute;
import hu.bendi.betterarchery.interfaces.PartsHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(ArrowItem.class)
public abstract class ArrowItemMixin extends Item {

    public ArrowItemMixin(Settings settings) {
        super(settings);
    }
    
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (world == null) return;
        if (!world.isClient) return;
        if (GLFW.glfwGetKey(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS) {
            if (stack.getSubNbt("ArrowData") != null) {
                var attr = ArrowAttribute.fromNbt(stack.getSubNbt("ArrowData"));

                tooltip.add(Text.translatable("tooltip.arrow.damage").append(String.valueOf(attr.getDamage())));
                tooltip.add(Text.translatable("tooltip.arrow.speed").append(String.valueOf(attr.getSpeed())));
                tooltip.add(Text.translatable("tooltip.arrow.accuracy").append(String.valueOf(attr.getAccuracy())));
            }

            if (stack.getSubNbt("Parts") == null) return;
            var parts = PartsHolder.Parts.fromNbt(stack.getSubNbt("Parts"));
            tooltip.add(Text.literal(""));
            tooltip.add(Text.translatable("tooltip.arrow.parts"));
            tooltip.add(Text.literal(" - ").append(Text.translatable(parts.head).formatted(Formatting.GRAY)));
            tooltip.add(Text.literal(" - ").append(Text.translatable(parts.body).formatted(Formatting.GRAY)));
            tooltip.add(Text.literal(" - ").append(Text.translatable(parts.tail).formatted(Formatting.GRAY)));
        } else {
            tooltip.add(Text.translatable("tooltip.arrow.sneak").fillStyle(Style.EMPTY.withBold(true)));
        }
    }
}
