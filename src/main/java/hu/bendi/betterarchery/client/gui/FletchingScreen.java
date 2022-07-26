package hu.bendi.betterarchery.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import hu.bendi.betterarchery.ArcheryMod;
import hu.bendi.betterarchery.screen.FletchingScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class FletchingScreen extends HandledScreen<FletchingScreenHandler> {
    private static final Identifier TEXTURE = ArcheryMod.i("textures/gui/container/fletching_table.png");

    public FletchingScreen(FletchingScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 188;
        this.playerInventoryTitleY += 22;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        this.renderBackground(matrices);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = this.x;
        int j = this.y;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        if (handler.input.getStack(0).isEmpty()) this.drawTexture(matrices, i+21, j+25, 240, 16, 16, 16);
        if (handler.input.getStack(1).isEmpty()) this.drawTexture(matrices, i+21, j+46, 240, 32, 16, 16);
        if (handler.input.getStack(2).isEmpty()) this.drawTexture(matrices, i+21, j+67, 240, 48, 16, 16);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        super.onMouseClick(slot, slotId, button, actionType);
    }
}
