package com.terminapaul.terminamod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.terminapaul.terminamod.TerminaMod;
import com.terminapaul.terminamod.entity.SmelterEntity;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;

public class SmelterRenderer extends MobRenderer<SmelterEntity, VillagerModel<SmelterEntity>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(TerminaMod.MOD_ID, "textures/entity/smelter.png");

    public SmelterRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)), 0.5f);
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
        this.addLayer(new CrossedArmsItemLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(SmelterEntity entity) {
        return TEXTURE;
    }
}