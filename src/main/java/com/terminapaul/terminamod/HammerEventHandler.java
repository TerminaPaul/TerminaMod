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

    @SubscribeEvent
    public static void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        handleHammer(event.getEntity());
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        handleHammer(event.getEntity());
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

        // Fortune : chaque niveau donne une chance d'obtenir des nuggets bonus
        int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, held);
        int bonusNuggets = 0;
        for (int i = 0; i < rubyCount; i++) {
            if (fortuneLevel > 0 && level.random.nextInt(3) < fortuneLevel) {
                bonusNuggets++;
            }
        }

        level.playSound(null,
                target.blockPosition(),
                SoundEvents.ANVIL_USE,
                SoundSource.PLAYERS,
                0.8f, 1.2f
        );

        target.discard();

        int totalNuggets = rubyCount * 4 + bonusNuggets;
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