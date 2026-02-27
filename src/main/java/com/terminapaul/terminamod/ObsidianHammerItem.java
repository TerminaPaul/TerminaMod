package com.terminapaul.terminamod;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class ObsidianHammerItem extends Item {

    public ObsidianHammerItem(Properties properties) {
        super(properties);
    }

    private boolean isAllowed(Enchantment enchantment) {
        return enchantment == Enchantments.UNBREAKING
                || enchantment == Enchantments.MENDING
                || enchantment == Enchantments.BLOCK_FORTUNE;
    }

    @Override
    public int getEnchantmentValue() {
        return 10;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return isAllowed(enchantment);
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return EnchantmentHelper.getEnchantments(book).keySet().stream()
                .allMatch(this::isAllowed);
    }
}