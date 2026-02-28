package com.terminapaul.terminamod;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = TerminaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HammerEventHandler {


    private static final int[][] FORTUNE_WEIGHTS = {
            // 1    2    3    4    5    6
            { 30,  28,  22,  13,   6,   1 }, // Fortune 0
            { 10,  12,  15,  20,  22,  21 }, // Fortune 1
            {  5,   8,  12,  20,  25,  30 }, // Fortune 2
            {  2,   3,   5,  10,  30,  50 }, // Fortune 3
    };

    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        handleHammer(event.getEntity());
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        handleHammer(event.getEntity());
    }

    private static int rollNuggets(Level level, int fortuneLevel) {
        int[] weights = FORTUNE_WEIGHTS[Math.min(fortuneLevel, 3)];
        int totalWeight = 0;
        for (int w : weights) totalWeight += w;

        int roll = level.random.nextInt(totalWeight);
        int cumulative = 0;
        for (int i = 0; i < weights.length; i++) {
            cumulative += weights[i];
            if (roll < cumulative) return i + 1;
        }
        return 4; // fallback
    }

    private static void handleHammer(Player player) {
        ItemStack held = player.getMainHandItem();
        if (!held.is(ModItems.OBSIDIAN_HAMMER.get())) return;
        if (player.level().isClientSide()) return;

        Level level = player.level();

        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getViewVector(1.0f);
        double reach = 4.0;
        Vec3 endPos = eyePos.add(lookVec.scale(reach));

        AABB searchBox = new AABB(eyePos, endPos).inflate(1.0);
        List<ItemEntity> rubies = level.getEntitiesOfClass(
                ItemEntity.class,
                searchBox,
                ie -> ie.getItem().is(ModItems.RUBY.get())
        );

        if (rubies.isEmpty()) return;

        ItemEntity target = null;
        double closestDist = Double.MAX_VALUE;

        for (ItemEntity ruby : rubies) {
            AABB hitbox = ruby.getBoundingBox().inflate(0.3);
            Optional<Vec3> hit = hitbox.clip(eyePos, endPos);
            if (hit.isPresent()) {
                double dist = eyePos.distanceToSqr(hit.get());
                if (dist < closestDist) {
                    closestDist = dist;
                    target = ruby;
                }
            }
        }

        if (target == null) return;

        int rubyCount = target.getItem().getCount();
        int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, held);

        int totalNuggets = 0;
        for (int i = 0; i < rubyCount; i++) {
            totalNuggets += rollNuggets(level, fortuneLevel);
        }

        level.playSound(null,
                target.blockPosition(),
                SoundEvents.ANVIL_USE,
                SoundSource.PLAYERS,
                0.8f, 1.2f
        );

        target.discard();

        ItemStack nuggets = new ItemStack(ModItems.RUBY_NUGGET.get(), totalNuggets);
        ItemEntity nuggetEntity = new ItemEntity(level,
                target.getX(), target.getY(), target.getZ(), nuggets);
        nuggetEntity.setPickUpDelay(10);
        level.addFreshEntity(nuggetEntity);

        for (int i = 0; i < rubyCount; i++) {
            if (held.isEmpty()) break;
            held.hurtAndBreak(1, player,
                    p -> p.broadcastBreakEvent(player.getUsedItemHand()));
        }
    }
}