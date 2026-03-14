package com.terminapaul.terminamod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.terminapaul.terminamod.TerminaMod;
import com.terminapaul.terminamod.client.model.RedCapModel;
import com.terminapaul.terminamod.item.RedCapItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class RedCapArmorRenderer<T extends LivingEntity, M extends HumanoidModel<T>>
        extends RenderLayer<T, M> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(TerminaMod.MOD_ID, "textures/models/armor/red_cap_layer_1.png");

    private RedCapModel model;

    public RedCapArmorRenderer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    private RedCapModel getModel() {
        if (model == null) {
            model = new RedCapModel(
                    Minecraft.getInstance().getEntityModels()
                            .bakeLayer(RedCapItemRenderer.RED_CAP_LAYER));
        }
        return model;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       T entity, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {

        ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
        if (!(helmet.getItem() instanceof RedCapItem)) return;

        poseStack.pushPose();
        this.getParentModel().head.translateAndRotate(poseStack);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        getModel().renderToBuffer(poseStack, consumer, packedLight,
                OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

        poseStack.popPose();
    }
}