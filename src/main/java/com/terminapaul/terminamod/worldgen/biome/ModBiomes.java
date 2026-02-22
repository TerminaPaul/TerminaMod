package com.terminapaul.terminamod.worldgen.biome;

import com.terminapaul.terminamod.TerminaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.*;
import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModBiomes {
    public static final DeferredRegister<Biome> BIOMES =
            DeferredRegister.create(Registries.BIOME, TerminaMod.MOD_ID);

    public static final RegistryObject<Biome> RUBY_HIGHLANDS =
            BIOMES.register("ruby_highlands", ModBiomes::rubyHighlands);

    public static void register(IEventBus eventBus) {
        BIOMES.register(eventBus);
    }

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