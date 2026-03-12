package com.terminapaul.terminamod.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.terminapaul.terminamod.entity.SmelterEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class SmelterModel extends EntityModel<SmelterEntity> {

    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart arms;
    private final ModelPart leg0;
    private final ModelPart leg1;

    public SmelterModel(ModelPart root) {
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.arms = root.getChild("arms");
        this.leg0 = root.getChild("leg0");
        this.leg1 = root.getChild("leg1");
    }

    /**
     * Copie exacte du VillagerModel vanilla 1.20.1 (Mojmap décompilé),
     * avec le chapeau custom en plus.
     *
     * Système de coordonnées ModelPart Java :
     * - Y=0 = sol de l'entité
     * - Y positif = vers le HAUT
     * - Les pivots sont absolus depuis le sol
     * - addBox(x,y,z) : coordonnées relatives au pivot, Y+ = vers le haut
     */
    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // ── HEAD pivot (0, 24, 0) ──────────────────────────────────────────────
        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        // Tête : 8x10x8, posée sur les épaules
                        .texOffs(0, 0).addBox(-4, 0, -4, 8, 10, 8)
                        // Nez
                        .texOffs(24, 0).addBox(-1, 2, -6, 2, 4, 2),
                PartPose.offset(0, 24, 0));

        // Chapeau custom incliné -15° (relatif à la tête)
        // Les cubes du chapeau commencent à y=10 (dessus de la tête)
        head.addOrReplaceChild("hat",
                CubeListBuilder.create()
                        // Calotte : 9x4x9 posée sur la tête
                        .texOffs(32, 0).addBox(-4.5f, 10, -4, 9, 4, 9)
                        // Pompon : 2x1x2
                        .texOffs(0, 0).addBox(-1, 14, -0.5f, 2, 1, 2)
                        // Bord avant : 9x1x5
                        .texOffs(45, 13).addBox(-4.5f, 10, -9, 9, 1, 5),
                PartPose.offsetAndRotation(0, 0, 0,
                        (float) Math.toRadians(-15), 0, 0));

        // ── BODY pivot (0, 12, 0) ──────────────────────────────────────────────
        root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        // Corps : 8x12x6
                        .texOffs(16, 20).addBox(-4, 0, -3, 8, 12, 6)
                        // Veste : 8x20x6 inflate 0.5 (descend jusqu'en bas)
                        .texOffs(0, 38).addBox(-4, -6, -3, 8, 18, 6, new CubeDeformation(0.5f)),
                PartPose.offset(0, 12, 0));

        // ── ARMS pivot (0, 21, -1) rotation -43° X ────────────────────────────
        // Valeurs exactes du VillagerModel vanilla
        root.addOrReplaceChild("arms",
                CubeListBuilder.create()
                        // Barre transversale
                        .texOffs(40, 38).addBox(-4, 2, -2, 8, 4, 4)
                        // Bras droit
                        .texOffs(44, 22).addBox(-8, -2, -2, 4, 8, 4)
                        // Bras gauche
                        .texOffs(44, 22).addBox( 4, -2, -2, 4, 8, 4),
                PartPose.offsetAndRotation(0, 21, -1,
                        -0.75f, 0, 0));  // -0.75 rad = ~-43°, valeur exacte vanilla

        // ── LEGS pivot (-2/+2, 12, 0) ──────────────────────────────────────────
        root.addOrReplaceChild("leg0",
                CubeListBuilder.create()
                        .texOffs(0, 22).addBox(-2, -12, -2, 4, 12, 4),
                PartPose.offset(-2, 12, 0));

        root.addOrReplaceChild("leg1",
                CubeListBuilder.create()
                        .texOffs(0, 22).addBox(-2, -12, -2, 4, 12, 4),
                PartPose.offset(2, 12, 0));

        return LayerDefinition.create(mesh, 96, 96);
    }

    @Override
    public void setupAnim(SmelterEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180f);
        this.head.xRot = headPitch  * ((float) Math.PI / 180f);
        this.leg0.xRot =  (float) Math.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount;
        this.leg1.xRot = -(float) Math.cos(limbSwing * 0.6662f) * 1.4f * limbSwingAmount;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer,
                               int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        body.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        arms.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        leg0.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        leg1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}