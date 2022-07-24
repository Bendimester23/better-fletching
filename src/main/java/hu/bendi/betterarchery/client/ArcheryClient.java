package hu.bendi.betterarchery.client;

import hu.bendi.betterarchery.ArcheryMod;
import hu.bendi.betterarchery.client.gui.FletchingScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

@Environment(EnvType.CLIENT)
public class ArcheryClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(ArcheryMod.FLETCHING_SCREEN_HANDLER, FletchingScreen::new);
    }
    
}
