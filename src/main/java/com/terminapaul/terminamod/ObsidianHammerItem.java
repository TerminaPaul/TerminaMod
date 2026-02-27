package com.terminapaul.terminamod;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class ObsidianHammerItem extends Item {

    public ObsidianHammerItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment == Enchantments.UNBREAKING
                || enchantment == Enchantments.MENDING
                || enchantment == Enchantments.BLOCK_FORTUNE;
    }
}