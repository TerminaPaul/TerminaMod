package com.terminapaul.terminamod.entity;

import com.terminapaul.terminamod.TerminaMod;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TerminaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BedBreakHandler {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!event.getState().is(BlockTags.BEDS)) return;

        BlockPos pos = event.getPos();
        SmelterEntity.onBedBroken(pos);
    }
}