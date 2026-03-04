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
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Fond principal
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // Barre de progression (flèche) : 24px de large
        int progress = menu.getProgress();
        int maxProgress = menu.getMaxProgress();
        if (maxProgress > 0 && progress > 0) {
            int progressWidth = (int) (24.0 * progress / maxProgress);
            graphics.blit(TEXTURE, x + 77, y + 67, 176, 0, progressWidth, 17);
        }

        // Barre FE horizontale : remplit de gauche à droite
        int energy = menu.getEnergy();
        int maxEnergy = menu.getMaxEnergy();
        if (maxEnergy > 0) {
            int energyWidth = (int) (52.0 * energy / maxEnergy);
            if (energyWidth > 0) {
                graphics.blit(TEXTURE, x + 8, y + 80, 176, 17, energyWidth, 5);
            }
        }

        // Visuel contenant nuggets : 16px large, 40px haute
        int nuggets = menu.getNuggetCount();
        if (nuggets > 0) {
            int fillHeight = (int) (40.0 * nuggets / 64);
            graphics.blit(TEXTURE, x + 44, y + 49 + (40 - fillHeight),
                    181, 17, 16, fillHeight);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Tooltip FE — zone de survol alignée sur la barre horizontale
        if (mouseX >= x + 8 && mouseX <= x + 60 && mouseY >= y + 80 && mouseY <= y + 85) {
            graphics.renderTooltip(font,
                    Component.literal(menu.getEnergy() + " / " + menu.getMaxEnergy() + " FE"),
                    mouseX, mouseY);
        }

        // Tooltip nuggets
        if (mouseX >= x + 44 && mouseX <= x + 60 && mouseY >= y + 49 && mouseY <= y + 89) {
            graphics.renderTooltip(font,
                    Component.literal(menu.getNuggetCount() + " / 64 fragments"),
                    mouseX, mouseY);
        }
    }
}