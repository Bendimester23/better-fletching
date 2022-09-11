package hu.bendi.betterarchery.network;

import hu.bendi.betterarchery.ArcheryMod;
import hu.bendi.betterarchery.screen.FletchingScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class ModNetworking {

    public static final Identifier FLETCHING_SET_TYPE_PACKET = ArcheryMod.i("fletching_set_type_packet");

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(FLETCHING_SET_TYPE_PACKET, (server, player, handler, buf, responseSender) -> {
            if (player.currentScreenHandler == null) return;
            if (player.currentScreenHandler.syncId != buf.readInt()) return;
            ((FletchingScreenHandler) player.currentScreenHandler).setSelectedType(buf.readByte());
        });

        ClientPlayNetworking.registerGlobalReceiver(FLETCHING_SET_TYPE_PACKET, (client, handler, buf, responseSender) -> {
            if (client.player == null) return;
            if (client.player.currentScreenHandler == null) return;
            if (client.player.currentScreenHandler.syncId != buf.readInt()) return;
            client.player.playSound(SoundEvents.UI_BUTTON_CLICK, SoundCategory.BLOCKS, 1.0f, 1.0f);
            ((FletchingScreenHandler) client.player.currentScreenHandler).setSelectedType(buf.readByte());
        });
    }
}
