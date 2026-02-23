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

    // Paramètres Climate copiés exactement depuis OverworldBiomeBuilder vanilla
    // Ces valeurs correspondent aux zones "meadow" et "cherry grove" en montagne
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

        // Utilise les paramètres exacts de meadow/cherry grove vanilla (temperature 2-3, humidity 1-3)
        // mais avec continentalness et erosion de haute montagne
        // temperature index 2 = tempéré (-0.15 à 0.2), index 3 = chaud (0.2 à 0.55)
        // humidity index 1-3 = modéré

        for (int i = 2; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                this.addBiome(mapper,
                        Climate.parameters(
                                temperatures[i],
                                humidities[j],
                                Climate.Parameter.span(0.3f, 1.0f),    // continentalness : hautes terres
                                Climate.Parameter.span(-0.375f, -0.2225f), // erosion : plateaux/peaks vanilla
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