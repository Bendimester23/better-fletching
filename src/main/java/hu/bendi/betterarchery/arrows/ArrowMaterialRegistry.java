package hu.bendi.betterarchery.arrows;

import java.util.HashMap;
import java.util.Map;

import hu.bendi.betterarchery.ArcheryMod;
import hu.bendi.betterarchery.item.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class ArrowMaterialRegistry {
    private static final Map<Item, ArrowAttribute> HEAD_MATERIALS = new HashMap<>();
    private static final Map<Item, ArrowAttribute> BODY_MATERIALS = new HashMap<>();
    private static final Map<Item, ArrowAttribute> TAIL_MATERIALS = new HashMap<>();

    private static boolean init;

    public static void registerHeadMaterial(Item item, ArrowAttribute attribute) {
        HEAD_MATERIALS.put(item, attribute);
    }

    public static void registerBodyMaterial(Item item, ArrowAttribute attribute) {
        BODY_MATERIALS.put(item, attribute);
    }

    public static void registerTailMaterial(Item item, ArrowAttribute attribute) {
        TAIL_MATERIALS.put(item, attribute);
    }

    public static void registerDefaultMaterials() {
        if (init) throw new IllegalStateException("Allready registered");
        init = true;

        ArcheryMod.LOGGER.info("Registering default arrow materials.");
        //Heads
        registerHeadMaterial(Items.FLINT, ArrowAttribute.Builder.create().setDamage(1).build());
        registerHeadMaterial(ModItems.TINY_FLINT, ArrowAttribute.Builder.create().setDamage(3).setSpeedMultiplier(3).build());
        registerHeadMaterial(Items.COBBLESTONE, ArrowAttribute.Builder.create().setDamage(20).setSpeedMultiplier(.2f).build());

        //Bodies
        registerBodyMaterial(Items.STICK, ArrowAttribute.Builder.create().setDamage(.2f).build());

        //Tails
        registerTailMaterial(Items.FEATHER, ArrowAttribute.Builder.create().setDamage(0).build());
        registerTailMaterial(Items.TORCH, ArrowAttribute.Builder.create().setDamage(0).setAccuracy(0).build());
    }

    public static ArrowAttribute getHeadMaterial(Item item) {
        return HEAD_MATERIALS.get(item);
    }

    public static ArrowAttribute getBodyMaterial(Item item) {
        return BODY_MATERIALS.get(item);
    }

    public static ArrowAttribute getTailMaterial(Item item) {
        return TAIL_MATERIALS.get(item);
    }
}
