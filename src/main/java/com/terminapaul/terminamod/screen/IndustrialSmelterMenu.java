package com.terminapaul.terminamod.screen;

import com.terminapaul.terminamod.ModItems;
import com.terminapaul.terminamod.ModMenuTypes;
import com.terminapaul.terminamod.block.entity.IndustrialSmelterBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class IndustrialSmelterMenu extends AbstractContainerMenu {

    private final IndustrialSmelterBlockEntity blockEntity;
    private final ContainerData data;

    public IndustrialSmelterMenu(int id, Inventory inv, IndustrialSmelterBlockEntity be) {
        super(ModMenuTypes.INDUSTRIAL_SMELTER.get(), id);
        this.blockEntity = be;

        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> be.getProgress();
                    case 1 -> be.getMaxProgress();
                    case 2 -> be.getEnergy();
                    case 3 -> be.getMaxEnergy();
                    case 4 -> be.getNuggetCount();
                    default -> 0;
                };
            }
            @Override
            public void set(int index, int value) {}
            @Override
            public int getCount() { return 5; }
        };

        addDataSlots(data);

        IItemHandler handler = be.getItemHandler();

        // Slot input nuggets (slot 0)
        addSlot(new SlotItemHandler(handler, 0, 44, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(ModItems.RUBY_NUGGET.get());
            }
        });

        // Slot output lingot (slot 1) - lecture seule
        addSlot(new SlotItemHandler(handler, 1, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) { return false; }
        });

        // Inventaire joueur
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // Barre rapide
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }
    }

    public IndustrialSmelterMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, (IndustrialSmelterBlockEntity) inv.player.level()
                .getBlockEntity(buf.readBlockPos()));
    }

    public int getProgress() { return data.get(0); }
    public int getMaxProgress() { return data.get(1); }
    public int getEnergy() { return data.get(2); }
    public int getMaxEnergy() { return data.get(3); }
    public int getNuggetCount() { return data.get(4); }

    @Override
    public boolean stillValid(Player player) { return true; }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemStack = stackInSlot.copy();
            if (index < 2) {
                if (!moveItemStackTo(stackInSlot, 2, slots.size(), true)) return ItemStack.EMPTY;
            } else {
                if (!moveItemStackTo(stackInSlot, 0, 1, false)) return ItemStack.EMPTY;
            }
            if (stackInSlot.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return itemStack;
    }
}