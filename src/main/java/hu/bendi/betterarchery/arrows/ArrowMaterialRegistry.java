package hu.bendi.betterarchery.arrows;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hu.bendi.betterarchery.ArcheryMod;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ArrowMaterialRegistry implements SimpleSynchronousResourceReloadListener {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private static Map<ArrowMaterial.Part, Map<Item, ArrowAttribute>> materials;

    public static ArrowAttribute getHeadMaterial(Item item) {
        return materials.get(ArrowMaterial.Part.HEAD).get(item);
    }

    public static ArrowAttribute getBodyMaterial(Item item) {
        return materials.get(ArrowMaterial.Part.BODY).get(item);
    }

    public static ArrowAttribute getTailMaterial(Item item) {
        return materials.get(ArrowMaterial.Part.TAIL).get(item);
    }

    @Override
    public Identifier getFabricId() {
        return ArcheryMod.i("arrow_material");
    }

    @Override
    public void reload(ResourceManager manager) {
        var builder = new ImmutableMap.Builder<ArrowMaterial.Part, Map<Item, ArrowAttribute>>();
        for (ArrowMaterial.Part part : ArrowMaterial.Part.values()) {
            builder.put(part, new HashMap<>());
        }
        materials = builder.build();

        for (Identifier id : manager.findResources("arrow_materials", path -> path.endsWith(".json"))) {
            try (InputStream stream = manager.getResource(id).getInputStream()) {
                var material = GSON.fromJson(new String(stream.readAllBytes()), ArrowMaterial.class);
                materials.get(material.getPart()).put(Registry.ITEM.get(Identifier.tryParse(material.getItem())), material.getAttributes());
            } catch (Exception e) {
                ArcheryMod.LOGGER.error("Error occurred while loading resource json " + id, e);
            }
        }
        AtomicInteger count = new AtomicInteger();
        materials.forEach((part, itemArrowAttributeMap) -> itemArrowAttributeMap.forEach((item, attribute) -> count.incrementAndGet()));

        ArcheryMod.LOGGER.info("Loaded {} arrow materials", count.get());
    }
}
