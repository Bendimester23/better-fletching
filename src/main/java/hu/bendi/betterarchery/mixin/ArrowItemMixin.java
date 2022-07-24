package hu.bendi.betterarchery.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;

import hu.bendi.betterarchery.arrows.ArrowAttribute;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

@Mixin(ArrowItem.class)
public abstract class ArrowItemMixin extends Item {

    public ArrowItemMixin(Settings settings) {
        super(settings);
    }
    
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        if (stack.getSubNbt("ArrowData") == null) return;
        var attr = ArrowAttribute.fromNbt(stack.getSubNbt("ArrowData"));

        tooltip.add(new LiteralText("Damage: " + attr.getDamage()));
        tooltip.add(new LiteralText("Speed: " + attr.getSpeedMultiplier()));
        tooltip.add(new LiteralText("Range: +" + attr.getSpeedMultiplier()));
        tooltip.add(new LiteralText("Accuracy: +" + attr.getAccuracy()));
        
    }
}
