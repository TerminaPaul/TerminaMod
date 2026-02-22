package com.terminapaul.terminamod.worldgen;

import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import com.terminapaul.terminamod.TerminaMod;
import com.terminapaul.terminamod.worldgen.biome.ModBiomes;

public class ModWorldGen {
    public static final DeferredRegister<Biome> BIOMES =
            DeferredRegister.create(Registries.BIOME, TerminaMod.MOD_ID);

    public static final RegistryObject<Biome> RUBY_HIGHLANDS =
            BIOMES.register("ruby_highlands", ModBiomes::rubyHighlands);

    public static void register(IEventBus eventBus) {
        BIOMES.register(eventBus);
    }
}