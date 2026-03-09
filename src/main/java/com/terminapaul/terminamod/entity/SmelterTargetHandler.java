package com.terminapaul.terminamod.entity;

import com.terminapaul.terminamod.TerminaMod;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.raid.Raider;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TerminaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SmelterTargetHandler {

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;

        var entity = event.getEntity();

        // Zombies et variantes
        if (entity instanceof Zombie zombie) {
            zombie.targetSelector.addGoal(2,
                    new NearestAttackableTargetGoal<>(zombie, SmelterEntity.class, false));
        }

        // Husk (zombie du désert)
        if (entity instanceof Husk husk) {
            husk.targetSelector.addGoal(2,
                    new NearestAttackableTargetGoal<>(husk, SmelterEntity.class, false));
        }

        // Drowned (zombie aquatique)
        if (entity instanceof Drowned drowned) {
            drowned.targetSelector.addGoal(2,
                    new NearestAttackableTargetGoal<>(drowned, SmelterEntity.class, false));
        }

        // Pillagers
        if (entity instanceof Pillager pillager) {
            pillager.targetSelector.addGoal(2,
                    new NearestAttackableTargetGoal<>(pillager, SmelterEntity.class, false));
        }

        // Vindicators
        if (entity instanceof Vindicator vindicator) {
            vindicator.targetSelector.addGoal(2,
                    new NearestAttackableTargetGoal<>(vindicator, SmelterEntity.class, false));
        }

        // Evokers
        if (entity instanceof Evoker evoker) {
            evoker.targetSelector.addGoal(2,
                    new NearestAttackableTargetGoal<>(evoker, SmelterEntity.class, false));
        }

        // Ravagers
        if (entity instanceof Ravager ravager) {
            ravager.targetSelector.addGoal(2,
                    new NearestAttackableTargetGoal<>(ravager, SmelterEntity.class, false));
        }

        // Illusioners (rare mais présents dans le code vanilla)
        if (entity instanceof Illusioner illusioner) {
            illusioner.targetSelector.addGoal(2,
                    new NearestAttackableTargetGoal<>(illusioner, SmelterEntity.class, false));
        }

        // Piglins (attaquent les villageois dans le Nether)
        if (entity instanceof Piglin piglin) {
            piglin.targetSelector.addGoal(2,
                    new NearestAttackableTargetGoal<>(piglin, SmelterEntity.class, false));
        }
    }
}