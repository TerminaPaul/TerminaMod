package com.terminapaul.terminamod.client.renderer;

import com.terminapaul.terminamod.TerminaMod;
import com.terminapaul.terminamod.client.model.SmelterModel;
import com.terminapaul.terminamod.entity.SmelterEntity;
import com.terminapaul.terminamod.client.renderer.SmelterModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SmelterRenderer extends MobRenderer<SmelterEntity, SmelterModel> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(TerminaMod.MOD_ID, "textures/entity/smelter.png");

    public SmelterRenderer(EntityRendererProvider.Context context) {
        super(context,
                new SmelterModel(context.bakeLayer(SmelterModelLayers.SMELTER)),
                0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(SmelterEntity entity) {
        return TEXTURE;
    }
}