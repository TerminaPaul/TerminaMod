package com.terminapaul.terminamod.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.terminapaul.terminamod.TerminaMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

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

        // Fond principal
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Barre de progression (flèche) : 34px de large
        int progress = menu.getProgress();
        int maxProgress = menu.getMaxProgress();
        if (maxProgress > 0 && progress > 0) {
            int progressWidth = (int) (34.0 * progress / maxProgress);
            graphics.blit(TEXTURE, x + 71, y + 44, 176, 0, progressWidth, 8);
        }

        // Barre FE horizontale : remplit de gauche à droite (160px max)
        int energy = menu.getEnergy();
        int maxEnergy = menu.getMaxEnergy();
        if (maxEnergy > 0) {
            int energyWidth = (int) (160.0 * energy / maxEnergy);
            if (energyWidth > 0) {
                graphics.blit(TEXTURE, x + 8, y + 91, 8, 200, energyWidth, 5);
            }
        }

        // Gauge minerais avec couleur dynamique selon la recette
        int nuggets = menu.getNuggetCount();
        int required = menu.getRequiredCount();
        if (nuggets > 0 && required > 0) {
            int fillHeight = (int) (64.0 * nuggets / required);
            fillHeight = Math.min(fillHeight, 64);

            // Extraire les composantes RGB de la couleur de la recette
            int color = menu.getColor();
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;

            RenderSystem.setShaderColor(r, g, b, 1f);
            graphics.blit(TEXTURE, x + 8, y + 20 + (64 - fillHeight), 176, 16, 16, fillHeight);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f); // reset couleur
        }
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

        // Tooltip nuggets
        if (mouseX >= x + 8 && mouseX <= x + 60 && mouseY >= y + 49 && mouseY <= y + 89) {
            graphics.renderTooltip(font,
                    Component.literal(menu.getNuggetCount() + " / " + menu.getRequiredCount()),
                    mouseX, mouseY);
        }
    }
}