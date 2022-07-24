package hu.bendi.betterarchery.item;

import hu.bendi.betterarchery.ArcheryMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.registry.Registry;

public class ModItems {
    public static final Item TINY_FLINT = new Item(new FabricItemSettings().group(ItemGroup.MATERIALS));

    public static void register() {
        Registry.register(Registry.ITEM, ArcheryMod.i("tiny_flint"), TINY_FLINT);
    }
}
