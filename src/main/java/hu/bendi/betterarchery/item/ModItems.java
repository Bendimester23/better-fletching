package hu.bendi.betterarchery.item;

import hu.bendi.betterarchery.ArcheryMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class ModItems {
    public static final Item TINY_FLINT = new Item(new FabricItemSettings().group(ArcheryMod.ARCHERY_TAB));

    public static final Item STRIDER_HAIR = new Item(new FabricItemSettings().group(ArcheryMod.ARCHERY_TAB).fireproof());
    public static final Item STRIDER_FLETCHING = new Item(new FabricItemSettings().group(ArcheryMod.ARCHERY_TAB).fireproof());

    public static final Item BONE_STICK = new Item(new FabricItemSettings().group(ArcheryMod.ARCHERY_TAB));

    public static final Item SPECTRAL_UPGRADE = new Item(new FabricItemSettings().group(ArcheryMod.ARCHERY_TAB));

    public static void register() {
        Registry.register(Registry.ITEM, ArcheryMod.i("tiny_flint"), TINY_FLINT);
        Registry.register(Registry.ITEM, ArcheryMod.i("strider_hair"), STRIDER_HAIR);
        Registry.register(Registry.ITEM, ArcheryMod.i("strider_fletching"), STRIDER_FLETCHING);
        Registry.register(Registry.ITEM, ArcheryMod.i("bone_stick"), BONE_STICK);
        Registry.register(Registry.ITEM, ArcheryMod.i("spectral_upgrade"), SPECTRAL_UPGRADE);
    }
}
