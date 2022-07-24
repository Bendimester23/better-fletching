package hu.bendi.betterarchery;

import net.fabricmc.api.ModInitializer;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.bendi.betterarchery.arrows.ArrowMaterialRegistry;
import hu.bendi.betterarchery.item.ModItems;
import hu.bendi.betterarchery.screen.FletchingScreenHandler;

public class ArcheryMod implements ModInitializer {
	public static final String MODID = "betterarchery";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	//Content stuff
	public static final ScreenHandlerType<FletchingScreenHandler> FLETCHING_SCREEN_HANDLER;

	static {
		FLETCHING_SCREEN_HANDLER = new ScreenHandlerType<FletchingScreenHandler>(FletchingScreenHandler::new);
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Making Minecraft Archery better since 2022!");

		Registry.register(Registry.SCREEN_HANDLER, i("fletching_screen"), FLETCHING_SCREEN_HANDLER);

		ArrowMaterialRegistry.registerDefaultMaterials();
		ModItems.register();
	}

	public static Identifier i(String name) {
		return new Identifier(MODID, name);
	}
}
