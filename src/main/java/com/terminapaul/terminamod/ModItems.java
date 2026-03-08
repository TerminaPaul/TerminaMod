package com.terminapaul.terminamod;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TerminaMod.MOD_ID);

    public static final RegistryObject<Item> RUBY = ITEMS.register("ruby",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RUBY_INGOT = ITEMS.register("ruby_ingot",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RUBY_NUGGET = ITEMS.register("ruby_nugget",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> IRON_STICK = ITEMS.register("iron_stick",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> OBSIDIAN_HAMMER = ITEMS.register("obsidian_hammer",
            () -> new ObsidianHammerItem(new Item.Properties().durability(64)));

    public static final RegistryObject<Item> RUBY_BLOCK_ITEM = ITEMS.register("ruby_block",
            () -> new BlockItem(ModBlocks.RUBY_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> RUBY_ORE_ITEM = ITEMS.register("ruby_ore",
            () -> new BlockItem(ModBlocks.RUBY_ORE.get(), new Item.Properties()));

    public static final RegistryObject<Item> DEEPSLATE_RUBY_ORE_ITEM = ITEMS.register("deepslate_ruby_ore",
            () -> new BlockItem(ModBlocks.DEEPSLATE_RUBY_ORE.get(), new Item.Properties()));

    public static final RegistryObject<Item> INDUSTRIAL_SMELTER_ITEM = ITEMS.register("industrial_smelter",
            () -> new BlockItem(ModBlocks.INDUSTRIAL_SMELTER.get(), new Item.Properties()));

    public static final RegistryObject<Item> SMELTER_SPAWN_EGG = ITEMS.register("smelter_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.SMELTER, 0x4A2F1A, 0xFF8C00,
                    new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}