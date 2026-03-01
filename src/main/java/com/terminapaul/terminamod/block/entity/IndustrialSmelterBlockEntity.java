package com.terminapaul.terminamod.block.entity;

import com.terminapaul.terminamod.ModBlockEntities;
import com.terminapaul.terminamod.ModItems;
import com.terminapaul.terminamod.screen.IndustrialSmelterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IndustrialSmelterBlockEntity extends BlockEntity implements MenuProvider {

    public static final int FE_CAPACITY = 10000;
    public static final int FE_PER_TICK = 20;
    public static final int TICKS_TO_CONVERT = 200;
    public static final int NUGGETS_NEEDED = 64;

    // TEMPORAIRE : mettre à false une fois que tu as un mod FE
    public static final boolean DEBUG_INFINITE_FE = true;

    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (slot == 0) return stack.is(ModItems.RUBY_NUGGET.get());
            return false;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final EnergyStorage energyStorage = new EnergyStorage(FE_CAPACITY, FE_CAPACITY, FE_CAPACITY, 0) {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = super.receiveEnergy(maxReceive, simulate);
            if (!simulate) setChanged();
            return received;
        }
    };

    private int progress = 0;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<EnergyStorage> lazyEnergyStorage = LazyOptional.empty();

    public IndustrialSmelterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INDUSTRIAL_SMELTER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, IndustrialSmelterBlockEntity be) {
        if (level.isClientSide()) return;

        if (DEBUG_INFINITE_FE) {
            be.energyStorage.extractEnergy(be.energyStorage.getEnergyStored(), true);
            be.energyStorage.receiveEnergy(be.energyStorage.getMaxEnergyStored(), true);
        }

        boolean canProcess = be.canProcess();

        if (canProcess && be.energyStorage.getEnergyStored() >= FE_PER_TICK) {
            be.energyStorage.extractEnergy(FE_PER_TICK, false);
            be.progress++;

            if (be.progress >= TICKS_TO_CONVERT) {
                be.doConversion();
                be.progress = 0;
            }
            be.setChanged();
        } else if (!canProcess && be.progress > 0) {
            be.progress = 0;
            be.setChanged();
        }
    }

    private boolean canProcess() {
        ItemStack input = itemHandler.getStackInSlot(0);
        if (input.isEmpty() || !input.is(ModItems.RUBY_NUGGET.get())) return false;
        if (input.getCount() < NUGGETS_NEEDED) return false;

        ItemStack output = itemHandler.getStackInSlot(1);
        if (output.isEmpty()) return true;
        if (!output.is(ModItems.RUBY_INGOT.get())) return false;
        return output.getCount() < output.getMaxStackSize();
    }

    private void doConversion() {
        itemHandler.extractItem(0, NUGGETS_NEEDED, false);
        ItemStack output = itemHandler.getStackInSlot(1);
        if (output.isEmpty()) {
            itemHandler.setStackInSlot(1, new ItemStack(ModItems.RUBY_INGOT.get(), 1));
        } else {
            output.grow(1);
        }
    }

    public void dropContents(Level level, BlockPos pos) {
        SimpleContainer inv = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inv.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(level, pos, inv);
    }

    public int getProgress() { return progress; }
    public int getMaxProgress() { return TICKS_TO_CONVERT; }
    public int getEnergy() { return energyStorage.getEnergyStored(); }
    public int getMaxEnergy() { return FE_CAPACITY; }
    public int getNuggetCount() { return itemHandler.getStackInSlot(0).getCount(); }
    public ItemStackHandler getItemHandler() { return itemHandler; }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.terminamod.industrial_smelter");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new IndustrialSmelterMenu(id, inventory, this);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return lazyItemHandler.cast();
        if (cap == ForgeCapabilities.ENERGY) return lazyEnergyStorage.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyStorage = LazyOptional.of(() -> energyStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyStorage.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("energy", energyStorage.getEnergyStored());
        tag.putInt("progress", progress);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        energyStorage.receiveEnergy(tag.getInt("energy"), false);
        progress = tag.getInt("progress");
    }
}