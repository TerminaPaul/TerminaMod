package com.terminapaul.terminamod.entity;

import com.terminapaul.terminamod.ModEntities;
import com.terminapaul.terminamod.ModItems;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class SmelterEntity extends PathfinderMob implements Merchant {

    @Nullable
    private Player tradingPlayer;
    @Nullable
    private MerchantOffers offers;
    private int xp = 0;

    public SmelterEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    // ── Attributs ──────────────────────────────────────────────────────────────

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FOLLOW_RANGE, 16.0);
    }

    // ── Goals IA ───────────────────────────────────────────────────────────────

    @Override
    protected void registerGoals() {
        // Priorité basse : flâner et regarder
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0f, 0.6, 0.8));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.5));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        // Fuir les mobs hostiles
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, net.minecraft.world.entity.monster.Monster.class, 10.0f, 0.6, 0.8));
    }

    // ── Interaction clic droit ─────────────────────────────────────────────────

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide() && hand == InteractionHand.MAIN_HAND) {
            this.setTradingPlayer(player);
            this.openTradingScreen(player, this.getDisplayName(), 1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }

    // ── Merchant interface ─────────────────────────────────────────────────────

    @Override
    public void setTradingPlayer(@Nullable Player player) {
        this.tradingPlayer = player;
    }

    @Nullable
    @Override
    public Player getTradingPlayer() {
        return this.tradingPlayer;
    }

    @Override
    public MerchantOffers getOffers() {
        if (this.offers == null) {
            this.offers = new MerchantOffers();
            buildTrades();
        }
        return this.offers;
    }

    private void buildTrades() {
        Random rand = new Random();

        // Niveau 1 — Novice
        this.offers.add(new MerchantOffer(
                new ItemStack(ModItems.IRON_STICK.get(), 5),
                new ItemStack(ModItems.RUBY.get(), 1),
                12, 5, 0.05f
        ));

        // Niveau 1 — Novice
        this.offers.add(new MerchantOffer(
                new ItemStack(ModItems.RUBY_NUGGET.get(), 10),
                new ItemStack(ModItems.IRON_STICK.get(), 2),
                12, 5, 0.05f
        ));

        // Niveau 2 — Apprenti
        this.offers.add(new MerchantOffer(
                new ItemStack(ModItems.RUBY_INGOT.get(), 3),
                new ItemStack(ModItems.OBSIDIAN_HAMMER.get(), 1),
                8, 10, 0.05f
        ));
    }

    @Override
    public void overrideOffers(MerchantOffers offers) {
        this.offers = offers;
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        offer.increaseUses();
        this.xp += offer.getXp();
        this.level().playSound(null, this.blockPosition(),
                SoundEvents.VILLAGER_YES, this.getSoundSource(), 1.0f, 1.0f);
    }

    @Override
    public void notifyTradeUpdated(ItemStack stack) {
        // pas de particules custom pour l'instant
    }

    @Override
    public int getVillagerXp() {
        return this.xp;
    }

    @Override
    public void overrideXp(int xp) {
        this.xp = xp;
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return SoundEvents.VILLAGER_YES;
    }

    @Override
    public boolean isClientSide() {
        return this.level().isClientSide();
    }

    // ── Sons ───────────────────────────────────────────────────────────────────

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }

    // ── NBT ────────────────────────────────────────────────────────────────────

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.offers != null) {
            tag.put("Offers", this.offers.createTag());
        }
        tag.putInt("Xp", this.xp);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Offers")) {
            this.offers = new MerchantOffers(tag.getCompound("Offers"));
        }
        this.xp = tag.getInt("Xp");
    }

}