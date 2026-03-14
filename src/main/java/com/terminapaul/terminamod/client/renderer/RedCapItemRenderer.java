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

        // Scale plus grand que 0.0625 pour que le chapeau soit visible dans l'inventaire.
        // Le chapeau fait ~9 unités de large → avec scale 0.1 il fait 0.9 blocs → bien visible.
        // On centre manuellement en compensant le bone offset (0,-12,-9).
        float scale = 0.1f;

        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.scale(scale, -scale, -scale);

        // Compenser le bone offset (0,-12,-9) après scale:
        // Y: -12 * -scale = +1.2 → déjà compensé par le scale négatif
        // Z: -9 * -scale = +0.9 → déjà compensé
        // Les cubes sont à Y: -1 à 4, Z: 0 à 14 relatifs au bone
        // Centre cubes: Y ≈ 1.5, Z ≈ 7 → en monde: Y ≈ -0.15, Z ≈ -0.7
        // + bone: Y = 1.2 - 0.15 = 1.05, Z = 0.9 - 0.7 = 0.2
        // Pour centrer à 0: translate(0, -1.05/scale * ... )
        // Plus simple: on translate le bone offset directement en unités ModelPart
        poseStack.translate(0, -12, -9);  // annule le bone offset → chapeau centré à l'origine

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        getModel().renderToBuffer(poseStack, consumer, packedLight,
                OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

        poseStack.popPose();
    }
}