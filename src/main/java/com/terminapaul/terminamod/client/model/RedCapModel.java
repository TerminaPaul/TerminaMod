package com.terminapaul.terminamod.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import com.terminapaul.terminamod.entity.SmelterEntity;

public class RedCapModel extends EntityModel<SmelterEntity> {

    private final ModelPart bone;

    public RedCapModel(ModelPart root) {
        this.bone = root.getChild("bone");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // Code généré directement par Blockbench Export Java Entity
        partdefinition.addOrReplaceChild("bone",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.5F, -1.0F, 5.0F, 9.0F, 4.0F, 9.0F, new CubeDeformation(0.0F))
                        .texOffs(13, 13).addBox(-4.5F, 3.0F, 0.0F, 9.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 0).addBox(-1.0F, -1.5F, 8.5F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -12.0F, -9.0F, -0.1745F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 96, 96);
    }

    @Override
    public void setupAnim(SmelterEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {}

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}