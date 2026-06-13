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

    public static final ResourceKey<Biome> CHERRY_PEAKS = ResourceKey.create(
            Registries.BIOME,
            new ResourceLocation(TerminaMod.MOD_ID, "cherry_peaks")
    );

    public BiomeRegion() {
        super(new ResourceLocation(TerminaMod.MOD_ID, "overworld"), RegionType.OVERWORLD, 1);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        Climate.Parameter temp     = Climate.Parameter.span(-0.2f, 0.1f);
        Climate.Parameter humidity = Climate.Parameter.span(-0.3f, 0.0f);
        Climate.Parameter cont     = Climate.Parameter.span(0.3f, 1.0f);
        Climate.Parameter erosion  = Climate.Parameter.span(-1.0f, -0.5f);

        mapper.accept(Pair.of(Climate.parameters(
                        temp, humidity, cont, erosion,
                        Climate.Parameter.point(0.0f),
                        Climate.Parameter.span(0.2f, 1.0f), 0),
                CHERRY_PEAKS));

        mapper.accept(Pair.of(Climate.parameters(
                        temp, humidity, cont, erosion,
                        Climate.Parameter.point(1.0f),
                        Climate.Parameter.span(0.2f, 1.0f), 0),
                CHERRY_PEAKS));
    }
}