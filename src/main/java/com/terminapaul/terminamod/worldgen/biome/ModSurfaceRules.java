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

        // Transition stone/deepslate progressive -8 / 8
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

        // calcite/stone
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

                        // Au dessus de Y=80 ET en surface → calcite/stone sauf 1 grass au sommet
                        SurfaceRules.ifTrue(
                                SurfaceRules.yBlockCheck(VerticalAnchor.absolute(80), 0),
                                SurfaceRules.ifTrue(
                                        SurfaceRules.abovePreliminarySurface(), // <-- seulement en surface, pas les grottes
                                        SurfaceRules.sequence(
                                                // 1 bloc de grass tout en haut
                                                SurfaceRules.ifTrue(
                                                        SurfaceRules.stoneDepthCheck(0, false, 0, CaveSurface.FLOOR),
                                                        GRASS
                                                ),
                                                // Flancs → calcite/stone
                                                calcitePatch
                                        )
                                )
                        ),

                        // En dessous de Y=80 : surface normale grass + dirt
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