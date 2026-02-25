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
    private static final SurfaceRules.RuleSource DEEPSLATE =
            SurfaceRules.state(Blocks.DEEPSLATE.defaultBlockState());

    private static final ResourceKey<NormalNoise.NoiseParameters> SURFACE_NOISE =
            ResourceKey.create(Registries.NOISE, new ResourceLocation("minecraft", "surface"));
    private static final ResourceKey<NormalNoise.NoiseParameters> NOODLE_NOISE =
            ResourceKey.create(Registries.NOISE, new ResourceLocation("minecraft", "noodle"));

    public static SurfaceRules.RuleSource makeRules() {

        SurfaceRules.ConditionSource isRubyHighlands = SurfaceRules.isBiome(
                BiomeRegion.RUBY_HIGHLANDS
        );

        // Transition stone/deepslate progressive
        SurfaceRules.RuleSource stoneOrDeepslate = SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.not(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(-8), 0)),
                        DEEPSLATE
                ),
                SurfaceRules.ifTrue(
                        SurfaceRules.not(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(8), 0)),
                        SurfaceRules.ifTrue(
                                SurfaceRules.noiseCondition(NOODLE_NOISE, -1.0, 0.0),
                                DEEPSLATE
                        )
                ),
                STONE
        );

        // Patches calcite/stone via bruit sur les flancs
        SurfaceRules.RuleSource calcitePatch = SurfaceRules.sequence(
                SurfaceRules.ifTrue(
                        SurfaceRules.noiseCondition(SURFACE_NOISE, -0.5, 0.5),
                        CALCITE
                ),
                STONE
        );

        return SurfaceRules.ifTrue(
                isRubyHighlands,
                SurfaceRules.sequence(

                        SurfaceRules.ifTrue(
                                SurfaceRules.yBlockCheck(VerticalAnchor.absolute(80), 0),
                                SurfaceRules.sequence(
                                        // Seulement 1 bloc de grass tout en haut
                                        SurfaceRules.ifTrue(
                                                SurfaceRules.abovePreliminarySurface(),
                                                SurfaceRules.ifTrue(
                                                        SurfaceRules.stoneDepthCheck(0, false, 0, CaveSurface.FLOOR),
                                                        GRASS
                                                )
                                        ),
                                        calcitePatch
                                )
                        ),
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
                                        stoneOrDeepslate
                                )
                        ),
                        stoneOrDeepslate
                )
        );
    }
}