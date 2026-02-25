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

    public BiomeRegion() {
        super(new ResourceLocation(TerminaMod.MOD_ID, "overworld"), RegionType.OVERWORLD, 2);
    }

    @Override
    public void addBiomes(Registry<Biome> registry, Consumer<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> mapper) {
        // Valeurs EXACTES de jagged_peaks vanilla (source: Minecraft 1.20.1 BiomeBuilder)
        // C'est la zone la plus haute du jeu, Y200+
        // erosion: -1.0 à -0.375 (très escarpé)
        // continentalness: 0.3 à 1.0 (terres hautes)
        // weirdness: 0.05 à 1.0 (pics positifs) et -1.0 à -0.05 (pics négatifs)

        Climate.Parameter temp = Climate.Parameter.span(-0.45f, 0.55f);
        Climate.Parameter humidity = FULL_RANGE;
        Climate.Parameter cont = Climate.Parameter.span(0.3f, 1.0f);
        Climate.Parameter erosion = Climate.Parameter.span(-1.0f, -0.375f);

        // surface (depth=0)
        mapper.accept(Pair.of(Climate.parameters(
                        temp, humidity, cont, erosion,
                        Climate.Parameter.point(0.0f),
                        Climate.Parameter.span(0.05f, 1.0f), 0),
                RUBY_HIGHLANDS));

        mapper.accept(Pair.of(Climate.parameters(
                        temp, humidity, cont, erosion,
                        Climate.Parameter.point(0.0f),
                        Climate.Parameter.span(-1.0f, -0.05f), 0),
                RUBY_HIGHLANDS));

        // underground (depth=1) - requis pour que les pics soient solides
        mapper.accept(Pair.of(Climate.parameters(
                        temp, humidity, cont, erosion,
                        Climate.Parameter.point(1.0f),
                        Climate.Parameter.span(0.05f, 1.0f), 0),
                RUBY_HIGHLANDS));

        mapper.accept(Pair.of(Climate.parameters(
                        temp, humidity, cont, erosion,
                        Climate.Parameter.point(1.0f),
                        Climate.Parameter.span(-1.0f, -0.05f), 0),
                RUBY_HIGHLANDS));
    }
}