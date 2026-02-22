package com.terminapaul.terminamod.worldgen.biome;

import com.mojang.datafixers.util.Pair;
import com.terminapaul.terminamod.TerminaMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.Region;
import terrablender.api.RegionType;

import java.util.function.Consumer;

public class BiomeRegion extends Region {

    public static final ResourceKey<Biome> RUBY_HIGHLANDS = ResourceKey.create(
            Registries.BIOME,
            new ResourceLocation(TerminaMod.MOD_ID, "ruby_highlands")
    );

    public BiomeRegion() {
        super(new ResourceLocation(TerminaMod.MOD_ID, "overworld"), RegionType.OVERWORLD, 2);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        this.addBiome(mapper,
                Climate.parameters(
                        Climate.Parameter.span(0.1f, 0.4f),
                        Climate.Parameter.span(-0.2f, 0.2f),
                        Climate.Parameter.span(-0.5f, -0.1f),
                        Climate.Parameter.span(-0.3f, 0.3f),
                        Climate.Parameter.point(0.0f),
                        Climate.Parameter.span(-1.0f, 1.0f),
                        0
                ),
                RUBY_HIGHLANDS
        );
    }
}