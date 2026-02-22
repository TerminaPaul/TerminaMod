package com.terminapaul.terminamod.worldgen.biome;

import com.terminapaul.terminamod.TerminaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.*;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;

@SuppressWarnings("'ResourceLocation(java.lang.String, java.lang.String)' is deprecated since version 1.20.6 and marked for removal")
public class ModBiomes {
    public static final ResourceKey<Biome> RUBY_HIGHLANDS =
            ResourceKey.create(Registries.BIOME,
                    new ResourceLocation(TerminaMod.MOD_ID, "ruby_highlands"));

    public static Biome rubyHighlands() {
        MobSpawnSettings.Builder spawnBuilder = new MobSpawnSettings.Builder();
        BiomeDefaultFeatures.commonSpawns(spawnBuilder);

        BiomeGenerationSettings.PlainBuilder genBuilder = new BiomeGenerationSettings.PlainBuilder();

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.3f)
                .downfall(0.4f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .skyColor(0x78A7FF)
                        .fogColor(0xC0D8FF)
                        .waterColor(0x3F76E4)
                        .waterFogColor(0x050533)
                        .grassColorOverride(0x7A0000)
                        .foliageColorOverride(0x7A0000)
                        .build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(genBuilder.build())
                .build();
    }
}