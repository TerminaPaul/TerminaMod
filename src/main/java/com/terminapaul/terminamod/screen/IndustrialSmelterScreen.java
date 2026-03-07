package com.terminapaul.terminamod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.terminapaul.terminamod.TerminaMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.lwjgl.opengl.GL11;

public class IndustrialSmelterScreen extends AbstractContainerScreen<IndustrialSmelterMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(TerminaMod.MOD_ID, "textures/gui/industrial_smelter.png");

    public IndustrialSmelterScreen(IndustrialSmelterMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 200;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 107;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 1. Fond principal
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // 2. Barre de progression (flèche)
        int progress = menu.getProgress();
        int maxProgress = menu.getMaxProgress();
        if (maxProgress > 0 && progress > 0) {
            int progressWidth = (int) (34.0 * progress / maxProgress);
            graphics.blit(TEXTURE, x + 71, y + 44, 176, 0, progressWidth, 8);
        }

        // 3. Barre FE horizontale
        int energy = menu.getEnergy();
        int maxEnergy = menu.getMaxEnergy();
        if (maxEnergy > 0) {
            int energyWidth = (int) (160.0 * energy / maxEnergy);
            if (energyWidth > 0) {
                graphics.blit(TEXTURE, x + 8, y + 91, 8, 200, energyWidth, 5);
            }
        }

        // 4. Liquide animé
        int nuggets = menu.getNuggetCount();
        int required = menu.getRequiredCount();
        if (nuggets > 0 && required > 0) {
            int fillHeight = (int) (64.0 * nuggets / required);
            fillHeight = Math.min(fillHeight, 64);

            int color = menu.getColor();
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;

            TextureAtlasSprite waterSprite = Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(new ResourceLocation("minecraft", "block/water_still"));

            RenderSystem.setShaderColor(r, g, b, 1f);

            int gaugeX = x + 8;
            int gaugeTopY = y + 20 + (64 - fillHeight);

            // Scissor pour limiter le rendu à la zone de la gauge
            double scale = Minecraft.getInstance().getWindow().getGuiScale();
            int screenHeight = Minecraft.getInstance().getWindow().getHeight();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(
                    (int)(gaugeX * scale),
                    (int)(screenHeight - (gaugeTopY + fillHeight) * scale),
                    (int)(16 * scale),
                    (int)(fillHeight * scale)
            );

            for (int tileY = gaugeTopY; tileY < gaugeTopY + fillHeight; tileY += 16) {
                graphics.blit(gaugeX, tileY, 0, 16, 16, waterSprite);
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }


        graphics.blit(TEXTURE, x + 8, y + 20, 176, 16, 16, 64);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Tooltip FE
        if (mouseX >= x + 8 && mouseX <= x + 170 && mouseY >= y + 91 && mouseY <= y + 96) {
            graphics.renderTooltip(font,
                    Component.literal(menu.getEnergy() + " / " + menu.getMaxEnergy() + " FE"),
                    mouseX, mouseY);
        }

        // Tooltip gauge minerais
        if (mouseX >= x + 8 && mouseX <= x + 24 && mouseY >= y + 20 && mouseY <= y + 84) {
            graphics.renderTooltip(font,
                    Component.literal(menu.getNuggetCount() + " / " + menu.getRequiredCount()),
                    mouseX, mouseY);
        }
    }
}