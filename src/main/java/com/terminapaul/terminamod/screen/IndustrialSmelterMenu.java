package com.terminapaul.terminamod.screen;

import com.terminapaul.terminamod.ModItems;
import com.terminapaul.terminamod.ModMenuTypes;
import com.terminapaul.terminamod.block.entity.IndustrialSmelterBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class IndustrialSmelterMenu extends AbstractContainerMenu {

    private final IndustrialSmelterBlockEntity blockEntity;
    private final ContainerLevelAccess access;

    private final DataSlot progress = DataSlot.standalone();
    private final DataSlot maxProgress = DataSlot.standalone();
    private final DataSlot energyLow = DataSlot.standalone();
    private final DataSlot energyHigh = DataSlot.standalone();
    private final DataSlot maxEnergyLow = DataSlot.standalone();
    private final DataSlot maxEnergyHigh = DataSlot.standalone();
    private final DataSlot nuggetCount = DataSlot.standalone();

    public IndustrialSmelterMenu(int id, Inventory inv, IndustrialSmelterBlockEntity be) {
        super(ModMenuTypes.INDUSTRIAL_SMELTER.get(), id);
        this.blockEntity = be;
        this.access = ContainerLevelAccess.create(be.getLevel(), be.getBlockPos());

        addDataSlot(progress);
        addDataSlot(maxProgress);
        addDataSlot(energyLow);
        addDataSlot(energyHigh);
        addDataSlot(maxEnergyLow);
        addDataSlot(maxEnergyHigh);
        addDataSlot(nuggetCount);

        IItemHandler handler = be.getItemHandler();

        // Slot input nuggets
        addSlot(new SlotItemHandler(handler, 0, 44, 40) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ModItems.RUBY_NUGGET.get());
            }
        });

        // Slot output lingot - lecture seule
        addSlot(new SlotItemHandler(handler, 1, 116, 40) {
            @Override
            public boolean mayPlace(ItemStack stack) { return false; }
        });

        // Inventaire joueur
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 118 + row * 18));
            }
        }

        // Barre rapide
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, 8 + col * 18, 176));
        }
    }

    public IndustrialSmelterMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, (IndustrialSmelterBlockEntity) inv.player.level()
                .getBlockEntity(buf.readBlockPos()));
    }

    @Override
    public void broadcastChanges() {
        // Côté serveur uniquement (blockEntity non null et niveau non client)
        if (blockEntity.getLevel() != null && !blockEntity.getLevel().isClientSide()) {
            progress.set(blockEntity.getProgress());
            maxProgress.set(blockEntity.getMaxProgress());

            int fe = blockEntity.getEnergy();
            energyLow.set(fe & 0xFFFF);
            energyHigh.set((fe >> 16) & 0xFFFF);

            int maxFe = blockEntity.getMaxEnergy();
            maxEnergyLow.set(maxFe & 0xFFFF);
            maxEnergyHigh.set((maxFe >> 16) & 0xFFFF);

            nuggetCount.set(blockEntity.getNuggetCount());
        }
        super.broadcastChanges();
    }

    public int getProgress() { return progress.get(); }
    public int getMaxProgress() { return maxProgress.get(); }
    public int getEnergy() { return (energyHigh.get() << 16) | (energyLow.get() & 0xFFFF); }
    public int getMaxEnergy() { return (maxEnergyHigh.get() << 16) | (maxEnergyLow.get() & 0xFFFF); }
    public int getNuggetCount() { return nuggetCount.get(); }

    @Override
    public boolean stillValid(Player player) {
        // Vérifie que le joueur est à moins de 8 blocs du block
        return stillValid(access, player, net.minecraft.world.level.block.Blocks.AIR) ||
                blockEntity.getBlockPos().distSqr(player.blockPosition()) < 64;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stackInSlot = slot.getItem();
        itemStack = stackInSlot.copy();

        if (index == 0) {
            // Slot input → inventaire joueur
            if (!moveItemStackTo(stackInSlot, 2, slots.size(), true)) return ItemStack.EMPTY;
        } else if (index == 1) {
            // Slot output lingot → inventaire joueur
            if (!moveItemStackTo(stackInSlot, 2, slots.size(), true)) return ItemStack.EMPTY;
        } else {
            // Inventaire joueur → slot input nuggets seulement
            if (stackInSlot.is(ModItems.RUBY_NUGGET.get())) {
                if (!moveItemStackTo(stackInSlot, 0, 1, false)) return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (stackInSlot.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        return itemStack;
    }
}