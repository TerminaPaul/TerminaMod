package com.terminapaul.terminamod.entity;

import com.terminapaul.terminamod.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SmelterEntity extends PathfinderMob implements Merchant {

    // ── Registre global des lits réservés ──────────────────────────────────────
    // clé = BlockPos du lit, valeur = UUID du Smelter propriétaire
    // ConcurrentHashMap car accédé depuis tick() sur le thread serveur
    private static final Map<BlockPos, UUID> CLAIMED_BEDS = new ConcurrentHashMap<>();

    /** Appelé par BedBreakHandler quand un lit est cassé. */
    public static void onBedBroken(BlockPos pos) {
        CLAIMED_BEDS.remove(pos);
    }

    /** Un Smelter peut-il réclamer ce lit ? */
    public static boolean isBedAvailable(BlockPos pos) {
        return !CLAIMED_BEDS.containsKey(pos);
    }

    // ── Champs instance ────────────────────────────────────────────────────────

    @Nullable private Player tradingPlayer;
    @Nullable private MerchantOffers offers;
    private int xp = 0;
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
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0f, 0.6, 0.8));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this,
                net.minecraft.world.entity.monster.Monster.class, 10.0f, 0.6, 0.8));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.5));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    // ── Gestion du lit ─────────────────────────────────────────────────────────

    @Nullable
    public BlockPos getHomePos() { return homePos; }

    /** Réclame un lit — enregistre dans la map globale. */
    public void claimBed(BlockPos pos) {
        // Libérer l'ancien si on en avait un
        if (homePos != null) releaseBed();
        homePos = pos;
        CLAIMED_BEDS.put(pos, this.getUUID());
    }

    /** Libère le lit actuel. */
    public void releaseBed() {
        if (homePos != null) {
            CLAIMED_BEDS.remove(homePos);
            homePos = null;
        }
    }

    /** Vérifie que le bloc est toujours un lit. */
    public boolean isBedStillValid() {
        if (homePos == null) return false;
        return this.level().getBlockState(homePos).is(BlockTags.BEDS);
    }

    // ── Tick : surveiller si le lit est cassé ──────────────────────────────────

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.tickCount % 20 == 0) {
            if (homePos != null && !isBedStillValid()) {
                // Lit cassé : sortir proprement
                if (this.isSleeping()) this.stopSleeping();
                releaseBed(); // retire de la map + met homePos à null
            }
        }
    }

    // ── Mort : libérer le lit ──────────────────────────────────────────────────

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
            this.openTradingScreen(player, this.getDisplayName(), 1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide());
    }

    // ── Merchant ───────────────────────────────────────────────────────────────

    @Override public void setTradingPlayer(@Nullable Player p) { this.tradingPlayer = p; }
    @Nullable @Override public Player getTradingPlayer() { return this.tradingPlayer; }

    @Override
    public MerchantOffers getOffers() {
        if (this.offers == null) { this.offers = new MerchantOffers(); buildTrades(); }
        return this.offers;
    }

    private void buildTrades() {
        this.offers.add(new MerchantOffer(
                new ItemStack(ModItems.RUBY.get(), 5),
                new ItemStack(ModItems.RUBY_INGOT.get(), 1),
                12, 5, 0.05f));
        this.offers.add(new MerchantOffer(
                new ItemStack(ModItems.RUBY_NUGGET.get(), 10),
                new ItemStack(ModItems.RUBY.get(), 1),
                12, 5, 0.05f));
        this.offers.add(new MerchantOffer(
                new ItemStack(ModItems.RUBY_INGOT.get(), 3),
                new ItemStack(ModItems.RUBY_BLOCK_ITEM.get(), 1),
                8, 10, 0.05f));
    }

    @Override public void overrideOffers(MerchantOffers o) { this.offers = o; }
    @Override public void notifyTrade(MerchantOffer offer) {
        offer.increaseUses();
        this.xp += offer.getXp();
        this.level().playSound(null, this.blockPosition(),
                SoundEvents.VILLAGER_YES, this.getSoundSource(), 1.0f, 1.0f);
    }
    @Override public void notifyTradeUpdated(ItemStack stack) {}
    @Override public int getVillagerXp() { return this.xp; }
    @Override public void overrideXp(int xp) { this.xp = xp; }
    @Override public boolean showProgressBar() { return false; }
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
        if (tag.contains("HomePosX")) {
            BlockPos saved = new BlockPos(
                    tag.getInt("HomePosX"),
                    tag.getInt("HomePosY"),
                    tag.getInt("HomePosZ"));
            // Ré-enregistrer dans la map globale au rechargement
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
            // A déjà un lit valide → y aller
            if (smelter.homePos != null && smelter.isBedStillValid()) return true;
            // Chercher un nouveau lit
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
            // On garde homePos — le Smelter se souvient de son lit
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
                // Si le lit a disparu entre-temps, tick() de SmelterEntity.tick()
                // s'en occupera dans les 20 ticks suivants
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

        /**
         * Scan manuel dans un rayon limité.
         * On ne scanne QUE les chunks déjà chargés (getBlockState est safe).
         * Coût réel : uniquement les blocs dans la zone chargée, exécuté
         * au maximum 1 fois toutes les 200 ticks — pas de lag.
         */
        private void findAndClaimBed() {
            BlockPos origin = smelter.blockPosition();
            Level level = smelter.level();
            int radius = SEARCH_RADIUS;

            BlockPos best = null;
            double bestDist = Double.MAX_VALUE;

            for (int dx = -radius; dx <= radius; dx += 1) {
                for (int dz = -radius; dz <= radius; dz += 1) {
                    for (int dy = -3; dy <= 3; dy++) {
                        BlockPos pos = origin.offset(dx, dy, dz);
                        // Vérifier chunk chargé pour éviter le force-load
                        if (!level.isLoaded(pos)) continue;
                        if (!level.getBlockState(pos).is(BlockTags.BEDS)) continue;
                        if (!isBedAvailable(pos)) continue;

                        double d = pos.distSqr(origin);
                        if (d < bestDist) {
                            bestDist = d;
                            best = pos.immutable();
                        }
                    }
                }
            }

            if (best != null) {
                smelter.claimBed(best);
            }
        }

        /** Vérifie si ce lit n'est pas déjà pris par un autre Smelter. */
        private boolean isBedAvailable(BlockPos pos) {
            UUID owner = CLAIMED_BEDS.get(pos);
            // Disponible si pas réclamé, ou réclamé par ce même Smelter
            return owner == null || owner.equals(smelter.getUUID());
        }
    }
}