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

    private static final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0f, 1.0f);

    public BiomeRegion() {
        super(new ResourceLocation(TerminaMod.MOD_ID, "overworld"), RegionType.OVERWORLD, 2);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        Climate.Parameter temp = Climate.Parameter.span(-0.45f, 0.2f);
        Climate.Parameter humidity = Climate.Parameter.span(-0.45f, 0.2f);
        Climate.Parameter cont = Climate.Parameter.span(0.4f, 1.0f);
        Climate.Parameter erosion = Climate.Parameter.span(-1.0f, -0.7f);

        mapper.accept(Pair.of(Climate.parameters(
                        temp, humidity, cont, erosion,
                        Climate.Parameter.point(0.0f),
                        Climate.Parameter.span(0.05f, 1.0f), 0),
                CHERRY_PEAKS));

        mapper.accept(Pair.of(Climate.parameters(
                        temp, humidity, cont, erosion,
                        Climate.Parameter.point(0.0f),
                        Climate.Parameter.span(-1.0f, -0.05f), 0),
                CHERRY_PEAKS));

        mapper.accept(Pair.of(Climate.parameters(
                        temp, humidity, cont, erosion,
                        Climate.Parameter.point(1.0f),
                        Climate.Parameter.span(0.05f, 1.0f), 0),
                CHERRY_PEAKS));

        mapper.accept(Pair.of(Climate.parameters(
                        temp, humidity, cont, erosion,
                        Climate.Parameter.point(1.0f),
                        Climate.Parameter.span(-1.0f, -0.05f), 0),
                CHERRY_PEAKS));
    }
}