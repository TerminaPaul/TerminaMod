package com.terminapaul.terminamod.worldgen.biome;

import com.terminapaul.terminamod.TerminaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

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
        spawnBuilder.addSpawn(MobCategory.CREATURE,
                new MobSpawnSettings.SpawnerData(EntityType.SHEEP, 12, 4, 4));
        spawnBuilder.addSpawn(MobCategory.CREATURE,
                new MobSpawnSettings.SpawnerData(EntityType.COW, 10, 4, 4));
        spawnBuilder.addSpawn(MobCategory.CREATURE,
                new MobSpawnSettings.SpawnerData(EntityType.PIG, 10, 4, 4));
        spawnBuilder.addSpawn(MobCategory.MONSTER,
                new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, 95, 4, 4));
        spawnBuilder.addSpawn(MobCategory.MONSTER,
                new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 100, 4, 4));
        spawnBuilder.addSpawn(MobCategory.MONSTER,
                new MobSpawnSettings.SpawnerData(EntityType.CREEPER, 100, 4, 4));

        BiomeGenerationSettings.PlainBuilder genBuilder = new BiomeGenerationSettings.PlainBuilder();

        return new Biome.BiomeBuilder()
                .hasPrecipitation(true)
                .temperature(0.25f)
                .downfall(0.4f)
                .specialEffects(new BiomeSpecialEffects.Builder()
                        .skyColor(0x84BFFF)
                        .fogColor(0xC0D8FF)
                        .waterColor(0x3F76E4)
                        .waterFogColor(0x050533)
                        .grassColorOverride(0x5DEB3A)   // vert pomme vif
                        .foliageColorOverride(0x4AC41E)
                        .build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(genBuilder.build())
                .build();
    }
}