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

    private static final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0f, 1.0f);
    private static final Climate.Parameter[] temperatures = new Climate.Parameter[]{
            Climate.Parameter.span(-1.0f, -0.45f),
            Climate.Parameter.span(-0.45f, -0.15f),
            Climate.Parameter.span(-0.15f, 0.2f),
            Climate.Parameter.span(0.2f, 0.55f),
            Climate.Parameter.span(0.55f, 1.0f)
    };
    private static final Climate.Parameter[] humidities = new Climate.Parameter[]{
            Climate.Parameter.span(-1.0f, -0.35f),
            Climate.Parameter.span(-0.35f, -0.1f),
            Climate.Parameter.span(-0.1f, 0.1f),
            Climate.Parameter.span(0.1f, 0.3f),
            Climate.Parameter.span(0.3f, 1.0f)
    };

    public BiomeRegion() {
        super(new ResourceLocation(TerminaMod.MOD_ID, "overworld"), RegionType.OVERWORLD, 2);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {

        // Montagnes très hautes avec falaises :
        // erosion très bas (-1.0 à -0.375) = terrain très escarpé, hautes falaises
        // continentalness très haut (0.5 à 1.0) = altitude maximale
        for (int i = 2; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                // Zone principale : très haute avec erosion minimale = falaises verticales
                this.addBiome(mapper,
                        Climate.parameters(
                                temperatures[i],
                                humidities[j],
                                Climate.Parameter.span(0.5f, 1.0f),     // continentalness max
                                Climate.Parameter.span(-1.0f, -0.375f), // erosion minimal = falaises
                                Climate.Parameter.point(0.0f),
                                FULL_RANGE,
                                0
                        ),
                        RUBY_HIGHLANDS
                );
            }
        }
    }
}