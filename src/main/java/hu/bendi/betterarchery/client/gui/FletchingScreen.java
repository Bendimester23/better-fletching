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
import net.minecraft.util.Formatting;
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
        if (handler.input.getStack(0).isEmpty()) this.drawTexture(matrices, i + 21, j + 25, 240, 16, 16, 16);
        if (handler.input.getStack(1).isEmpty()) this.drawTexture(matrices, i + 21, j + 46, 240, 32, 16, 16);
        if (handler.input.getStack(2).isEmpty()) this.drawTexture(matrices, i + 21, j + 67, 240, 48, 16, 16);
        if (getScreenHandler().hasSpectralUpgrade) {
            this.drawTexture(matrices, x - 53, y, 0, 188, 58, 57);
            float glowStoneLevel = getScreenHandler().getGlowStoneLevel();
            int startY = (int) (glowStoneLevel / 256.0f * 43.0f);
            this.drawTexture(matrices, x - 19, y + 7 + (43 - startY), 249, 213, 7, startY);
        }

    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE);

        drawButton(matrices, 0, true, mouseX, mouseY);
        drawButton(matrices, 1, getScreenHandler().hasSpectralUpgrade, mouseX, mouseY);
        drawButton(matrices, 2, false, mouseX, mouseY);

        if (getScreenHandler().hasSpectralUpgrade) {
            if (mouseX > x - 19 && mouseX < x - 11 && mouseY > y + 7 && mouseY < y + 50) {
                renderTooltip(matrices,
                        Text.translatable("tooltip.glowstone_level")
                                .append(Text.literal(getScreenHandler().getGlowStoneLevel() + "/256").formatted(Formatting.GRAY)
                                ), mouseX, mouseY);
            }
        }
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    private void drawButton(MatrixStack matrices, int i, boolean enabled, int mouseX, int mouseY) {
        if (getScreenHandler().getSelectedType() == i) {
            this.drawTexture(matrices, x + 150, y + 19 + (i * 23), 216, 0, 20, 20);
        } else if (enabled) {
            if (mouseX > x + 150 && mouseX < x + 170 && mouseY > y + 19 + (i * 23) && mouseY < y + 39 + (i * 23)) {
                this.drawTexture(matrices, x + 150, y + 19 + (i * 23), 216, 0, 20, 20);
            } else this.drawTexture(matrices, x + 150, y + 19 + (i * 23), 176, 0, 20, 20);
        } else this.drawTexture(matrices, x + 150, y + 19 + (i * 23), 196, 0, 20, 20);

        this.drawTexture(matrices, x + 152, y + 21 + (i * 23), 224, 32 + (i * 16), 16, 16);
    }

    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        super.onMouseClick(slot, slotId, button, actionType);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (mouseX < x + 150 || mouseX > x + 170) return super.mouseClicked(mouseX, mouseY, button);
            var i = (byte) ((mouseY - y - 19) / 23);
            if (i < 0 || i > 3) return super.mouseClicked(mouseX, mouseY, button);
            getScreenHandler().clientClickType(i);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
