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
import com.mojang.math.Axis;

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

        if (context == ItemDisplayContext.GROUND) {
            //Sur le Sol
            float tick = (float)(Minecraft.getInstance().level.getGameTime() % 360);
            float angle = tick * 2f;

            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
            poseStack.scale(0.6f, -0.6f, -0.6f);
            poseStack.translate(0, 0.5, 0);

        } else if (context == ItemDisplayContext.GUI) {
            //Inventaire
            poseStack.mulPose(Axis.XP.rotationDegrees(37.5f));
            poseStack.mulPose(Axis.YP.rotationDegrees(-35));
            poseStack.mulPose(Axis.ZP.rotationDegrees(10));
            poseStack.scale(1.0f, -1.0f, -1.0f);
            poseStack.translate(0.8f, -0.25, 0);

        } else if (context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.mulPose(Axis.YP.rotationDegrees(0));
            poseStack.mulPose(Axis.ZP.rotationDegrees(0));
            poseStack.scale(0.6f, -0.6f, -0.6f);
            poseStack.translate(0, 0.5, 0.5);

        } else if (context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Axis.XP.rotationDegrees(30));
            poseStack.mulPose(Axis.YP.rotationDegrees(-25));
            poseStack.scale(0.6f, -0.6f, -0.6f);
            poseStack.translate(0, 0, 0);

        } else {
            poseStack.translate(0.5, 0.5, 0.5);
            poseStack.mulPose(Axis.XP.rotationDegrees(30)); //30 //
            poseStack.mulPose(Axis.YP.rotationDegrees(-25)); //-45
            poseStack.mulPose(Axis.ZP.rotationDegrees(0));// oe non (roulil)
            poseStack.scale(0.6f, -0.6f, -0.6f);
            poseStack.translate(0, 0, 0);
        }

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        getModel().renderToBuffer(poseStack, consumer, packedLight,
                OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

        poseStack.popPose();
    }
}