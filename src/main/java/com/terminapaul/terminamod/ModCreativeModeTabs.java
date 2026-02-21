package com.terminapaul.terminamod;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TerminaMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TERMINA_TAB = CREATIVE_MODE_TABS.register("termina_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.terminamod"))
                    .icon(() -> ModItems.RUBY.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.RUBY.get());
                        output.accept(ModItems.RUBY_BLOCK_ITEM.get());
                        output.accept(ModItems.RUBY_ORE_ITEM.get());
                        output.accept(ModItems.DEEPSLATE_RUBY_ORE_ITEM.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}