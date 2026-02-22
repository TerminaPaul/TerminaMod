package com.terminapaul.terminamod;

import com.mojang.logging.LogUtils;
import com.terminapaul.terminamod.worldgen.ModWorldGen;
import com.terminapaul.terminamod.worldgen.biome.BiomeRegion;
import com.terminapaul.terminamod.worldgen.biome.ModBiomes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import terrablender.api.Regions;

@Mod(TerminaMod.MOD_ID)
public class TerminaMod {
    public static final String MOD_ID = "terminamod";
    private static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("'get()' is deprecated since version 1.21.1 and marked for removal")
    public TerminaMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModWorldGen.register(modEventBus);
        ModBiomes.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> Regions.register(new BiomeRegion()));
    }
}