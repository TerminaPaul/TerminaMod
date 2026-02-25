package com.terminapaul.terminamod.worldgen.biome;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class ModSurfaceRules {

    private static final SurfaceRules.RuleSource GRASS =
            SurfaceRules.state(Blocks.GRASS_BLOCK.defaultBlockState());
    private static final SurfaceRules.RuleSource DIRT =
            SurfaceRules.state(Blocks.DIRT.defaultBlockState());
    private static final SurfaceRules.RuleSource STONE =
            SurfaceRules.state(Blocks.STONE.defaultBlockState());
    private static final SurfaceRules.RuleSource CALCITE =
            SurfaceRules.state(Blocks.CALCITE.defaultBlockState());

    private static final ResourceKey<NormalNoise.NoiseParameters> SURFACE_NOISE =
            ResourceKey.create(Registries.NOISE, new ResourceLocation("minecraft", "surface"));

    public static SurfaceRules.RuleSource makeRules() {

        SurfaceRules.ConditionSource isRubyHighlands = SurfaceRules.isBiome(
                BiomeRegion.RUBY_HIGHLANDS
        );

        // Patches naturels de calcite via bruit (-0.5 à 0.5 = ~50% calcite, ~50% stone)
        SurfaceRules.RuleSource steepLayers = SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.noiseCondition(SURFACE_NOISE, -0.5, 0.5),
                        CALCITE
                ),
                STONE
        );

        return SurfaceRules.ifTrue(
                isRubyHighlands,
                SurfaceRules.sequence(
                        // Parois raides → patches calcite/stone
                        SurfaceRules.ifTrue(SurfaceRules.steep(), steepLayers),
                        // Surface → grass + dirt
                        SurfaceRules.ifTrue(
                                SurfaceRules.abovePreliminarySurface(),
                                SurfaceRules.sequence(
                                        SurfaceRules.ifTrue(
                                                SurfaceRules.stoneDepthCheck(0, false, 0, CaveSurface.FLOOR),
                                                GRASS
                                        ),
                                        SurfaceRules.ifTrue(
                                                SurfaceRules.stoneDepthCheck(3, false, 0, CaveSurface.FLOOR),
                                                DIRT
                                        ),
                                        STONE
                                )
                        ),
                        STONE
                )
        );
    }
}