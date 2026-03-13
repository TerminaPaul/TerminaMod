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

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        // ── CORPS pivot (0,0,0) ────────────────────────────────────────────────
        // body   BB to_Y=24 → y=24-24=0
        // jacket BB to_Y=24 → y=24-24=0
        root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(16, 20).addBox(-4,  0, -3,  8, 12, 6)
                        .texOffs( 0, 38).addBox(-4,  0, -3,  8, 20, 6, new CubeDeformation(0.5f)),
                PartPose.ZERO);

        // ── TÊTE pivot (0,0,0) ─────────────────────────────────────────────────
        // head BB to_Y=34 → y=24-34=-10
        // nose BB to_Y=27 → y=24-27=-3
        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs( 0,  0).addBox(-4, -10, -4,  8, 10,  8)
                        .texOffs(24,  0).addBox(-1,  -3, -6,  2,  4,  2),
                PartPose.ZERO);

        // ── CHAPEAU enfant de head ─────────────────────────────────────────────
        // hat_main   BB to_Y=37 → y=24-37=-13
        // hat_pompon BB to_Y=37.5 → y=24-37.5=-13.5
        // hat_brim   BB to_Y=34 → y=24-34=-10
        head.addOrReplaceChild("hat",
                CubeListBuilder.create()
                        .texOffs(32,  0).addBox(-4.5f, -13,    -4,    9, 4, 9)
                        .texOffs( 0,  0).addBox(  -1,  -13.5f, -0.5f, 2, 1, 2)
                        .texOffs(45, 13).addBox(-4.5f, -9,    -9,    9, 1, 5), // X Y Z largeur hauteur profondeur
                PartPose.offsetAndRotation(0, 1, -3, //x y z (- = gauche monte devant)
                        (float) Math.toRadians(-15), 0, 0));

        // ── BRAS pivot (0, 3, -1) ──────────────────────────────────────────────
        // pivot à y=3 = niveau des aisselles dans le repère inversé (24-21=3)
        // arms_cross BB to_Y=19 → y=24-19=5,  rel au pivot: 5-3=2  → addBox y=2
        // right_arm  BB to_Y=23 → y=24-23=1,  rel au pivot: 1-3=-2 → addBox y=-2
        // left_arm   BB to_Y=23 → y=24-23=1,  rel au pivot: 1-3=-2 → addBox y=-2
        root.addOrReplaceChild("arms",
                CubeListBuilder.create()
                        .texOffs(40, 38).addBox(-4,  2, -2,  8, 4, 4)
                        .texOffs(60, 22).addBox(-8, -2, -2,  4, 8, 4)
                        .texOffs(44, 22).addBox( 4, -2, -2,  4, 8, 4),
                PartPose.offsetAndRotation(0, 3, -1,
                        -0.75f, 0, 0));

        // ── JAMBE DROITE pivot (−2, 12, 0) ────────────────────────────────────
        // pivot à y=12 = niveau des hanches dans le repère inversé (24-12=12)
        // leg0 BB to_Y=12 → y=24-12=12, rel au pivot: 12-12=0 → addBox y=0
        root.addOrReplaceChild("leg0",
                CubeListBuilder.create()
                        .texOffs(0, 22).addBox(-2, 0, -2,  4, 12, 4),
                PartPose.offset(-2, 12, 0));

        // ── JAMBE GAUCHE pivot (+2, 12, 0) ────────────────────────────────────
        root.addOrReplaceChild("leg1",
                CubeListBuilder.create()
                        .texOffs(0, 22).addBox(-2, 0, -2,  4, 12, 4),
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