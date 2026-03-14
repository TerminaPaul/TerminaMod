package com.terminapaul.terminamod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.terminapaul.terminamod.TerminaMod;
import com.terminapaul.terminamod.client.model.RedCapModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RedCapItemRenderer extends BlockEntityWithoutLevelRenderer {

    public static final ModelLayerLocation RED_CAP_LAYER =
            new ModelLayerLocation(new ResourceLocation(TerminaMod.MOD_ID, "red_cap"), "main");

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(TerminaMod.MOD_ID, "textures/models/armor/red_cap_layer_1.png");

    private RedCapModel model;

    public RedCapItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    private RedCapModel getModel() {
        if (model == null) {
            model = new RedCapModel(
                    Minecraft.getInstance().getEntityModels().bakeLayer(RED_CAP_LAYER));
        }
        return model;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context,
                             PoseStack poseStack, MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        poseStack.pushPose();

        // Le bone a PartPose offset (0, -12, -9).
        // Après scale(0.0625), ces offsets deviennent (0, -0.75, -0.5625) en blocs.
        // On compense pour centrer le chapeau dans l'espace item.
        // L'espace item va de 0 à 1, on veut le chapeau visible au centre.
        poseStack.translate(0.5, 1.25, 1.0);
        poseStack.scale(0.0625f, -0.0625f, -0.0625f);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        getModel().renderToBuffer(poseStack, consumer, packedLight,
                OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

        poseStack.popPose();
    }
}