package com.terminapaul.terminamod.entity;

import com.terminapaul.terminamod.TerminaMod;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.core.Direction;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TerminaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BedBreakHandler {

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        BlockState state = event.getState();
        if (!state.is(BlockTags.BEDS)) return;

        BlockPos brokenPos = event.getPos();

        BlockPos headPos = getHeadPos(brokenPos, state);

        SmelterEntity.onBedBroken(headPos);
        SmelterEntity.onBedBroken(brokenPos);
    }

    private static BlockPos getHeadPos(BlockPos pos, BlockState state) {
        try {
            BedPart part = state.getValue(BedBlock.PART);
            if (part == BedPart.HEAD) return pos;
            Direction facing = state.getValue(BedBlock.FACING);
            return pos.relative(facing);
        } catch (Exception e) {
            return pos;
        }
    }
}