package com.terminapaul.terminamod.client.renderer;

import com.terminapaul.terminamod.TerminaMod;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class SmelterModelLayers {
    public static final ModelLayerLocation SMELTER =
            new ModelLayerLocation(
                    new ResourceLocation(TerminaMod.MOD_ID, "smelter"), "main");
}