package com.terminapaul.terminamod.entity;

import com.terminapaul.terminamod.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.properties.BedPart;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SmelterEntity extends PathfinderMob implements Merchant {

    // ── Niveaux (comme les villageois) ─────────────────────────────────────────
    private static final int[] LEVEL_XP_THRESHOLDS = { 0, 10, 70, 150, 250 };    // XP requis pour passer au niveau suivant
    public static final int MAX_LEVEL = 5;

    // ── Registre global des lits réservés ──────────────────────────────────────
    private static final Map<BlockPos, UUID> CLAIMED_BEDS = new ConcurrentHashMap<>();

    public static void onBedBroken(BlockPos pos) { CLAIMED_BEDS.remove(pos); }
    public static boolean isBedAvailable(BlockPos pos) { return !CLAIMED_BEDS.containsKey(pos); }

    // ── Champs instance ────────────────────────────────────────────────────────
    @Nullable private Player tradingPlayer;
    @Nullable private MerchantOffers offers;
    private int xp = 0;
    private int villagerLevel = 1;
    @Nullable private BlockPos homePos = null;

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

    // ── Goals ──────────────────────────────────────────────────────────────────

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SmelterUseBedGoal(this));
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Zombie.class, 10.0f, 0.6, 0.8));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, net.minecraft.world.entity.monster.Husk.class, 10.0f, 0.6, 0.8));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, net.minecraft.world.entity.monster.Drowned.class, 10.0f, 0.6, 0.8));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, net.minecraft.world.entity.monster.Pillager.class, 10.0f, 0.6, 0.8));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, net.minecraft.world.entity.monster.Vindicator.class, 10.0f, 0.6, 0.8));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, net.minecraft.world.entity.monster.Evoker.class, 10.0f, 0.6, 0.8));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, net.minecraft.world.entity.monster.Ravager.class, 12.0f, 0.6, 0.8));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, net.minecraft.world.entity.monster.Vex.class, 8.0f, 0.6, 0.8));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.5));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    // ── Système de niveaux ─────────────────────────────────────────────────────

    public int getVillagerLevel() { return villagerLevel; }

    /** Calcule le niveau en fonction de l'XP actuel. */
    private int computeLevel() {
        for (int i = LEVEL_XP_THRESHOLDS.length - 1; i >= 0; i--) {
            if (xp >= LEVEL_XP_THRESHOLDS[i]) return Math.min(i + 1, MAX_LEVEL);
        }
        return 1;
    }

    /** XP nécessaire pour le prochain niveau */
    public int getXpNeededForNextLevel() {
        if (villagerLevel >= MAX_LEVEL) return 0;
        return LEVEL_XP_THRESHOLDS[villagerLevel] - xp;
    }

    /** Appelé après chaque trade pour vérifier si on monte de niveau. */
    private void checkLevelUp() {
        int newLevel = computeLevel();
        if (newLevel > villagerLevel) {
            villagerLevel = newLevel;
            // Débloquer les nouveaux trades du niveau
            unlockTradesForLevel(villagerLevel);
            // Son de level-up
            this.level().playSound(null, this.blockPosition(),
                    SoundEvents.VILLAGER_WORK_WEAPONSMITH, this.getSoundSource(), 1.0f, 1.2f);
        }
    }

    /** Ajoute les trades du niveau indiqué aux offres existantes. */
    private void unlockTradesForLevel(int level) {
        if (offers == null) return;
        switch (level) {
            case 2 -> {
                offers.add(new MerchantOffer(
                        new ItemStack(ModItems.RUBY_INGOT.get(), 3),
                        new ItemStack(ModItems.RUBY_BLOCK_ITEM.get(), 1),
                        8, 10, 0.05f));
            }
            case 3 -> { /* trades niveau 3 */ }
            case 4 -> { /* trades niveau 4 */ }
            case 5 -> { /* trades niveau 5 */ }
        }
    }

    // ── Gestion du lit ─────────────────────────────────────────────────────────

    @Nullable public BlockPos getHomePos() { return homePos; }

    public void claimBed(BlockPos pos) {
        if (homePos != null) releaseBed();
        homePos = pos;
        CLAIMED_BEDS.put(pos, this.getUUID());
    }

    public void releaseBed() {
        if (homePos != null) { CLAIMED_BEDS.remove(homePos); homePos = null; }
    }

    public boolean isBedStillValid() {
        if (homePos == null) return false;
        return this.level().getBlockState(homePos).is(BlockTags.BEDS);
    }

    // ── Tick ───────────────────────────────────────────────────────────────────

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.tickCount % 20 == 0) {
            if (homePos != null && !isBedStillValid()) {
                if (this.isSleeping()) this.stopSleeping();
                releaseBed();
            }
        }
    }

    // ── Mort ───────────────────────────────────────────────────────────────────

    @Override
    public void die(DamageSource source) {
        super.die(source);
        releaseBed();
    }

    // ── Interaction ────────────────────────────────────────────────────────────

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide() && hand == InteractionHand.MAIN_HAND) {
            this.setTradingPlayer(player);
            this.openTradingScreen(player, this.getDisplayName(), villagerLevel);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }

    // ── Merchant ───────────────────────────────────────────────────────────────

    @Override public void setTradingPlayer(@Nullable Player p) { this.tradingPlayer = p; }
    @Nullable @Override public Player getTradingPlayer() { return this.tradingPlayer; }

    @Override
    public MerchantOffers getOffers() {
        if (this.offers == null) {
            this.offers = new MerchantOffers();
            buildInitialTrades();
        }
        return this.offers;
    }

    /** Trades disponibles dès le niveau 1. */
    private void buildInitialTrades() {
        // Niveau 1
        this.offers.add(new MerchantOffer(
                new ItemStack(ModItems.RUBY.get(), 5),
                new ItemStack(ModItems.RUBY_INGOT.get(), 1),
                12, 5, 0.05f));
        this.offers.add(new MerchantOffer(
                new ItemStack(ModItems.RUBY_NUGGET.get(), 10),
                new ItemStack(ModItems.RUBY.get(), 1),
                12, 5, 0.05f));
        // Si le Smelter est déjà niveau 2+ au chargement, débloquer les trades supérieurs
        for (int lvl = 2; lvl <= villagerLevel; lvl++) {
            unlockTradesForLevel(lvl);
        }
    }

    @Override public void overrideOffers(MerchantOffers o) { this.offers = o; }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        offer.increaseUses();
        this.xp += offer.getXp();
        checkLevelUp(); // vérifie montée de niveau après chaque trade
        this.level().playSound(null, this.blockPosition(),
                SoundEvents.VILLAGER_YES, this.getSoundSource(), 1.0f, 1.0f);
    }

    @Override public void notifyTradeUpdated(ItemStack stack) {}

    @Override public int getVillagerXp() { return this.xp; }
    @Override public void overrideXp(int xp) { this.xp = xp; }

    /* true = affiche la barre XP/niveau dans le GUI de trade. */
    @Override public boolean showProgressBar() { return true; }

    @Override public SoundEvent getNotifyTradeSound() { return SoundEvents.VILLAGER_YES; }
    @Override public boolean isClientSide() { return this.level().isClientSide(); }

    // ── Sons ───────────────────────────────────────────────────────────────────

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.VILLAGER_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.VILLAGER_HURT; }
    @Override protected SoundEvent getDeathSound() { return SoundEvents.VILLAGER_DEATH; }

    // ── NBT ────────────────────────────────────────────────────────────────────

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.offers != null) tag.put("Offers", this.offers.createTag());
        tag.putInt("Xp", this.xp);
        tag.putInt("VillagerLevel", this.villagerLevel);
        if (this.homePos != null) {
            tag.putInt("HomePosX", homePos.getX());
            tag.putInt("HomePosY", homePos.getY());
            tag.putInt("HomePosZ", homePos.getZ());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Offers")) this.offers = new MerchantOffers(tag.getCompound("Offers"));
        this.xp = tag.getInt("Xp");
        this.villagerLevel = tag.contains("VillagerLevel") ? tag.getInt("VillagerLevel") : 1;
        if (tag.contains("HomePosX")) {
            BlockPos saved = new BlockPos(tag.getInt("HomePosX"), tag.getInt("HomePosY"), tag.getInt("HomePosZ"));
            homePos = saved;
            CLAIMED_BEDS.put(saved, this.getUUID());
        }
    }

    // ── Goal de sommeil ────────────────────────────────────────────────────────

    static class SmelterUseBedGoal extends Goal {

        private static final int SEARCH_RADIUS   = 48;
        private static final int SEARCH_COOLDOWN = 200;

        private final SmelterEntity smelter;
        private int searchCooldown = 0;

        SmelterUseBedGoal(SmelterEntity smelter) {
            this.smelter = smelter;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
        }

        private boolean isNight() {
            long t = smelter.level().getDayTime() % 24000;
            return t >= 12000;
        }

        @Override
        public boolean canUse() {
            if (smelter.level().isClientSide()) return false;
            if (!isNight()) return false;
            if (smelter.isSleeping()) return true;
            if (smelter.homePos != null && smelter.isBedStillValid()) return true;
            if (searchCooldown > 0) { searchCooldown--; return false; }
            findAndClaimBed();
            searchCooldown = SEARCH_COOLDOWN;
            return smelter.homePos != null;
        }

        @Override
        public boolean canContinueToUse() {
            if (!isNight()) return false;
            return smelter.homePos != null && smelter.isBedStillValid();
        }

        @Override public void start() { walkToBed(); }

        @Override
        public void stop() {
            if (smelter.isSleeping()) smelter.stopSleeping();
        }

        @Override
        public void tick() {
            if (smelter.homePos == null) return;
            if (smelter.isSleeping()) return;

            double distSq = smelter.distanceToSqr(
                    smelter.homePos.getX() + 0.5,
                    smelter.homePos.getY(),
                    smelter.homePos.getZ() + 0.5);

            if (distSq < 2.25) {
                if (smelter.isBedStillValid()) {
                    smelter.getNavigation().stop();
                    smelter.startSleeping(smelter.homePos);
                }
            } else if (smelter.getNavigation().isDone()) {
                walkToBed();
            }
        }

        private void walkToBed() {
            if (smelter.homePos == null) return;
            smelter.getNavigation().moveTo(
                    smelter.homePos.getX() + 0.5,
                    smelter.homePos.getY(),
                    smelter.homePos.getZ() + 0.5,
                    0.6);
        }

        private void findAndClaimBed() {
            BlockPos origin = smelter.blockPosition();
            Level level = smelter.level();
            BlockPos best = null;
            double bestDist = Double.MAX_VALUE;

            for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx++) {
                for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz++) {
                    for (int dy = -3; dy <= 3; dy++) {
                        BlockPos pos = origin.offset(dx, dy, dz);
                        if (!level.isLoaded(pos)) continue;
                        var state = level.getBlockState(pos);
                        if (!state.is(BlockTags.BEDS)) continue;
                        BlockPos headPos = getHeadPos(pos, state);
                        if (headPos == null) continue;
                        if (!isBedAvailable(headPos)) continue;
                        double d = headPos.distSqr(origin);
                        if (d < bestDist) { bestDist = d; best = headPos.immutable(); }
                    }
                }
            }
            if (best != null) smelter.claimBed(best);
        }

        @Nullable
        private BlockPos getHeadPos(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
            try {
                BedPart part = state.getValue(BedBlock.PART);
                if (part == BedPart.HEAD) return pos;
                Direction facing = state.getValue(BedBlock.FACING);
                return pos.relative(facing);
            } catch (Exception e) { return null; }
        }

        private boolean isBedAvailable(BlockPos pos) {
            UUID owner = CLAIMED_BEDS.get(pos);
            return owner == null || owner.equals(smelter.getUUID());
        }
    }
}